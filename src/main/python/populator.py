from datetime import datetime
from random import choice, randint
from threading import Thread
from faker.providers import lorem
from faker.providers import company
from faker import Faker
import json
import cx_Oracle

class Populator:
    connectionURL: str
    connectionDriverName: str
    connectionUserName: str
    connectionPassword: str
    configPath = 'config.json'
    fake = Faker()

    def __init__(self):
        cx_Oracle.init_oracle_client(lib_dir=r".\instantclient_21_3")
        configLoaded = self.__load_json_config()
        if configLoaded:
            print("Putos todos menos yo")
        else:
            print("Yo puto, los demas no")

    def __load_json_config(self) -> bool:
        try:
            with open(self.configPath) as jsonFile:
                data = json.load(jsonFile)
                self.connectionURL = data['connectionURL']
                self.connectionDriverName = data['connectionDriverName']
                self.connectionUserName = data['connectionUserName']
                self.connectionPassword = data['connectionPassword']
                return True
        except:
            return False

    def populate(self)->None:
        connection = self.getConnection()
        cursor = connection.cursor()
        for i in range (0,15000):
            self.__populate_cliente()

    #TODO: USUARIO, RESERVA, OFERTA (CLIENT, RESERVATION, HOST_OFFER)
    def __populate_cliente(self, connection, cursor)->None:
        for i in range(0, 15000):

            cursor.execute(f"INSERT INTO client nuip, name, client_type VALUES ({self.fake.unique.random_int},{self.fake.bs()})")
