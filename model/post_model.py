from pydantic import BaseModel
from enum import Enum


class Platform(Enum):
    LINKEDIN = "linkedin"
    INSTAGRAM = "instagram"
    TWITTER = "twitter"


class PostModel(BaseModel):
    platforms: list[Platform]


class GeneratePostModel(PostModel):
    brief: str
    brand: str
    tone: str


class ScheduleModel(PostModel):
    post_id: int
    when: str
