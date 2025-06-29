# service.py  – simple CLI demo
from agent.llm_provider import OpenAIProvider, DeepSeekProvider, QwenProvider
from agent.post_agent import SocialPostAgent
from model.post_model import Platform


def __get_agent(llm_provider: str):
    if llm_provider == "deepseek":
        llm = DeepSeekProvider()
    elif llm_provider == "openai":
        llm = OpenAIProvider()
    else:
        llm = QwenProvider()

    return SocialPostAgent(llm)


def generate_post(brief: str, llm_provider: str, platforms: list[Platform]):
    agent = __get_agent(llm_provider)
    id_, draft = agent.create_post(brief, platforms)
    image_url = agent.suggest_image(brief)

    return {"id": id_, "post": draft, "image": image_url}


def schedule_post(post_id: int, when: str, llm_provider: str, platforms: list[Platform]):
    agent = __get_agent(llm_provider)
    agent.schedule(post_id, when, platforms)
