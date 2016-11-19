from app import db
from app.api_wrapper.wrapper import Wrapper

from flask import Blueprint, render_template
from jinja2.exceptions import TemplateNotFound

from flask.ext.restful import abort

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
    customer = Wrapper.get_customer(244);
    customer_policies = Wrapper.get_customer_policies(244);
    try:
        return render_template('dashboard.html', customer=customer, customer_policies=customer_policies)
    except TemplateNotFound:
        abort(404)
