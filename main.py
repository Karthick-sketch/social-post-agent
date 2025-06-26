# main.py  – simple CLI demo
import os
from agent.llm_provider import OpenAIProvider, DeepSeekProvider
from agent.post_agent import SocialPostAgent


def main():
    llm = (DeepSeekProvider()
           if os.getenv("LLM_PROVIDER") == "deepseek"
           else OpenAIProvider())

    agent = SocialPostAgent(llm)

    brief = input("Describe the announcement in one sentence:\n> ")
    id_, draft = agent.create_post(brief)
    print("\n----- DRAFT -----\n", draft, "\n-----------------")

    choice = input("Approve? (y/n) ")
    if choice.lower().startswith("y"):
        when = input("Schedule (ISO datetime, e.g. 2025-07-01T10:00): ")
        agent.schedule(id_, when)
        print("✅  Scheduled!")
    else:
        print("❌  Discarded.")


if __name__ == "__main__":
    main()
