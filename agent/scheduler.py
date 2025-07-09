# agent/scheduler.py  – mock Buffer wrapper
import os, requests
from abc import ABC, abstractmethod
from ast import literal_eval
from model.post_model import Platform


class Scheduler(ABC):
    @abstractmethod
    def schedule(self, post: dict, when: str, platforms: list[Platform]): ...


class AryshareScheduler(Scheduler):
    def __init__(self):
        self.url = "https://api.ayrshare.com/api/post"

    def schedule(self, post: dict, when: str, platforms: list[Platform]):
        content = literal_eval(post["content"])
        images = literal_eval(post["images"])
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
