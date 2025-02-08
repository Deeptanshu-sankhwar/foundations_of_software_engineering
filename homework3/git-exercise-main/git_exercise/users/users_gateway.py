from git_exercise.users.user import User


class UsersGateway:
    users: list[User]
    
    def __init__(self):
        self.users = [
            User(id=1, name="Fred Derf", email="fred.derf@gmail.com", is_admin=False),
            User(id=2, name="Mary Yram", email="mary.yram@gmail.com", is_admin=False),
            User(id=3, name="Jane Enaj", email="jena.enaj@gmail.com", is_admin=False),
            User(id=4, name="John Nhoj", email="john.nhoj@gmail.com", is_admin=True),
        ]
    
    def find(self, user_id: int) -> User | None:
        for user in self.users:
            if user.id == user_id:
                return user
        
        return None
    
    def list(self):
        return self.users
    
    def create(self, name: str, email: str, is_admin: bool = False) -> int:
        new_id = max(user.id for user in self.users) + 1
        new_user = User(id=new_id, name=name, email=email, is_admin=is_admin)
        self.users.append(new_user)
        return new_id
    
    def update(self, user_id: int, name: str, email: str, is_admin: bool) -> dict | None:
        for user in self.users:
            if user.id == user_id:
                user.name = name
                user.email = email
                user.is_admin = is_admin
                return user.__dict__
        return None