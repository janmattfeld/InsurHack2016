import flask
from flask.ext.sqlalchemy import SQLAlchemy
from flask_restful import Api
from flask.ext.restful import abort
from flask.ext.migrate import Migrate
from flask.ext.cors import CORS

from config import DASHBOARD_ROOT

app = flask.Flask(__name__)
app.config.from_object('config')
db = SQLAlchemy(app)
migrate = Migrate(app, db)

CORS(app, resources=r'/*', allow_headers='*')

@app.errorhandler(404)
def not_found(error):
    err = {'message': "Resource doesn't exist."}
    return flask.jsonify(**err)


from app.blog.resources import blog_bp
from app.auth.resources import auth_bp
from app.pages.resources import page_bp
from app.notify.resources import notify_bp
from app.views.resources import dashboard_bp, home_bp


app.register_blueprint(
    blog_bp,
    url_prefix='/blog'
)

app.register_blueprint(
    auth_bp,
    url_prefix='/auth'
)

app.register_blueprint(
    page_bp,
    url_prefix='/page'
)

app.register_blueprint(
    notify_bp,
    url_prefix='/notify'
)

app.register_blueprint(
    dashboard_bp,
    url_prefix=('/dashboard')
)

app.register_blueprint(
    home_bp,
    url_prefix=('/')
)