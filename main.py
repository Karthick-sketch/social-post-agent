import os

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from model.image_model import ImageModel
from model.post_model import GeneratePostModel, PostModel, ScheduleModel
from social_post_agent_service import SocialPostAgentService

LLM_PROVIDER = os.getenv("LLM_PROVIDER", "deepseek")
MODEL = os.getenv("MODEL", "deepseek/deepseek-r1-0528:free")

app = FastAPI()
service = SocialPostAgentService(LLM_PROVIDER, MODEL)

origins = ["http://localhost:4200"]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


@app.post("/generate-post")
async def generate_post(model: GeneratePostModel) -> PostModel:
    return service.generate_post(model.brief, model.platforms, model.brand, model.tone)


@app.put("/save-post")
async def save_post(model: PostModel) -> None:
    (service.save_post(model))


@app.put("/save-post-images/{post_id}")
async def save_post_images(post_id: int, model: list[ImageModel]) -> None:
    service.save_post_images(post_id, model)


@app.get("/suggest-images/{post_id}")
async def suggest_images(post_id: int, page: int = 1) -> list[dict]:
    return service.suggest_images(post_id, page)


@app.post("/schedule")
async def schedule_post(model: ScheduleModel) -> bool:
    return service.schedule_post(model.post_id, model.when, model.platforms)
