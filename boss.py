#!/usr/bin/python

import json
import sys
import urllib2

dir = "http://www.sexgangsters.com/api/"
headers = {
    'Cookie': 'cook=d4omq90zf34lez2g6qfoo317alz8imd9; _lang=ru; _gat=1; sessionid=hr23tah1brapo1lat8v4eke1vaiazwk6; _ga=GA1.2.717424405.1455726483; csrftoken=QA989ftv5NMiCvWJRD98H69nv8h0ukto',
    'Host': 'www.sexgangsters.com',
    'X-CSRFToken': 'QA989ftv5NMiCvWJRD98H69nv8h0ukto',
    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'}

request = urllib2.Request(dir, "data=%7B%22method%22%3A%22pvpboss.fight.start%22%2C%22args%22%3A%7B%22cid%22%3A23%2C%22id%22%3A%22CB23%22%7D%7D", headers)
response = json.loads(urllib2.urlopen(request).read())

request = urllib2.Request(dir, "data=%7B%22method%22%3A%22pvpboss.fight.finish%22%2C%22args%22%3A%7B%22cid%22%3A23%2C%22id%22%3A%22CB23%22%7D%7D", headers)
response = json.loads(urllib2.urlopen(request).read())