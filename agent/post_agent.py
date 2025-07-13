# agent/post_agent.py
from enum import Enum, auto
from agent.prompts import BASE_SYSTEM, IMAGE_PROMPT
from agent.scheduler import AryshareScheduler
from agent.db import DB
from agent.image_provider import UnsplashProvider
from model.post_model import Platform, PostModel


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
        record_id = self.db.insert_post(brief, content, Status.DRAFT.name)
        return record_id, content


    def save_post(self, model: PostModel) -> None:
        print("ID: " + str(model.id_))
        user_content = (
                '{"linkedin": "'
                + model.linkedin
                + '", "instagram": "'
                + model.instagram
                + '", "twitter": "'
                + model.twitter
                + '"}'
        )
        self.db.update_user_content(model.id_, user_content)

    # 2. Suggest Unsplash image
    def suggest_image(self, id_: int, page: int) -> list[dict]:
        print("Generating Post images...")
        post = self.db.get(id_)
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

    # 3. Schedule after human approval
    def schedule(self, record_id: int, when: str, platforms: list[Platform]) -> bool:
        post = self.db.get(record_id)
        self.scheduler.schedule(post, when, platforms)
        self.db.update_status(record_id, Status.SCHEDULED.name)
        return True
