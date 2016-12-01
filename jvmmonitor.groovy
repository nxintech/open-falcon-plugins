#!/usr/local/env groovy

import javax.management.remote.JMXConnectorFactory
import javax.management.remote.JMXServiceURL
import groovy.json.JsonBuilder

/**
 * Created on 2016/4/7.
 * author web
 */


class Mbean {
    private server

    Mbean(ip, port) {
        def jmxServerUrl = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://$ip:$port/jmxrmi")
        server = JMXConnectorFactory.connect(jmxServerUrl).MBeanServerConnection
    }

    def getMbean(cls, type) {
        return new GroovyMBean(server, "$cls:type=$type")
    }

    def getMbean(cls, type, name) {
        return new GroovyMBean(server, "$cls:type=$type,name=$name")
    }

    def combine = { String... names ->
        def res = []
        names.each {
            res.add(it.replace(" ", ""))
        }
        res.join('.')
    }

    static def getPercent(numerator, denominator) {
        if (denominator < 0) {
            return 0
        }
        BigDecimal res = (numerator / denominator) * 100
        return res.setScale(2, BigDecimal.ROUND_HALF_UP)
    }
}

class JavaLangMbean extends Mbean {
    String metricPrefix = "jvm"
    String cls = "java.lang"

    JavaLangMbean(ip, port) { super(ip, port) }

    // A entry is the data that pushed to open-falcon transfer
    def entry = [
            hostname : InetAddress.getLocalHost().getHostName(),
            timestamp: new Date().getTime().intdiv(1000),
            step     : 60
    ]
    def entryList = []

    // add Threading info from mbean to entryList
    def getThreading() {
        def type = "Threading"
        def mbean = getMbean(cls, type)
        ["ThreadCount", "PeakThreadCount"].each {
            entryList.add([
                    CounterType: "GAUGE",
                    Endpoint   : entry.hostname,
                    Timestamp  : entry.timestamp,
                    Step       : entry.step,
                    TAGS       : "type=${type}",
                    Metric     : combine(metricPrefix, cls, type, it),
                    Value      : mbean."${it}"
            ])
        }
    }

    def getMemory() {
        def type = "Memory"
        def mbean = getMbean(cls, type)
        ["HeapMemoryUsage", "NonHeapMemoryUsage"].each {
            def used = mbean."${it}".used
            def max = mbean."${it}".max
            entryList.add([
                    CounterType: "GAUGE",
                    Endpoint   : entry.hostname,
                    Timestamp  : entry.timestamp,
                    Step       : entry.step,
                    TAGS       : "type=${type}",
                    Metric     : combine(metricPrefix, cls, type, it, "used"),
                    Value      : used
            ])
            entryList.add([
                    CounterType: "GAUGE",
                    Endpoint   : entry.hostname,
                    Timestamp  : entry.timestamp,
                    Step       : entry.step,
                    TAGS       : "type=${type}",
                    Metric     : combine(metricPrefix, cls, type, it, "percent"),
                    Value      : getPercent(used, max)
            ])
        }

    }

    // MemoryPool
    def getMemoryPool() {
        def type = "MemoryPool"
        zones.each {
            def mbean = getMbean(cls, type, it)
            def used = mbean.Usage.used
            entryList.add([
                    CounterType: "COUNTER",
                    Endpoint   : entry.hostname,
                    Timestamp  : entry.timestamp,
                    Step       : entry.step,
                    TAGS       : "type=${type}",
                    Metric     : combine(metricPrefix, cls, type, it, "used"),
                    Value      : used
            ])
        }
    }

    def dumpEntryList() {
        def json = new JsonBuilder()
        json.call(entryList)
        println(json.toPrettyString())
    }
}


trait CatalinaMbean {
    private String _cls = "Catalina"
    String name = "\"http-bio-8080\""

    def setThreadPoolName(name) {
        this.name = name
    }

