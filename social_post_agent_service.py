# service.py  – simple CLI demo
from ast import literal_eval
from agent.llm_provider import OpenAIProvider, DeepSeekProvider
from agent.post_agent import SocialPostAgent
from model.post_model import Platform


class SocialPostAgentService:
    def __init__(self, llm_provider: str, model: str):
        self.agent = self.__get_agent(llm_provider, model)

    def __get_agent(self, llm_provider: str, model: str):
        if llm_provider == "openai":
            llm = OpenAIProvider(model=model)
        else:
            llm = DeepSeekProvider(model=model)
        return SocialPostAgent(llm)

    def generate_post(
        self, brief: str, platforms: list[Platform], brand: str, tone: str
    ) -> dict:
        id_, draft = self.agent.create_post(brief, platforms, brand, tone)
        post = self.__convert_post(draft)
        hashtags = post.get("hashtags", [])
        return {
            "id": id_,
            "linkedin": self.__merge_hastags(hashtags, post.get("linkedin", "")),
            "twitter": self.__merge_hastags(hashtags, post.get("twitter", "")),
            "instagram": self.__merge_hastags(hashtags, post.get("instagram", "")),
        }

    def suggest_images(self, id_: int, page: int) -> dict:
        image_url = self.agent.suggest_image(id_, page)
        return {"image": image_url}

    def schedule_post(self, post_id: int, when: str, platforms: list[Platform]) -> bool:
        return self.agent.schedule(post_id, when, platforms)

    def __convert_post(self, draft: str) -> dict:
        if draft.startswith("```json\n") and draft.endswith("\n```"):
            draft = draft[8:-4]
            post = literal_eval(draft)
            if isinstance(post, dict):
                return post
        return {}

    def __merge_hastags(self, hashtags: list[str], post: str) -> str:
        if post:
            return (
                post
                + " "
                + " ".join(
                    tag if tag.startswith("#") else f"#{tag}" for tag in hashtags
                )
            )
        return ""
