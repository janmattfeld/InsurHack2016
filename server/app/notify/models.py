from app import db
from app.base.models import Base
from sqlalchemy.orm import relationship


class Notification(Base):
    __tablename__ = 'notification'

    id = db.Column(db.Integer(), unique=True, primary_key=True)
    notification_key = db.Column(db.Integer())
    customer = db.Column(db.Integer(), db.ForeignKey('customer.id'))

    def __init__(self, not_key, customer_id):
        self.notification_key = not_key
        self.customer = customer_id

    def __repr__(self):
        return '<Notification ' + self.notification_key + ' ' + self.customer + '>'


class Customer(Base):
    __tablename__ = 'customer'

    id = db.Column(db.Integer(), primary_key=True)
    customer_id = db.Column(db.Integer(), unique=True)
    notifications = relationship('Notification')

    def __init__(self, id):
        self.customer_id = id

    def __repr__(self):
        return '<Customer ' + self.id + '>'
