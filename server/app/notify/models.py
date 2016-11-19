from app import db
from app.base.models import Base
from sqlalchemy.orm import relationship


class Notification(Base):
    __tablename__ = 'notification'

    id = db.Column(db.Integer(), unique=True, primary_key=True)
    notification_key = db.Column(db.Integer())
    customer = db.Column(db.Integer(), db.ForeignKey('customer.customer_id'))

    def __init__(self, not_key, customer_id):
        self.notification_key = not_key
        self.customer = customer_id

    def __repr__(self):
        return '<Notification ' + str(self.notification_key) + ' ' + \
                str(self.customer) + '>'


class Customer(Base):
    __tablename__ = 'customer'

    id = db.Column(db.Integer(), primary_key=True)
    customer_id = db.Column(db.Integer(), unique=True)
    updated = db.Column(db.Boolean(), default=False)
    notifications = relationship('Notification')

    def __init__(self, id, updated=False):
        self.customer_id = id
        self.updated = updated

    def __repr__(self):
        return '<Customer ' + str(self.id) + '>'
