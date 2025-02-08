# Git Exercise

This exercise demonstrates keeping a linear Git history using [trunk-based development](https://martinfowler.com/articles/branching-patterns.html#Trunk-basedDevelopment),
while staying flexible to add a hotfix.
You'll also learn a bit about [Python](https://www.python.org/) and [Flask](https://flask.palletsprojects.com/en/stable/).

We recommend using [UV](https://github.com/astral-sh/uv) to manage Python and PyPi dependencies, and [PyCharm](https://www.jetbrains.com/pycharm/)
as your editor.

## Project setup

The [git_exercise](./git_exercise) package contains a simple application built with [Flask](https://flask.palletsprojects.com/en/stable/)
that tracks users.
It currently has two endpoints: a `/users` endpoint that returns a list of all users and a `/users/<user_id>` endpoint
that returns a single user.

1.  Create a virtual environment and install dependencies.
    ```shell
    uv sync
    ```

1.  Run tests.
    ```shell
    uv run -m unittest
    ```

1.  Run the app.
    ```shell
    uv run -m git_exercise
    ```

1.  Visit [localhost:8080](http://localhost:8080) to see the app.

1.  Use the [requests.http](./requests.http) file to make requests to the app from PyCharm.

## The Exercise

Add features to the codebase, tracking changes in Git.
Your grade will be based on successfully adding the two features and tracking your changes in Git.
Importantly, at the end of the exercise your commit history on your main branch must be linear.
Follow the directions below to make changes to the codebase, and record all git commands that you use in the
[git-commands.log](./git-commands.log) file.

To start, the codebase has a passing test suite.
Update these tests to ensure the test suite continues to pass throughout the assignment, and new tests to cover the new
features you add.

### Create the repository

Initialize a new Git repository and version 1.0.0 of the app.

1.  Create a git repository with the [init command](https://git-scm.com/docs/git-init).
1.  Stage the project files with the [add command](https://git-scm.com/docs/git-add).
1.  Make a commit with the [commit command](https://git-scm.com/docs/git-commit).
    Give your commit the message "Initial commit".
1.  Create a tag called `v1.0.0` with the [tag command](https://git-scm.com/docs/git-tag).
1.  Run the [log command](https://git-scm.com/docs/git-log) with the [`--oneline`](https://git-scm.com/docs/git-log#Documentation/git-log.txt---oneline),
    [`--graph`](https://git-scm.com/docs/git-log#Documentation/git-log.txt---graph), and [`--all`](https://git-scm.com/docs/git-log#Documentation/git-log.txt---all)
    flags to see a graphical view of your Git history.
    Remember this command (or learn about [bash/zsh reverse-i-search](https://www.oreilly.com/library/view/learn-linux-shell/9781788995597/85d106f3-cc78-42d4-90d9-4a944db3c065.xhtml))
    to use it later in the exercise to check your progress

### Create version 1.1.0

Add a couple features and create version 1.1.0 of the app.

1.  Add the `email` attribute to the user class as a string.
    Make sure `email` is returned from the list and find endpoints and that the tests still pass.

1.  Add an endpoint that handles a POST request to the `/users` endpoint with a body that looks like
    ```json
    {
      "name": "Kate Etak",
      "email": "kate@example.com"
    }
    ```
    The endpoint should create a new user and respond with a 201 status code and a body that looks like
    ```json
    {
      "id": 7
    }
    ```
    Once you're done and everything is well tested, commit your changes and add a [descriptive commit message](https://git-scm.com/docs/git-commit#_discussion).
    
1.  Add an endpoint that handles a PUT request to the `/users/<int:user_id>` endpoint with a body that looks like
    ```json
    {
      "name": "Paul Luap",
      "email": "paul@example.com"
    }
    ```
    The endpoint should update the given user and respond with a 200 status code and a body that looks like
    ```json
    {
      "id": 8,
      "name": "Paul Luap",
      "email": "paul@example.com"
    }
    ```
    If the user with the indicated ID does not exist, the endpoint should return a 404 status code.
    Once you're done and everything is well tested, commit your changes and add a descriptive commit message.

1.  Create a tag called `v1.1.0` to mark the new version.

### Add a hotfix

Before version 1.1.0 is released we have to make a hotfix to version 1.0.0 and deploy it.

1.  Create a new branch called `add-admin-role` at the `v1.0.0` tag using the [branch command](https://git-scm.com/docs/git-branch).

1.  Add the `is_admin` attribute to the user class as a boolean.
    Make sure `is_admin` is returned from the list and find endpoints and that the tests still pass.

1.  Commit your changes and add a descriptive commit message.

1.  Create a tag called `v1.0.1` to mark the new version.

### Apply your hotfix to main

Now apply your `is_admin` hotfix to `main` to create version 1.1.1.

1.  While on the `add-admin-role` branch, apply your changes on top of `main` with the [rebase command](https://git-scm.com/docs/git-rebase).
    You'll likely run into merge conflicts (by design), so [fix them before continuing](https://git-scm.com/docs/git-rebase#:~:text=valid%20commit%2Dish.-,In%20case%20of%20conflict,-%2C%20git%20rebase).
    PyCharm has a [nice interface](https://www.jetbrains.com/help/pycharm/resolve-conflicts.html) for resolving conflicts.

1.  Run the test to make sure they still pass.

1.  Switch to the `main` branch with the [switch command](https://git-scm.com/docs/git-switch).

1.  Since `main` is now a direct descendant of `add-admin-role`, use a [fast-forward merge](https://git-scm.com/docs/git-merge#_fast_forward_merge)
    to bring the branch up-to-date.
    Use the [merge command](https://git-scm.com/docs/git-merge) with the [`--ff-only` flag](https://git-scm.com/docs/git-merge#Documentation/git-merge.txt---ff-only)
    to do so.

1.  Run the test to make sure the fast-forward merge was performed correctly.

1.  Create a tag called `v1.1.1` to mark the new version.

1.  Since the `add-admin-role` branch is no longer used, delete it with the branch command's [`--delete` flag](https://git-scm.com/docs/git-branch#Documentation/git-branch.txt---delete).

1.  Run the log command again with the `--oneline`, `--graph`, and `--all` flags to make sure you have a linear history
    on the `main` branch.
    If your history is linear and your tests pass, then you're done! 

After completing the assignment, push your code to a **private** repository in the [coloradocollective](https://github.com/coloradocollective)
GitHub organization.
Check to make sure that the GitHub action passes!
