from app import db

from flask import Blueprint, g

from flask_restful import Api, Resource
from flask.ext.restful import abort, fields, marshal_with, reqparse
from app.notify.models import Customer, Notification

from app.settings import Assistance

#from app.base.decorators import login_required, has_permissions
#from app.pages.models import Page

notify_bp = Blueprint('notify_api', __name__)
api = Api(notify_bp)

notify_fields = {
    'customer_id': fields.Integer,
    'notification_key': fields.Integer
}

parser = reqparse.RequestParser()
parser.add_argument('customer_id', type=int)
parser.add_argument('notification_key', type=int)

class Notification(Resource):
    def post(self):
        parsed_args = parser.parsed_args()
        customer_id = parsed_args['customer_id']
        notification_key = parsed_args['notification_key']
        if not self.__ensure_customer_existence(customer_id):
            abort(400, message="Customer does not exist")
        notification = Notification(notification_key, customer_id)
        # TODO: inform dashboard and send sms
        db.session.add(notification)
        db.session.commit()
        return {}, 200

    def __ensure_customer_existence(self, cust_id):
        return True if Customer.query.filter_by(id=cust_id) else False

api.add_resource(Notification, '/')
