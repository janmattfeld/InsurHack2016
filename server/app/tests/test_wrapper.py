import unittest
from app.api_wrapper.wrapper import Wrapper

from app.settings import Assistance, assistance_for_policy



class TestWrapper(unittest.TestCase):
    def test_customer_exists(self):
        self.assertTrue(Wrapper.customer_exists(221))
        self.assertFalse(Wrapper.customer_exists(-1))

    #TODO it's mocked
    def test_get_assistance_customer(self):
        assistance = Wrapper.get_assistance_for_customer(-1)
        self.assertTrue(Assistance.user_dropped.value in assistance)
        self.assertFalse(Assistance.user_stopped_moving.value in Assistance)
        self.assertFalse(Assistance.car_moves.value in Assistance)
        self.assertFalse(Assistance.car_out_of_range.value in Assistance)


if __name__ == '__main__':
    unittest.main()