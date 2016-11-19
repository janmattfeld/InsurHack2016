import requests
from app.settings import Policy, assistance_for_policy

class Wrapper(object):

    service_uri = "https://api.insurhack.com/apis/gi/1"

    headers = {
        'authorization': "Bearer 415aeaf7-58c7-3d4e-97e6-412d97f57161",
        'content-type': "application/json",
        'cache-control': "no-cache"
    }

    @staticmethod
    def get_customer(customer_id):

        resource_path = "/Account_Set(%27pc:" + str(customer_id) + "%27)"

        query_string = {"$expand": "AccountHolderContact"}

        return requests.request("GET", Wrapper.service_uri + resource_path , headers=Wrapper.headers, params=query_string).json()


    @staticmethod
    def customer_exists(customer_id):
        return Wrapper.get_customer(customer_id).status_code == 200


    @staticmethod
    def get_customer_policies(customer_id):

        resource_path = "/Account_Set(%27pc:" + str(customer_id) + "%27)"

        query_string = {"$expand": "Policies"}

        policy_ids = requests.request("GET", Wrapper.service_uri + resource_path , headers=Wrapper.headers, params=query_string).json()
        policy_details = []
        for policy in policy_ids["Policies"]:
            print(policy['PublicID'])


    @staticmethod
    def get_assistance_for_customer(customer_id):

        # TODO FIX
        # respones = get_customer(customer_id)
        # get policys
            #get assistance for each policy
        return assistance_for_policy[Policy.Lebensversicherung] # MOCKED
