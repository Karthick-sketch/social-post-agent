from pydantic import BaseModel
from typing import Any


class ImageModel(BaseModel):
    id_: int
    url: str
    selected: bool

    def __init__(self, id_: int, url: str, **data: Any):
        super().__init__(**data)
        self.id_ = id_
        self.url = url
        self.selected = False
