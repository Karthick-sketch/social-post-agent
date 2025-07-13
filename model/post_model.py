from pydantic import BaseModel
from enum import Enum


class Platform(Enum):
    LINKEDIN = "linkedin"
    INSTAGRAM = "instagram"
    TWITTER = "twitter"


class PostModel(BaseModel):
    id_: int
    linkedin: str
    instagram: str
    twitter: str


class GeneratePostModel(BaseModel):
    brief: str
    brand: str
    tone: str
    platforms: list[Platform]


class ScheduleModel(BaseModel):
    post_id: int
    when: str
    platforms: list[Platform]
