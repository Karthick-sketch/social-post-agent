# agent/scheduler.py  – mock Buffer wrapper
from ast import literal_eval
from model.post_model import Platform

class BufferScheduler:
    def schedule(self, post, when, platforms: list[Platform]):
        content = literal_eval(post["content"])
        # TODO: Replace with real Buffer API call
        print(f"[MOCK] Would schedule at {when}")
        for platform in platforms:
            print(f"{platform.value.capitalize}: {content[platform.value]}")
