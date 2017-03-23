#!/bin/env python
# -*- coding:utf-8 -*-
import time
import json
import socket
from itertools import izip
from operator import sub

Entry = {
    "Endpoint": socket.gethostname(),
    "Timestamp": int(time.time()),
    "Step": 60,
}


def per_cpu_data():
    result = []
    with open('/proc/stat') as f:
        for line in f.readlines():
            if line.startswith("cpu"):
                name, user, nice, system, idle, iowait, irq, softirq, steal, guest = line.split()
                if name == 'cpu':
                    # we don't need first cpu line, open-falcon already collected this
                    continue
                metric = map(int, [user, nice, system, idle, iowait, irq, softirq, steal, guest])
                metric.append(name)
                result.append(metric)
    return result


def cpu_metric():
    result = []

    old_cpu_metrics = per_cpu_data()
    time.sleep(1)
    new_cpu_metrics = per_cpu_data()

    def guage(metric, value):
        entry = Entry.copy()
        entry.update({
            "CounterType": "GAUGE",
            "Metric": metric,
            "TAGS": "type=perCPUCore",
            "Value": value
        })
        return entry

    for n_metric, o_metric in izip(new_cpu_metrics, old_cpu_metrics):
        name = o_metric.pop()
        if name != n_metric.pop():
            raise ValueError('cpu index not match')
        user, nice, system, idle, iowait, irq, softirq, steal, guest = map(sub, n_metric, o_metric)
        result.append(guage("{0}.user".format(name), user))
        result.append(guage("{0}.nice".format(name), nice))
        result.append(guage("{0}.system".format(name), system))
        result.append(guage("{0}.idle".format(name), idle))
        result.append(guage("{0}.iowait".format(name), iowait))
        result.append(guage("{0}.irq".format(name), irq))
        result.append(guage("{0}.softirq".format(name), softirq))
        result.append(guage("{0}.steal".format(name), steal))
        result.append(guage("{0}.guest".format(name), guest))
    return result

print(json.dumps(cpu_metric()))
