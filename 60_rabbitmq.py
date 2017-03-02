#!/bin/env python
# -*- coding:utf-8 -*-
import time
import socket
import requests
import json

# change below in your environment
manager_host = ''
manager_port = 15672
username = ''
password = ''

# manager api 
queue_list = 'http://{0}:{1}/api/queues/'
queue_detail = 'http://{0}:{1}/api/queues/%2F/{2}'

# open-falcon push data
Entry = {
    "Endpoint": socket.gethostname(),
    "Timestamp": int(time.time()),
    "Step": 60
}


def new_entry(data):
    entry = Entry.copy()
    entry.update(data)
    return entry


def list_queue():
    url = queue_list.format(manager_host, manager_port)
    response = requests.get(url, auth=(username, password))
    return response.json()


def falcon_push_data(entries, queue_data):
    queue_name = queue_data['name']
    try:
        message_stats = queue_data['message_stats']
    except KeyError:
        return
    
    # 'running' is 1
    entries.append(new_entry({
        "CounterType": "GAUGE",
        "Metric": "rabbit.queue.state",
        "TAGS": "type=rabbit,queue_name={0}".format(queue_name),
        "Value": 1 if queue_data['state'] == 'running' else 0
    }))

    # publish: Count of messages published.
    entries.append(new_entry({
        "CounterType": "GAUGE",
        "Metric": "rabbit.queue.message_stats.publish,",
        "TAGS": "type=rabbit,queue_name={0}".format(queue_name),
        "Value": message_stats['publish']
    }))

    # confirm: Count of messages confirmed.
    entries.append(new_entry({
        "CounterType": "GAUGE",
        "Metric": "rabbit.queue.message_stats.confirm,",
        "TAGS": "type=rabbit,queue_name={0}".format(queue_name),
        "Value": message_stats['confirm']
    }))

    # not every queue has deliver / get / deliver_get
    try:
        # deliver: Count of messages delivered in acknowledgement mode to consumers.
        entries.append(new_entry({
            "CounterType": "GAUGE",
            "Metric": "rabbit.queue.message_stats.deliver,",
            "TAGS": "type=rabbit,queue_name={0}".format(queue_name),
            "Value": message_stats['deliver']
        }))
    except KeyError:
        pass

    try:
        # get: Count of messages delivered in acknowledgement mode in response to basic.get.
        entries.append(new_entry({
            "CounterType": "GAUGE",
            "Metric": "rabbit.queue.message_stats.get,",
            "TAGS": "type=rabbit,queue_name={0}".format(queue_name),
            "Value": message_stats['get']
        }))
    except KeyError:
        pass

    try:
        # deliver_get :Sum four of deliver/deliver_noack and get/get_noack
        entries.append(new_entry({
            "CounterType": "GAUGE",
            "Metric": "rabbit.queue.message_stats.deliver_get,",
            "TAGS": "type=rabbit,queue_name={0}".format(queue_name),
            "Value": message_stats['deliver_get']
        }))
    except KeyError:
        pass


if __name__ == '__main__':
    result = []
    for queue in list_queue():
        if queue['name'] == 'nxin.monitor.queue':
            # pass this monitor queue
            continue
        falcon_push_data(result, queue)
    print(json.dumps(result))