    def getThreadingPool() {
        def type = "ThreadPool"
        def mbean = getMbean(_cls, type, this.name)
        ["currentThreadCount",
         "currentThreadsBusy",
         "connectionCount"].each {
            entryList.add([
                    CounterType: "GAUGE",
                    Endpoint   : entry.hostname,
                    Timestamp  : entry.timestamp,
                    Step       : entry.step,
                    TAGS       : "type=${type}",
                    Metric     : combine(metricPrefix, _cls, type, it),
                    Value      : mbean."${it}"
            ])
        }
    }

    def getGlobalRequestProcessor() {
        def type = "GlobalRequestProcessor"
        def mbean = getMbean(_cls, type, name)
        ["bytesReceived", "bytesSent",
         "errorCount", "requestCount"].each {
            entryList.add([
                    CounterType: "COUNTER",
                    Endpoint   : entry.hostname,
                    Timestamp  : entry.timestamp,
                    Step       : entry.step,
                    TAGS       : "type=${type}",
                    Metric     : combine(metricPrefix, _cls, type, it),
                    Value      : mbean."${it}"
            ])
        }
    }
}


class Jdk7MBean extends JavaLangMbean implements CatalinaMbean {
    // jdk 版本小于 7u69

    String EdenSpace = "Eden Space"
    String SurvivorSpace = "Survivor Space"
    String TenuredGen = "Tenured Gen"
    String PermGen = "Perm Gen"
    String CodeCache = "Code Cache"
    def zones = [EdenSpace, SurvivorSpace, TenuredGen, PermGen, CodeCache]

    Jdk7MBean(ip, port) { super(ip, port) }
}

class Jdk7u69PlusMBean extends JavaLangMbean implements CatalinaMbean {
    String EdenSpace = "PS Eden Space"
    String SurvivorSpace = "PS Survivor Space"
    String TenuredGen = "PS Old Gen"
    String PermGen = "PS Perm Gen"
    String CodeCache = "Code Cache"
    def zones = [EdenSpace, SurvivorSpace, TenuredGen, PermGen, CodeCache]

    Jdk7u69PlusMBean(ip, port) { super(ip, port) }
}

class Jdk8MBean extends JavaLangMbean implements CatalinaMbean {
    // Tenured Gen is renamed to PS Old Gen
    // Perm gen is moved to MetaSpace

    String EdenSpace = "PS Eden Space"
    String SurvivorSpace = "PS Survivor Space"
    String TenuredGen = "PS Old Gen"
    String PermGen = "Metaspace"
    String CodeCache = "Code Cache"
    String CompressedClassSpace = "Compressed Class Space" // this only opened on X86_64 in JDK8
    def zones = [EdenSpace, SurvivorSpace, TenuredGen, PermGen, CodeCache, CompressedClassSpace]

    Jdk8MBean(ip, port) { super(ip, port) }
}


static def createJdkMbean(ip, port) {
    // port:JXM port
    def jdkVersion = System.getProperty("java.version")
    def mb
    if (jdkVersion.startsWith("1.7")) {
        // 获取jdk小版本号 1.7.0_69 -> 69
        def smallVersion = jdkVersion.tokenize('.')[-1].tokenize('_')[-1].toInteger()
        mb = (smallVersion >= 69)? new Jdk7u69PlusMBean(ip, port) : new Jdk7MBean(ip, port)
    } else if (jdkVersion.startsWith("1.8")) {
        mb = new Jdk8MBean(ip, port)
        // jdk 1.8 use nio instead of bio
        mb.setThreadPoolName("\"http-nio-8080\"")
    }
    assert mb != null
    mb
}

// main
static def main() {
    def mb = createJdkMbean("localhost", 10053)

    // jvm monitor info
    mb.getThreading()
    mb.getMemory()
    mb.getMemoryPool()

    // Catalina monitor info
    mb.getThreadingPool()
    mb.getGlobalRequestProcessor()

    // dump to stdout
    mb.dumpEntryList()
}

main()
