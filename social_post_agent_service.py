# service.py  – simple CLI demo
import json
from agent.llm_provider import OpenAIProvider, DeepSeekProvider
from agent.post_agent import SocialPostAgent
from model.image_model import ImageModel
from model.post_model import Platform, PostModel, ScheduleModel


class SocialPostAgentService:
    def __init__(self, llm_provider: str, model: str):
        self.agent = SocialPostAgent(
            OpenAIProvider(model=model) if llm_provider == "openai" else DeepSeekProvider(model=model))

    def generate_post(
            self, brief: str, platforms: list[Platform], brand: str, tone: str
    ) -> PostModel:
        id_, draft = self.agent.create_post(brief, platforms, brand, tone)
        post = self.__convert_post(draft)
        hashtags = post.get("hashtags", [])
        post_model = PostModel(
            id_=id_,
            linkedin=self.__merge_hashtags(hashtags, post.get("linkedin", "")),
            instagram=self.__merge_hashtags(hashtags, post.get("instagram", "")),
            twitter=self.__merge_hashtags(hashtags, post.get("twitter", "")),
        )
        return post_model

    def save_post(self, model: PostModel) -> None:
        self.agent.save_post(model)

    def save_post_images(self, post_id: int, model: list[ImageModel]) -> None:
        self.agent.save_post_images(post_id, model)

    def suggest_images(self, post_id: int, page: int) -> list[dict]:
        return self.agent.suggest_image(post_id, page)

    def selected_platforms(self, post_id: int) -> list[Platform]:
        return self.agent.selected_platforms(post_id)

    def schedule_post(self, post_id: int, model: ScheduleModel) -> bool:
        return self.agent.schedule_post(post_id, model)

    @staticmethod
    def __convert_post(draft: str) -> dict:
        draft = draft.replace("```json\n", '').replace("\n```", '').strip()
        post = json.loads(draft)
        if isinstance(post, dict):
            return post
        return {}

    @staticmethod
    def __merge_hashtags(hashtags: list[str], post: str) -> str:
        if post:
            return (
                    post
                    + " "
                    + " ".join(
                tag if tag.startswith("#") else f"#{tag}" for tag in hashtags
            )
            )
        return ""
