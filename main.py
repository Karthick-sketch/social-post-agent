from fastapi import FastAPI
from model.post_model import GeneratePostModel, ScheduleModel
import service

app = FastAPI()


@app.post("/generate-post")
async def generate_post(model: GeneratePostModel):
    print(model)
    return service.generate_post(model.brief, model.llm_provider, model.platforms)


@app.post("/schedule")
async def schedule_post(model: ScheduleModel):
    service.schedule_post(model.post_id, model.when, model.llm_provider, model.platforms)
