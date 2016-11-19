from app import db

from flask import Blueprint, g

from flask_restful import Api, Resource
from flask.ext.restful import abort, fields, marshal_with, reqparse
from app.notify.models import Customer
from app.notify.models import Notification as Notif

from app.settings import Assistance

dashboard_api_bp = Blueprint('dashboard_api', __name__)
api = Api(dashboard_api_bp)

notify_fields = {
    'customer_id': fields.Integer,
}

parser = reqparse.RequestParser()
parser.add_argument('customer_id', type=int)

class Dashboard(Resource):
    def post(self):
        parsed_args = parser.parse_args()
        customer_id = parsed_args['customer_id']
        # check whether an event has occured
        customer = Customer.query.filter_by(customer_id=customer_id).first()
        if not customer.updated:
            return {'data': {'updated': False}}, 200
        else:
            notification = Notif.query.filter_by(customer=customer_id).all()[-1]
            print('notif: ' + str(notification))
            customer.updated = False
            db.session.add(customer)
            db.session.commit()
            return {'data': {'updated': True, 'notification_key': notification.notification_key}}, 200


api.add_resource(Dashboard, '/')
