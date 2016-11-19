import flask
from flask.ext.sqlalchemy import SQLAlchemy
from flask.ext.migrate import Migrate
from flask.ext.cors import CORS

app = flask.Flask(__name__)
app.config.from_object('config')
db = SQLAlchemy(app)
migrate = Migrate(app, db)

CORS(app, resources=r'/*', allow_headers='*')

@app.errorhandler(404)
def not_found(error):
    err = {'message': "Resource doesn't exist."}
    return flask.jsonify(**err)


from app.notify.resources import notify_bp
from app.views.resources import dashboard_bp, home_bp
from app.dashboard.resources import dashboard_api_bp


app.register_blueprint(
    notify_bp,
    url_prefix='/notify'
)

app.register_blueprint(
    dashboard_bp,
    url_prefix=('/dashboard')
)

app.register_blueprint(
    dashboard_api_bp,
    url_prefix=('/updated')
)

app.register_blueprint(
    home_bp,
    url_prefix=('/')
)
