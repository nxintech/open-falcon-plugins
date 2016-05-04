#!/usr/bin/env python
# -*- coding:utf-8 -*-

import socket
import time
from itertools import izip


def group_by(iterable, n):
    args = [iter(iterable)] * n
    return izip(*args)


RECV_SIZE = 4096


class MemcacheClient(object):
    def __init__(self,
                 server,
                 timeout=None):
        self.sock = None
        self.server = server
        self.timeout = timeout

    def _connect(self):
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.connect(self.server)
        sock.settimeout(self.timeout)
        self.sock = sock

    def _close(self):
        if self.sock:
            self.sock.close()
        self.sock = None

    def get(self, name):
        name += b''
        cmd = name + b'\r\n'
        if not self.sock:
            self._connect()
        self.sock.sendall(cmd)

        buf = b''
        result = []
        while True:
            buf, line = parseline(self.sock, buf)
            if line == b'END':
                self._close()
                return result
            result.append(line)


def parseline(sock, buf):
    chunks = []
    last_char = b''

    while True:
        if last_char == b'\r' and buf[0:1] == b'\n':
            # Strip the last character from the last chunk.
            chunks[-1] = chunks[-1][:-1]
            return buf[1:], b''.join(chunks)
        elif buf.find(b'\r\n') != -1:
            before, sep, after = buf.partition(b"\r\n")
            chunks.append(before)
            return after, b''.join(chunks)

        if buf:
            chunks.append(buf)
            last_char = buf[-1:]

        try:
            buf = sock.recv(RECV_SIZE)
        except IOError as e:
            raise e


Entry = {
    "Endpoint": socket.gethostname(),
    "Timestamp": int(time.time()),
    "Step": 60,
}
entry_list = []
host, port = "localhost", 11211
mc = MemcacheClient((host, port), timeout=5)


def mget_stats():
    _bytes = hits_number = 0
    total_bytes = get_number = 1

    for item in mc.get("stats"):
        _, key, value = item.split()
        if key == "bytes":
            _bytes = int(value)
        elif key == "limit_maxbytes":
            total_bytes = int(value)
        elif key == "cmd_get":
            get_number = int(value)
        elif key == "get_hits":
            hits_number = int(value)

    mem_used_percent = round(_bytes * 100.0 / total_bytes, 2)
    cmd_hits_percent = round(hits_number * 100.0 / get_number, 2)

    entry = Entry.copy()
    entry.update({
        "CounterType": "GAUGE",
        "Metric": "memcache.stats.mem_used_percent",
        "TAGS": "type=memcache,port={0}".format(port),
        "Value": mem_used_percent
    })
    entry_list.append(entry)
    entry = Entry.copy()
    entry.update({
        "CounterType": "GAUGE",
        "Metric": "memcache.stats.cmd_hits_percent",
        "TAGS": "type=memcache,port={0}".format(port),
        "Value": cmd_hits_percent
    })
    entry_list.append(entry)


def mget_slabs():
    slabs = {}
    for group in group_by(mc.get("stats slabs"), 16):
        for line in group:
            _, slab, value = line.split()
            n, key = slab.split(":")
            if n in slabs:
                slabs[n][key] = value
            else:
                slabs[n] = {key: value}
    for n, item in slabs.items():
        used_chunks = int(item['used_chunks'])
        total_chunks = int(item['total_chunks'])
        chunks_used_percent = round(used_chunks * 100.0 / total_chunks, 2)
        entry = Entry.copy()
        entry.update({
            "CounterType": "GAUGE",
            "Metric": "memcache.slabs.{0}.chunks_used_percent".format(n),
            "TAGS": "type=memcache,port={0}".format(port),
            "Value": chunks_used_percent
        })
        entry_list.append(entry)

if __name__ == "__main__":
    mget_stats()
    mget_slabs()
    print(entry_list)

