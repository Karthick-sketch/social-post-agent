# agent/llm_provider.py
import os, abc, requests


class LLMProvider(abc.ABC):
    @abc.abstractmethod
    def chat(self, messages: list[dict], **kwargs) -> str: ...


class OpenAIProvider(LLMProvider):
    def __init__(self, model: str = "gpt-4o-mini"):
        from openai import OpenAI  # official SDK

        self.client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))
        self.model = model

    def chat(self, messages, **kwargs):
        resp = self.client.chat.completions.create(
            model=self.model, messages=messages, **kwargs  # type: ignore
        )  # type: ignore
        return resp.choices[0].message.content


class DeepSeekProvider(LLMProvider):
    def __init__(self, model: str = "deepseek-chat"):
        self.api_key = os.getenv("DEEPSEEK_API_KEY")
        self.model = model
        self.url = "https://openrouter.ai/api/v1/chat/completions"

    def chat(self, messages, **kwargs):
        payload = {"model": self.model, "messages": messages, **kwargs}
        headers = {
            "Authorization": f"Bearer {self.api_key}",
            "Content-Type": "application/json",
        }
        resp = requests.post(self.url, json=payload, headers=headers, timeout=60)
        resp.raise_for_status()
        return resp.json()["choices"][0]["message"]["content"]


# --- runtime switch ---
provider_name = os.getenv("LLM_PROVIDER", "openai")
provider_cls = {
    "openai": OpenAIProvider,
    "deepseek": DeepSeekProvider,
}[provider_name]
llm = provider_cls()
