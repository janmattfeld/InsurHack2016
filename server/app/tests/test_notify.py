import unittest
from app.notify.models import Notification


class TestNotify(unittest.TestCase):
    def test_create_notification(self):
        notification = Notification(1, 2);
        self.assertTrue(notification.customer == 2);
        self.assertTrue(notification.notification_key == 1);


if __name__ == '__main__':
    unittest.main()