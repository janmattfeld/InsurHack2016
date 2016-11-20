# InsurHack2016

We enable Insurance Assistance for more everyday situations and allow users to receive emergency assistance in stressful situations. Therefore, we collect sensor data from mobile phones and wearables, in order to detect certain activities and automate the needed support. E.g. when an elderly policy holder trips and falls, we automatically contact emergency services. By leveraging and extending the Zurich API, we track our features' usage and help Zurich respond to their modern customer needs.

How it was built: 
  Frontend - Materialize, HTML, CSS, JS
  Backend - Flask, Python, SQLite
  Native Android Apps

Install [Python 3](https://www.python.org/download/releases/3.0/)

Install dependencies
```
python pip install -r requirements.txt
```

If you have Python 2.~ and 3.~ installed try:
```
python3 -m pip install-r requirements.txt
```

Setup database:
```
python manage.py db init
python manage.py db migrate
python manage.py db upgrade
python manage.py shell
from app import db 
db.create_all()
from app.notify.models import Customer, Notification
c = Customer(221)
n = Notification(1, 221)
db.session.add(c)
db.session.add(n)
db.session.commit()
```

Run server on localhost:5000
```
python3 manage.py runserver
```
