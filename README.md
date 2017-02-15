# open-falcon-plugins
open-falcon 数据采集脚本

需要执行权限
```
sudo chmod +x 60_*
```


## jvm monitor
JVM options, rmi.port 可以不写
```
-Dcom.sun.management.jmxremote \
-Dcom.sun.management.jmxremote.rmi.port=7088 \
-Dcom.sun.management.jmxremote.port=10053 \
-Dcom.sun.management.jmxremote.ssl=false \
-Dcom.sun.management.jmxremote.authenticate=false
```

run test from commandline
```
groovy -cp .\src test\CatalinaMonitorTest.groovy dump
```

tomcat 收集结果展示
```
[
    {
        "CounterType": "GAUGE",
        "Endpoint": "web-PC",
        "Timestamp": "1487059682",
        "Step": 60,
        "TAGS": "domain=java.lang,type=Threading,name=ThreadCount",
        "Metric": "jvm.java.lang.Threading.ThreadCount",
        "Value": 140
    },
    {
        "CounterType": "GAUGE",
        "Endpoint": "web-PC",
        "Timestamp": "1487059682",
        "Step": 60,
        "TAGS": "domain=java.lang,type=Threading,name=PeakThreadCount",
        "Metric": "jvm.java.lang.Threading.PeakThreadCount",
        "Value": 141
    },
    {
        "CounterType": "GAUGE",
        "Endpoint": "web-PC",
        "Timestamp": "1487059682",
        "Step": 60,
        "TAGS": "domain=java.lang,type=Memory,name=HeapMemoryUsage",
        "Metric": "jvm.java.lang.Memory.HeapMemoryUsage.used",
        "Value": 813209296
    },
    {
        "CounterType": "GAUGE",
        "Endpoint": "web-PC",
        "Timestamp": "1487059682",
        "Step": 60,
        "TAGS": "domian=java.lang,type=Memory,name=HeapMemoryUsage",
        "Metric": "jvm.java.lang.Memory.HeapMemoryUsage.usedPercent",
        "Value": 13.35
    },
    {
        "CounterType": "GAUGE",
        "Endpoint": "web-PC",
        "Timestamp": "1487059682",
        "Step": 60,
        "TAGS": "domain=java.lang,type=Memory,name=NonHeapMemoryUsage",
        "Metric": "jvm.java.lang.Memory.NonHeapMemoryUsage.used",
        "Value": 106722248
    },
    {
        "CounterType": "GAUGE",
        "Endpoint": "web-PC",
        "Timestamp": "1487059682",
        "Step": 60,
        "TAGS": "domian=java.lang,type=Memory,name=NonHeapMemoryUsage",
        "Metric": "jvm.java.lang.Memory.NonHeapMemoryUsage.usedPercent",
        "Value": 0
    },
    {
        "CounterType": "GAUGE",
        "Endpoint": "web-PC",
        "Timestamp": "1487059682",
        "Step": 60,
        "TAGS": "domian=java.lang,type=MemoryPool,name=Metaspace",
        "Metric": "jvm.java.lang.MemoryPool.Metaspace.used",
        "Value": 60235344
    },
    {
        "CounterType": "GAUGE",
        "Endpoint": "web-PC",
        "Timestamp": "1487059682",
        "Step": 60,
        "TAGS": "domian=java.lang,type=MemoryPool,name=Metaspace",
        "Metric": "jvm.java.lang.MemoryPool.Metaspace.usedPercent",
        "Value": 0
    },
    {
        "CounterType": "GAUGE",
        "Endpoint": "web-PC",
        "Timestamp": "1487059682",
        "Step": 60,
        "TAGS": "domian=java.lang,type=MemoryPool,name=PS Old Gen",
        "Metric": "jvm.java.lang.MemoryPool.PSOldGen.used",
        "Value": 104367408
    },
    {
        "CounterType": "GAUGE",
        "Endpoint": "web-PC",
        "Timestamp": "1487059682",
        "Step": 60,
        "TAGS": "domian=java.lang,type=MemoryPool,name=PS Old Gen",
        "Metric": "jvm.java.lang.MemoryPool.PSOldGen.usedPercent",
        "Value": 2.43
    },
    {
        "CounterType": "GAUGE",
        "Endpoint": "web-PC",
        "Timestamp": "1487059682",
        "Step": 60,
        "TAGS": "domian=java.lang,type=MemoryPool,name=PS Eden Space",
        "Metric": "jvm.java.lang.MemoryPool.PSEdenSpace.used",
        "Value": 405988520
    },
    {
        "CounterType": "GAUGE",
        "Endpoint": "web-PC",
        "Timestamp": "1487059682",
        "Step": 60,
        "TAGS": "domian=java.lang,type=MemoryPool,name=PS Eden Space",
        "Metric": "jvm.java.lang.MemoryPool.PSEdenSpace.usedPercent",
        "Value": 28.21
    },
    {
        "CounterType": "GAUGE",
        "Endpoint": "web-PC",
        "Timestamp": "1487059682",
        "Step": 60,
        "TAGS": "domian=java.lang,type=MemoryPool,name=Compressed Class Space",
        "Metric": "jvm.java.lang.MemoryPool.CompressedClassSpace.used",
        "Value": 6527808
    },
    {
        "CounterType": "GAUGE",
        "Endpoint": "web-PC",
        "Timestamp": "1487059682",
        "Step": 60,
        "TAGS": "domian=java.lang,type=MemoryPool,name=Compressed Class Space",
        "Metric": "jvm.java.lang.MemoryPool.CompressedClassSpace.usedPercent",
        "Value": 0.61
    },
    {
        "CounterType": "GAUGE",
        "Endpoint": "web-PC",
        "Timestamp": "1487059682",
        "Step": 60,
        "TAGS": "domian=java.lang,type=MemoryPool,name=Code Cache",
        "Metric": "jvm.java.lang.MemoryPool.CodeCache.used",
        "Value": 39974464
    },
    {
        "CounterType": "GAUGE",
        "Endpoint": "web-PC",
        "Timestamp": "1487059682",
        "Step": 60,
        "TAGS": "domian=java.lang,type=MemoryPool,name=Code Cache",
        "Metric": "jvm.java.lang.MemoryPool.CodeCache.usedPercent",
        "Value": 15.88
    },
    {
        "CounterType": "GAUGE",
        "Endpoint": "web-PC",
        "Timestamp": "1487059682",
        "Step": 60,
        "TAGS": "domian=java.lang,type=MemoryPool,name=PS Survivor Space",
        "Metric": "jvm.java.lang.MemoryPool.PSSurvivorSpace.used",
        "Value": 303587512
    },
    {
        "CounterType": "GAUGE",
        "Endpoint": "web-PC",
        "Timestamp": "1487059682",
        "Step": 60,
        "TAGS": "domian=java.lang,type=MemoryPool,name=PS Survivor Space",
        "Metric": "jvm.java.lang.MemoryPool.PSSurvivorSpace.usedPercent",
        "Value": 85.41
    },
    {
        "CounterType": "GAUGE",
        "Endpoint": "web-PC",
        "Timestamp": "1487059682",
        "Step": 60,
        "TAGS": "domian=java.lang,type=OperatingSystem,name=ProcessCpuLoad",
        "Metric": "jvm.java.lang.OperatingSystem.ProcessCpuLoad",
        "Value": 0.00
    },
    {
        "CounterType": "GAUGE",
        "Endpoint": "web-PC",
        "Timestamp": "1487059682",
        "Step": 60,
        "TAGS": "domian=java.lang,type=GarbageCollector,name=PS MarkSweep",
        "Metric": "jvm.java.lang.GarbageCollector.PSMarkSweep.CollectionCount",
        "Value": 3
    },
    {
        "CounterType": "GAUGE",
        "Endpoint": "web-PC",
        "Timestamp": "1487059682",
        "Step": 60,
        "TAGS": "domian=java.lang,type=GarbageCollector,name=PS MarkSweep",
        "Metric": "jvm.java.lang.GarbageCollector.PSMarkSweep.CollectionTime",
        "Value": 992
    },
    {
        "CounterType": "GAUGE",
        "Endpoint": "web-PC",
        "Timestamp": "1487059682",
        "Step": 60,
        "TAGS": "domian=java.lang,type=GarbageCollector,name=PS Scavenge",
        "Metric": "jvm.java.lang.GarbageCollector.PSScavenge.CollectionCount",
        "Value": 44
    },
    {
        "CounterType": "GAUGE",
        "Endpoint": "web-PC",
        "Timestamp": "1487059682",
        "Step": 60,
        "TAGS": "domian=java.lang,type=GarbageCollector,name=PS Scavenge",
        "Metric": "jvm.java.lang.GarbageCollector.PSScavenge.CollectionTime",
        "Value": 21777
    },
    {
        "CounterType": "GAUGE",
        "Endpoint": "web-PC",
        "Timestamp": "1487059682",
        "Step": 60,
        "TAGS": "domain=Catalina,type=ThreadPool,name=currentThreadCount",
        "Metric": "jvm.Catalina.ThreadPool.currentThreadCount",
        "Value": 10
    },
    {
        "CounterType": "GAUGE",
        "Endpoint": "web-PC",
        "Timestamp": "1487059682",
        "Step": 60,
        "TAGS": "domain=Catalina,type=ThreadPool,name=currentThreadsBusy",
        "Metric": "jvm.Catalina.ThreadPool.currentThreadsBusy",
        "Value": 0
    },
    {
        "CounterType": "GAUGE",
        "Endpoint": "web-PC",
        "Timestamp": "1487059682",
        "Step": 60,
        "TAGS": "domain=Catalina,type=ThreadPool,name=connectionCount",
        "Metric": "jvm.Catalina.ThreadPool.connectionCount",
        "Value": 1
    },
    {
        "CounterType": "COUNTER",
        "Endpoint": "web-PC",
        "Timestamp": "1487059682",
        "Step": 60,
        "TAGS": "domain=Catalina,type=GlobalRequestProcessor,name=bytesReceived",
        "Metric": "jvm.Catalina.GlobalRequestProcessor.bytesReceived",
        "Value": 15425
    },
    {
        "CounterType": "COUNTER",
        "Endpoint": "web-PC",
        "Timestamp": "1487059682",
        "Step": 60,
        "TAGS": "domain=Catalina,type=GlobalRequestProcessor,name=bytesSent",
        "Metric": "jvm.Catalina.GlobalRequestProcessor.bytesSent",
        "Value": 17778202
    },
    {
        "CounterType": "COUNTER",
        "Endpoint": "web-PC",
        "Timestamp": "1487059682",
        "Step": 60,
        "TAGS": "domain=Catalina,type=GlobalRequestProcessor,name=errorCount",
        "Metric": "jvm.Catalina.GlobalRequestProcessor.errorCount",
        "Value": 15
    },
    {
        "CounterType": "COUNTER",
        "Endpoint": "web-PC",
        "Timestamp": "1487059682",
        "Step": 60,
        "TAGS": "domain=Catalina,type=GlobalRequestProcessor,name=requestCount",
        "Metric": "jvm.Catalina.GlobalRequestProcessor.requestCount",
        "Value": 1166
    }
]
```