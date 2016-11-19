from enum import Enum


class Policy(Enum):
    Lebensversicherung = 0
    Unfallverischerung = 1
    KFZVersicherung = 2


class Assistance(Enum):
    user_dropped = 0
    user_stopped_moving = 1
    car_out_of_range = 2
    car_moves = 3
    #to be extended


assistance_for_policy = {
    Policy.Lebensversicherung : [Assistance.user_dropped.value],
    Policy.Unfallverischerung : [Assistance.user_stopped_moving.value],
    Policy.KFZVersicherung : [Assistance.car_out_of_range.value, Assistance.car_moves.value]
    #To be extended
}