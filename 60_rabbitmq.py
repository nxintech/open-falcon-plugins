# -*- coding:utf-8 -*-
import time
import socket
import requests

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
    message_stats = queue_data['message_stats']

    # 'running' is 1
    entries.append(new_entry({
        "CounterType": "GAUGE",
        "Metric": "rabbit.queue.state",
        "TAGS": "type=rabbit,queue_name={0}".format(queue_name),
        "Value": 1 if queue_data['state'] == 'running' else 0
    }))

    # deliver: Count of messages delivered in acknowledgement mode to consumers.
    entries.append(new_entry({
        "CounterType": "GAUGE",
        "Metric": "rabbit.queue.message_stats.deliver,",
        "TAGS": "type=rabbit,queue_name={0}".format(queue_name),
        "Value": message_stats['deliver']
    }))

    # deliver: Count of messages delivered in acknowledgement mode to consumers.
    entries.append(new_entry({
        "CounterType": "GAUGE",
        "Metric": "rabbit.queue.message_stats.deliver,",
        "TAGS": "type=rabbit,queue_name={0}".format(queue_name),
        "Value": message_stats['deliver']
    }))

    # get: Count of messages delivered in acknowledgement mode in response to basic.get.
    entries.append(new_entry({
        "CounterType": "GAUGE",
        "Metric": "rabbit.queue.message_stats.get,",
        "TAGS": "type=rabbit,queue_name={0}".format(queue_name),
        "Value": message_stats['get']
    }))

    # deliver_get :Sum four of deliver/deliver_noack and get/get_noack
    entries.append(new_entry({
        "CounterType": "GAUGE",
        "Metric": "rabbit.queue.message_stats.deliver_get,",
        "TAGS": "type=rabbit,queue_name={0}".format(queue_name),
        "Value": message_stats['deliver_get']
    }))


if __name__ == '__main__':
    result = []
    for queue in list_queue():
        if queue['name'] == 'nxin.monitor.queue':
            # pass this monitor queue
            continue
        falcon_push_data(result, queue)
    print(result)
