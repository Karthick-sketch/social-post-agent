# agent/post_agent.py
from enum import Enum, auto
from agent.prompts import BASE_SYSTEM, IMAGE_PROMPT
from agent.scheduler import BufferScheduler
from agent.db import DB


class Status(Enum):
    DRAFT     = auto()
    APPROVED  = auto()
    SCHEDULED = auto()


class SocialPostAgent:
    def __init__(self, llm, scheduler=None, db=None):
        self.llm        = llm
        self.scheduler  = scheduler or BufferScheduler()
        self.db         = db or DB("posts.sqlite")

    # 1. Generate captions + hashtags
    def create_post(self, brief: str, brand="ACME", tone="friendly"):
        system = BASE_SYSTEM.format(brand=brand, tone=tone)
        messages = [
            {"role": "system", "content": system},
            {"role": "user",   "content": brief}
        ]
        content = self.llm.chat(messages, temperature=0.7)
        record_id = self.db.insert_post(brief, content, Status.DRAFT.name)
        return record_id, content

    # 2. Suggest Unsplash image
    def suggest_image(self, post_text: str):
        query = self.llm.chat(
            [{"role": "user",
              "content": IMAGE_PROMPT.format(post_text=post_text)}],
            temperature=0.5,
            max_tokens=20
        )
        return f"https://source.unsplash.com/1600x900/?{query}"

    # 3. Schedule after human approval
    def schedule(self, record_id: int, when: str):
        post = self.db.get(record_id)
        self.scheduler.schedule(post, when)
        self.db.update_status(record_id, Status.SCHEDULED.name)
