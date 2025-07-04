# agent/post_agent.py
from enum import Enum, auto
from agent.prompts import BASE_SYSTEM, IMAGE_PROMPT
from agent.scheduler import BufferScheduler
from agent.db import DB
from agent.image_provider import UnsplashProvider
from model.post_model import Platform


class Status(Enum):
    DRAFT = auto()
    APPROVED = auto()
    SCHEDULED = auto()


class SocialPostAgent:
    def __init__(self, llm, image_provider=None, scheduler=None, db=None):
        self.llm = llm
        self.image_provider = image_provider or UnsplashProvider()
        self.scheduler = scheduler or BufferScheduler()
        self.db = db or DB("posts.sqlite")

    # 1. Generate captions + hashtags
    def create_post(
        self, brief: str, platforms: list[Platform], brand: str, tone="friendly"
    ):
        platforms_str = ", ".join(p.value for p in platforms)
        system = BASE_SYSTEM.format(brand=brand, tone=tone, platforms=platforms_str)
        messages = [
            {"role": "system", "content": system},
            {"role": "user", "content": brief},
        ]
        print("Generating Post text...")
        content = self.llm.chat(messages, temperature=0.7)
        content = self.__remove_obstacles(content)
        record_id = self.db.insert_post(brief, content, Status.DRAFT.name)
        return record_id, content

    # 2. Suggest Unsplash image
    def suggest_image(self, post_text: str, page: int):
        print("Generating Post images...")
        query = self.llm.chat(
            [{"role": "user", "content": IMAGE_PROMPT.format(post_text=post_text)}],
            temperature=0.5,
            max_tokens=20,
        )
        query = self.__remove_obstacles(query)
        return self.image_provider.search(query, page)

    # 3. Schedule after human approval
    def schedule(self, record_id: int, when: str, platforms: list[Platform]):
        post = self.db.get(record_id)
        self.scheduler.schedule(post, when, platforms)
        self.db.update_status(record_id, Status.SCHEDULED.name)

    def __remove_obstacles(self, content: str):
        return content.replace("\n", "").replace("<think>", "").replace("</think>", "")
