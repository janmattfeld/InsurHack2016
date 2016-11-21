# InsurHack2016

We enable Insurances to provide Emergency Assistance in everyday situations automatically. We collect sensor data from mobile phones and wearables, to detect activities and potentially harmful situations. By leveraging and extending the Zurich API, we track our features' usage and help Zurich respond to their modern customer needs.

> When an elderly policy holder trips and falls, we automatically call emergency contacts.

The system consists of:
- An Android app, detecting collapsing owners and calling for help automatically
- An insurance backend visualization (Dashboard)

<img src="/Media/Screenshots/android_screenshot_01.png" height="360">
<img src="/Media/Screenshots/android_screenshot_02.png" height="360">
<img src="/Media/Screenshots/android_screenshot_03.png" height="360">

<img src="/Media/Screenshots/finished-dashboard.png" height="360">

## How it was built
- Frontend - HTML, CSS, JS, Materialize
- Backend - Python, Flask, SQLite
- Native Android App
- Zurich Insurance BETA API

## Setup
Setup the latest stable version of [Python 3](https://www.python.org/download/releases/3.0/).

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
