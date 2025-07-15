# agent/scheduler.py  – mock Buffer wrapper
# import os, requests
import json
from abc import ABC, abstractmethod
from model.post_model import Platform


class Scheduler(ABC):
    @abstractmethod
    def post(self, post: dict, platforms: list[Platform]): ...

    @abstractmethod
    def schedule(self, post: dict, when: str, platforms: list[Platform]): ...


class AryshareScheduler(Scheduler):
    def __init__(self):
        self.url = "https://api.ayrshare.com/api/post"

    def post(self, post: dict, platforms: list[Platform]):
        pass

    def schedule(self, post: dict, when: str, platforms: list[Platform]):
        content = json.loads(post["user_content"])
        images = json.loads(post["images"])
        print(content)
        print(images)
        # headers = {
        #     "Authorization": f"Bearer {os.getenv('AYRSHARE_API_KEY')}",
        #     "Content-Type": "application/json",
        # }
        # for platform in platforms:
        #     print(content[platform.value])
        #     payload = {
        #         "post": content[platform.value],
        #         "platforms": [platform.value],
        #         "mediaUrls": images,
        #         "scheduleDate": when,
        #     }
        #     response = requests.post(self.url, json=payload, headers=headers)
        #     print(response.json())
