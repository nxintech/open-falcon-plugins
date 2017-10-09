#!/bin/env python
# -*- coding:utf-8 -*-
import time
import socket
import requests
import json

# open-falcon push data
Entry = {
    "Endpoint": socket.gethostname(),
    "Timestamp": int(time.time()),
    "Step": 60
}


def new_entry(counter_type, metric, tag, value):
    entry = Entry.copy()
    entry.update({
        "CounterType": counter_type,
        "Metric": metric,
        "TAGS": "type=rabbit,{0}".format(tag),
        "Value": value
    })
    return entry


class Manager(object):
    def __init__(self, username, password, host="127.0.0.1", port=15672):
        self.username = username
        self.password = password
        self.host = host
        self.port = port
        self.entries = []

    @property
    def api(self):
        return "http://{0}:{1}/api/".format(self.host, self.port)

    def get_api_data(self, name):
        url = self.api + name
        response = requests.get(url, auth=(self.username, self.password))
        return response.json()

    def dump_queues(self, excludes):
        for q in self.get_api_data("queues"):
            if q['name'] in excludes:
                continue
            self.dump_queue(q)

    def dump_queue(self, data):
        tag = "queue_name={0}".format(data['name'])

        # state 'running' is 1
        self.entries.append(new_entry(
            "GAUGE",
            "rabbit.queue.state",
            tag,
            1 if data['state'] == 'running' else 0
        ))

        if "message_stats" not in data:
            return

        #################
        # message_stats #
        #################

        message_stats = data['message_stats']

        for key in ["publish", "confirm", "get", "deliver", "deliver_get"]:
            # publish: Count of messages published.
            # confirm: Count of messages confirmed.
            # get: Count of messages delivered in acknowledgement mode in response to basic.get.
            # deliver: Count of messages delivered in acknowledgement mode to consumers.
            # deliver_get :Sum four of deliver/deliver_noack and get/get_noack.

            if key in message_stats:
                self.entries.append(new_entry(
                    "GAUGE",
                    "rabbit.queue.message_stats.{0}".format(key),
                    tag,
                    message_stats[key]
                ))

    def dump_nodes(self):
        for node in self.get_api_data("nodes"):
            self.dump_node(node)

    def dump_node(self, data):
        tag = "node={0}".format(data["name"])

        partitions = 0 if data["partitions"] is None else 1
        self.entries.append(new_entry(
            "GAUGE",
            "rabbit.node.partitions",
            tag,
            partitions
        ))
        running = 1 if data["running"] else 0
        self.entries.append(new_entry(
            "GAUGE",
            "rabbit.node.running",
            tag,
            running
        ))

        for key in ["fd_used", "sockets_used", "proc_used",
                    "io_read_avg_time", "io_write_avg_time",
                    "io_sync_avg_time", "io_seek_avg_time",
                    "io_file_handle_open_attempt_avg_time"]:
            self.entries.append(new_entry(
                "GAUGE",
                "rabbit.node.{0}".format(key),
                tag,
                data[key]
            ))

        for key in ["io_read_count", "io_read_bytes",
                    "io_write_count", "io_write_bytes",
                    "io_sync_count", "io_seek_count", "io_reopen_count",
                    "mnesia_ram_tx_count", "mnesia_disk_tx_count",
                    "msg_store_read_count", "msg_store_write_count",
                    "queue_index_journal_write_count",
                    "queue_index_read_count", "queue_index_write_count",
                    "gc_num", "gc_bytes_reclaimed",
                    "context_switches", "io_file_handle_open_attempt_count",
                    "sockets_total"]:
            self.entries.append(new_entry(
                "COUNTER",
                "rabbit.node.{0}".format(key),
                tag,
                data[key]
            ))

    def dumps_all(self):
        return json.dumps(self.entries)


if __name__ == '__main__':
    manager = Manager("user", "password")
    manager.dump_queues(["nxin.monitor.queue"])
    manager.dump_nodes()
    print(manager.dumps_all())
