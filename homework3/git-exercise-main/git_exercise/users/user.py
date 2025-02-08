from dataclasses import dataclass


@dataclass
class User:
    id: int
    name: str
    email: str
    is_admin: bool = False
