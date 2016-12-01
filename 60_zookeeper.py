#!/usr/bin/env python
# -*- coding:utf-8 -*-

import socket
improt json
import time

RECV_SIZE = 4096
host, port = "localhost", 2181


class ZkClient(object):
    def __init__(self, server, timeout=None):
        self.sock = None
        self.server = server
        self.timeout = timeout

    def _connect(self):
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.connect(self.server)
        if self.timeout:
            sock.settimeout(self.timeout)
        self.sock = sock

    def _close(self):
        if self.sock:
            self.sock.close()
        self.sock = None

    def _recv(self):
        buff = b""
        res = True
        while res:
            res = self.sock.recv(RECV_SIZE)
            buff += res
        self._close()
        return buff

    def get(self, cmd):
        if not self.sock:
            self._connect()
        cmd += "\n"
        self.sock.sendall(cmd)
        return self._recv().split('\n')


def main():
    Entry = {
        "Endpoint": socket.gethostname(),
        "Timestamp": int(time.time()),
        "Step": 60,
    }
    entry_list = []
    zk = ZkClient((host, port))
    connections = node_count = 0
    for line in zk.get("stat"):
        if line.startswith("Connections"):
            connections = line.split()[1]
        if line.startswith("Node count"):
            node_count = line.split()[2]
    entry = Entry.copy()
    entry.update({
        "CounterType": "GAUGE",
        "Metric": "zookeeper.connections",
        "TAGS": "type=zookeeper,port={0}".format(port),
        "Value": connections
    })
    entry_list.append(entry)
    entry = Entry.copy()
    entry.update({
        "CounterType": "GAUGE",
        "Metric": "zookeeper.node_count",
        "TAGS": "type=zookeeper,port={0}".format(port),
        "Value": node_count
    })
    entry_list.append(entry)
    watches = zk.get("wchs")[1].split(":")[1]
    entry = Entry.copy()
    entry.update({
        "CounterType": "GAUGE",
        "Metric": "zookeeper.watches",
        "TAGS": "type=zookeeper,port={0}".format(port),
        "Value": watches
    })
    entry_list.append(entry)
    print(json.dumps(entry_list))


if __name__ == "__main__":
    main()
