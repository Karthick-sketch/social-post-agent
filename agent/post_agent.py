# agent/post_agent.py
from enum import Enum, auto
from agent.prompts import BASE_SYSTEM, IMAGE_PROMPT
from agent.scheduler import AryshareScheduler
from agent.db import DB
from agent.image_provider import UnsplashProvider
from model.image_model import ImageModel
from model.post_model import Platform, PostModel, ScheduleModel


class Status(Enum):
    DRAFT = auto()
    APPROVED = auto()
    SCHEDULED = auto()


class SocialPostAgent:
    def __init__(self, llm, image_provider=None, scheduler=None, db=None):
        self.llm = llm
        self.image_provider = image_provider or UnsplashProvider()
        self.scheduler = scheduler or AryshareScheduler()
        self.db = db or DB("posts.sqlite")

    # 1. Generate captions + hashtags
    def create_post(
            self, brief: str, platforms: list[Platform], brand: str, tone="friendly"
    ) -> tuple[int, str]:
        platforms_str = ", ".join(p.value for p in platforms)
        system = BASE_SYSTEM.format(brand=brand, tone=tone, platforms=platforms_str)
        messages = [
            {"role": "system", "content": system},
            {"role": "user", "content": brief},
        ]
        print("Generating Post text...")
        content = self.llm.chat(messages, temperature=0.7)
        record_id = self.db.insert_post(brief, content, platforms_str, Status.DRAFT.name)
        return record_id, content

    def save_post(self, model: PostModel) -> None:
        user_content = (
                '{"linkedin": "'
                + model.linkedin
                + '", "instagram": "'
                + model.instagram
                + '", "twitter": "'
                + model.twitter
                + '"}'
        )
        user_content = user_content.replace("\n", "\\n")
        self.db.update_user_content(model.id_, user_content)

    def save_post_images(self, post_id: int, model: list[ImageModel]) -> None:
        images = []
        for image in model:
            images.append(
                f'{{"id_": {image.id_}, "url": "{image.url}", "selected": {"true" if image.selected else "false"}}}')
        images_str = f'[{",".join(images)}]'
        self.db.update_images(post_id, images_str)

    # 2. Suggest Unsplash image
    def suggest_image(self, post_id: int, page: int) -> list[dict]:
        print("Generating Post images...")
        post = self.db.get(post_id)
        query = self.llm.chat(
            [
                {
                    "role": "user",
                    "content": IMAGE_PROMPT.format(post_text=post["content"]),
                }
            ],
            temperature=0.5,
            max_tokens=20,
        )
        return self.image_provider.search(query, page)

    def selected_platforms(self, post_id: int) -> list[Platform]:
        post = self.db.get(post_id)
        platforms = self.__get_platforms(post["platforms"])
        return platforms

    # 3. Schedule after human approval
    def schedule_post(self, post_id: int, model: ScheduleModel) -> bool:
        post = self.db.get(post_id)
        when = f'{model.date}T{model.time}'
        platforms = self.__get_platforms(post["platforms"])
        self.scheduler.schedule(post, when, platforms)
        self.db.update_schedule(post_id, when)
        self.db.update_status(post_id, Status.SCHEDULED.name)
        return True

    @staticmethod
    def __get_platforms(platforms_str: str) -> list[Platform]:
        platforms_str = platforms_str.split(",")
        platforms = []
        for p in platforms_str:
            platforms.append(Platform[p.strip().upper()])
        return platforms
