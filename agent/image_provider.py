import os, abc, requests


class ImageProvider(abc.ABC):
    @abc.abstractmethod
    def search(self, query: str, page: int, per_page: int) -> list[str]: ...


class UnsplashProvider(ImageProvider):
    def __init__(self):
        self.access_key = os.getenv("ACCESS_KEY")

    def search(self, query: str, page=1, per_page=5) -> list[dict]:
        url = self.__get_url(query, page, per_page)
        headers = {"Authorization": f"Client-ID {self.access_key}"}
        response = requests.get(url, headers=headers)
        body = response.json()
        results = body["results"]
        images = []
        for result in results:
            images.append({"id": result["id"], "url": result["urls"]["raw"]})

        return images

    def __get_url(self, query: str, page: int, per_page: int):
        return f"https://api.unsplash.com/search/photos?query={query}&page={page}&per_page={per_page}"
