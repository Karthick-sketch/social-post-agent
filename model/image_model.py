from pydantic import BaseModel


class ImageModel(BaseModel):
    id_: int
    url: str
    selected: bool = False
