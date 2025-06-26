# agent/scheduler.py  – mock Buffer wrapper
class BufferScheduler:
    def schedule(self, post, when):
        # TODO: Replace with real Buffer API call
        print(f"[MOCK] Would schedule at {when}: {post['content'][:80]}…")
