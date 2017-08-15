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

## test

### groovy script test (Deprecated)

install groovy first

run test from commandline
```
groovy -cp ./src test/CatalinaMonitorTest.groovy dump
```

### golang JSON unmarshal test
set TMPDIR environment before run test
```
export TMPDIR=`pwd`
```
run golang json unmarshal test
```
go get github.com/open-falcon/common/model
go test test/json_unmarshal_test.go -v
```

## data format
```
[
    {
        "CounterType": "GAUGE",
        "Endpoint": "web-PC",
        "Timestamp": 1487059682,
        "Step": 60,
        "TAGS": "domain=java.lang,type=Threading,name=ThreadCount",
        "Metric": "jvm.java.lang.Threading.ThreadCount",
        "Value": 140
    },
    {
        "CounterType": "COUNTER",
        "Endpoint": "web-PC",
        "Timestamp": 1487059682,
        "Step": 60,
        "TAGS": "domain=Catalina,type=GlobalRequestProcessor,name=requestCount",
        "Metric": "jvm.Catalina.GlobalRequestProcessor.requestCount",
        "Value": 1166
    }
    ...
]
```
