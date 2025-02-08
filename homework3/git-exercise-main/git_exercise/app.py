from flask import Flask, jsonify

from git_exercise.users.users_api import users_api
from git_exercise.users.users_gateway import UsersGateway


def create_app() -> Flask:
    app = Flask(__name__)

    @app.route("/")
    def index():
        return jsonify({"app": "Git Exercise"})

    @app.route("/health")
    def health():
        return jsonify({"status": "UP"})

    users_gateway = UsersGateway()
    app.register_blueprint(users_api(users_gateway))

    return app
