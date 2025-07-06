import os
from fastapi import FastAPI
from model.post_model import GeneratePostModel, ScheduleModel
from social_post_agent_service import SocialPostAgentService

LLM_PROVIDER = os.getenv("LLM_PROVIDER", "deepseek")
MODEL = os.getenv("MODEL", "deepseek/deepseek-r1-0528:free")

app = FastAPI()
service = SocialPostAgentService(LLM_PROVIDER, MODEL)


@app.post("/generate-post")
async def generate_post(model: GeneratePostModel):
    return service.generate_post(model.brief, model.platforms, model.brand, model.tone)


@app.get("/suggest-images")
async def suggest_images(query: str, page: int):
    return service.suggest_images(query, page)


@app.post("/schedule")
async def schedule_post(model: ScheduleModel):
    service.schedule_post(model.post_id, model.when, model.platforms)
