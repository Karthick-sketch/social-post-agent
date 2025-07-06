# service.py  – simple CLI demo
from agent.llm_provider import OpenAIProvider, DeepSeekProvider, QwenProvider
from agent.post_agent import SocialPostAgent
from model.post_model import Platform


class SocialPostAgentService:
    def __init__(self, llm_provider: str, model: str):
        self.agent = self.__get_agent(llm_provider, model)

    def __get_agent(self, llm_provider: str, model: str):
        if llm_provider == "deepseek":
            llm = DeepSeekProvider(model=model)
        elif llm_provider == "openai":
            llm = OpenAIProvider(model=model)
        else:
            llm = QwenProvider(model=model)
        return SocialPostAgent(llm)

    def generate_post(
        self, brief: str, platforms: list[Platform], brand: str, tone: str
    ):
        id_, draft = self.agent.create_post(brief, platforms, brand, tone)
        return {"id": id_, "post": draft}

    def suggest_images(self, query: str, page: int):
        image_url = self.agent.suggest_image(query, page)
        return {"image": image_url}

    def schedule_post(self, post_id: int, when: str, platforms: list[Platform]):
        self.agent.schedule(post_id, when, platforms)
