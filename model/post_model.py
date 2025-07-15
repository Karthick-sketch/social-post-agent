from pydantic import BaseModel
from enum import Enum


class Platform(Enum):
    LINKEDIN = "LinkedIn"
    INSTAGRAM = "Instagram"
    TWITTER = "Twitter"


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
    date: str
    time: str
