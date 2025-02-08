from flask import Blueprint, abort, jsonify, request

from git_exercise.users.users_gateway import UsersGateway


def users_api(users_gateway: UsersGateway) -> Blueprint:
    api = Blueprint("users_api", __name__)

    @api.route("/users")
    def list_all():
        return jsonify(users_gateway.list())
    
    @api.route("/users", methods=["POST"])
    def create():
        data = request.get_json()
        if not data or "name" not in data or "email" not in data:
            abort(400) 

        is_admin = data.get("is_admin", False)  
        user_id = users_gateway.create(data["name"], data["email"], is_admin)
        
        return jsonify({"id": user_id}), 201
    
    @api.route("/users/<int:user_id>")
    def find(user_id: int):
        user = users_gateway.find(user_id)
        if user is None:
            abort(404)

        return jsonify(user)

    @api.route("/users/<int:user_id>", methods=["PUT"])
    def update(user_id: int):
        data = request.get_json()
        if not data or "name" not in data or "email" not in data:
            abort(400) 

        if "is_admin" not in data:
            abort(400) 

        updated_user = users_gateway.update(user_id, data["name"], data["email"], data["is_admin"])
        if updated_user is None:
            abort(404)  

        return jsonify(updated_user), 200

    return api
