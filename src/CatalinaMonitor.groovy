import javax.management.ObjectName
import javax.management.remote.JMXServiceURL
import javax.management.MBeanServerConnection
import javax.management.remote.JMXConnectorFactory
import groovy.json.JsonBuilder

/**
 * Created on 2016/4/7.
 * author web
 * 需要 JVM 开启 JMX
 *
 */


trait JavaLangMbean {
    private String domain = "java.lang"

    def ProcessCpuLoad() {
        def type = "OperatingSystem"
        def name = "$domain:type=$type"
        entryList.add([
                CounterType: "GAUGE",
                Endpoint   : hostname,
                Timestamp  : timestamp,
                Step       : step,
                TAGS       : "domian=$domain,type=$type,name=ProcessCpuLoad",
                Metric     : combine(domain, type, "ProcessCpuLoad"),
                Value      : new BigDecimal(getAttribute(name, "ProcessCpuLoad")).setScale(2, BigDecimal.ROUND_HALF_UP)
        ])
    }

    def Threading() {
        def type = "Threading"
        def name = "$domain:type=$type"
        ["ThreadCount", "PeakThreadCount"].each {
            entryList.add([
                    CounterType: "GAUGE",
                    Endpoint   : hostname,
                    Timestamp  : timestamp,
                    Step       : step,
                    TAGS       : "domain=$domain,type=$type,name=$it",
                    Metric     : combine(domain, type, it),
                    Value      : getAttribute(name, it)
            ])
        }
    }

    def Memory() {
        def type = "Memory"
        def name = "$domain:type=$type"
        ["HeapMemoryUsage", "NonHeapMemoryUsage"].each {
            def used = getAttribute(name, it).used
            def max = getAttribute(name, it).max
            entryList.add([
                    CounterType: "GAUGE",
                    Endpoint   : hostname,
                    Timestamp  : timestamp,
                    Step       : step,
                    TAGS       : "domain=$domain,type=$type,name=$it",
                    Metric     : combine(domain, type, it, "used"),
                    Value      : used
            ])
            entryList.add([
                    CounterType: "GAUGE",
                    Endpoint   : hostname,
                    Timestamp  : timestamp,
                    Step       : step,
                    TAGS       : "domian=$domain,type=$type,name=$it",
                    Metric     : combine(domain, type, it, "usedPercent"),
                    Value      : getPercent(used, max)
            ])
        }

    }

    def MemoryPool() {
        def type = "MemoryPool"
        def zones = serverConnection.queryNames(new ObjectName("$domain:type=$type,name=*"), null)
        zones.each {
            def zoneName = it.toString().split(/java\.lang:type=$type,name=/)[1]
            def usage = getAttribute("$domain:type=$type,name=$zoneName", "Usage")
            entryList.add([
                    CounterType: "GAUGE",
                    Endpoint   : hostname,
                    Timestamp  : timestamp,
                    Step       : step,
                    TAGS       : "domian=$domain,type=$type,name=$zoneName",
                    Metric     : combine(domain, type, zoneName, "used"),
                    Value      : usage.used
            ])
            entryList.add([
                    CounterType: "GAUGE",
                    Endpoint   : hostname,
                    Timestamp  : timestamp,
                    Step       : step,
                    TAGS       : "domian=$domain,type=$type,name=$zoneName",
                    Metric     : combine(domain, type, zoneName, "usedPercent"),
                    Value      : getPercent(usage.used, usage.max)
            ])
        }
    }

    def GarbageCollector() {
        def type = "GarbageCollector"
        def collectors = serverConnection.queryNames(new ObjectName("$domain:type=$type,name=*"), null)
        collectors.each {
            def collectorName = it.toString().split(/java\.lang:type=$type,name=/)[1]
            ["CollectionCount", "CollectionTime"].each {
                entryList.add([
                        CounterType: "GAUGE",
                        Endpoint   : hostname,
                        Timestamp  : timestamp,
                        Step       : step,
                        TAGS       : "domian=$domain,type=$type,name=$collectorName",
                        Metric     : combine(domain, type, collectorName, it),
                        Value      : getAttribute("$domain:type=$type,name=$collectorName", it)
                ])
            }
        }

    }

    def javalangFetchAll() {
        Threading()
        Memory()
        MemoryPool()
        ProcessCpuLoad()
        GarbageCollector()
    }

}


trait CatalinaMbean {
    private String domain = "Catalina"

    def ThreadPool() {
        def type = "ThreadPool"
        def name = "$domain:type=$type,name=$protocolHandler"
        ["currentThreadCount", "currentThreadsBusy", "connectionCount"].each {
            entryList.add([
                    CounterType: "GAUGE",
                    Endpoint   : hostname,
                    Timestamp  : timestamp,
                    Step       : step,
                    TAGS       : "domain=$domain,type=$type,name=$it",
                    Metric     : combine(domain, type, it),
                    Value      : getAttribute(name, it)
            ])
        }
    }

    def GlobalRequestProcessor() {
        def type = "GlobalRequestProcessor"
        def name = "$domain:type=$type,name=$protocolHandler"
        ["bytesReceived", "bytesSent", "errorCount", "requestCount"].each {
            entryList.add([
                    CounterType: "COUNTER",
                    Endpoint   : hostname,
                    Timestamp  : timestamp,
                    Step       : step,
                    TAGS       : "domain=$domain,type=$type,name=$it",
                    Metric     : combine(domain, type, it),
                    Value      : getAttribute(name, it)
            ])
        }
    }

    def catalinaFetchALL() {
        ThreadPool()
        GlobalRequestProcessor()
    }
}


class JvmMonitor {
    protected MBeanServerConnection serverConnection
    protected final String hostname
    protected final String timestamp
    protected final int step

    static getPercent(numerator, denominator) {
        if (denominator < 0) { return 0 }
        BigDecimal res = (numerator / denominator) * 100
        res.setScale(2, BigDecimal.ROUND_HALF_UP)
    }

    JvmMonitor(String host, int port) {
        hostname = InetAddress.getLocalHost().getHostName()
        timestamp = new Date().getTime().intdiv(1000).toString()
        step = 60
        serverConnection = JMXConnectorFactory.connect(new JMXServiceURL("service:jmx:rmi:///jndi/rmi://$host:$port/jmxrmi")).MBeanServerConnection
    }
    def getAttribute(String name, String property) { serverConnection.getAttribute(new ObjectName(name), property) }

    def combine = { String... names ->
        def res = ["jvm"]
        names.each {
            res.add(it.replace(" ", ""))
        }
        res.join('.')
    }
}

class CatalinaMonitor extends JvmMonitor implements JavaLangMbean, CatalinaMbean {

    private final String connectorPort
    private final String protocolHandler
    // A entry is the data that pushed to open-falcon transfer
    def entryList = []

    def dumpEntryList() {
        def json = new JsonBuilder()
        json.call(entryList)
        println(json.toString())
    }

    CatalinaMonitor(String host, int port) {
        super(host, port)
        def connectorName = getAttribute("Catalina:type=Service", "connectorNames")[0].toString()
        connectorPort = connectorName.tokenize(",")[1].tokenize("=")[1]
        // "http-bio-$connectorPort" or "http-nio-$connectorPort"
        protocolHandler = getAttribute("Catalina:type=ProtocolHandler,port=$connectorPort", "name")
    }
}
