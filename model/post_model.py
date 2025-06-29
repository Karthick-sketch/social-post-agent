from pydantic import BaseModel
from enum import Enum


class Platform(Enum):
    LINKEDIN = "linkedin"
    INSTAGRAM = "instagram"
    TWITTER = "twitter"


class PostModel(BaseModel):
    llm_provider: str
    platforms: list[Platform]


class GeneratePostModel(PostModel):
    brief: str


class ScheduleModel(PostModel):
    post_id: int
    when: str
