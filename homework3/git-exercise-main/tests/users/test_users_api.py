import unittest

from git_exercise.users.users_api import users_api
from git_exercise.users.users_gateway import UsersGateway
from tests.blueprint_test_support import test_client


class TestUsersApi(unittest.TestCase):
    def setUp(self):
        gateway = UsersGateway()
        self.client = test_client(users_api(gateway))

    def test_list(self):
        response = self.client.get("/users")

        self.assertEqual(200, response.status_code)
        self.assertIn({"id": 1, "name": "Fred Derf", "email": "fred.derf@gmail.com", "is_admin": False}, response.json)

    def test_find(self):
        response = self.client.get("/users/2")

        self.assertEqual(200, response.status_code)
        self.assertEqual({"id": 2, "name": "Mary Yram", "email": "mary.yram@gmail.com", "is_admin": False}, response.json)

    def test_find_not_found(self):
        response = self.client.get("/users/2345")

        self.assertEqual(404, response.status_code)

    def test_create_user(self):
        response = self.client.post(
            "/users",
            json={"name": "Kate Etak", "email": "kate.etak@gmail.com", "is_admin": True}
        )

        self.assertEqual(201, response.status_code)
        self.assertEqual({"id": 5}, response.json)

        find_response = self.client.get("/users/5")
        self.assertEqual(200, find_response.status_code)
        self.assertEqual(
            {"id": 5, "name": "Kate Etak", "email": "kate.etak@gmail.com", "is_admin": True},
            find_response.json
        )

    def test_update_user(self):
        response = self.client.put(
            "/users/2",
            json={"name": "Paul Luap", "email": "paul.luap@gmail.com", "is_admin": True}
        )

        self.assertEqual(200, response.status_code)
        self.assertEqual(
            {"id": 2, "name": "Paul Luap", "email": "paul.luap@gmail.com", "is_admin": True},
            response.json
        )

        find_response = self.client.get("/users/2")
        self.assertEqual(200, find_response.status_code)
        self.assertEqual(
            {"id": 2, "name": "Paul Luap", "email": "paul.luap@gmail.com", "is_admin": True},
            find_response.json
        )


    def test_update_user_not_found(self):
        response = self.client.put(
            "/users/9999",
            json={"name": "Unknown User", "email": "unknown@gmail.com", "is_admin": False}
        )

        self.assertEqual(404, response.status_code)
