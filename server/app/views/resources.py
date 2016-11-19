from app import db

from flask import Blueprint, g, render_template
from jinja2.exceptions import TemplateNotFound

from flask_restful import Api, Resource
from flask.ext.restful import abort, fields, marshal_with, reqparse
from app.notify.models import Customer
from app.notify.models import Notification as Notif

home_bp = Blueprint('home_bp', __name__,
                        template_folder='templates')

@home_bp.route('/', defaults={'page': 'index'})
def show_home(page):
    try:
        return render_template('index.html')
    except TemplateNotFound:
        abort(404)


dashboard_bp = Blueprint('dashboard_bp', __name__,
                        template_folder='templates')

@dashboard_bp.route('/', defaults={'page': 'dashboard'})
def show_home(page):
    try:
        return render_template('dashboard.html')
    except TemplateNotFound:
        abort(404)
