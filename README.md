# open-falcon-plugins
open-falcon 数据采集脚本


# jvm monitor
JVM options, rmi.port 可以不写
```
-Dcom.sun.management.jmxremote \
-Dcom.sun.management.jmxremote.rmi.port=7088 \
-Dcom.sun.management.jmxremote.port=10053 \
-Dcom.sun.management.jmxremote.ssl=false \
-Dcom.sun.management.jmxremote.authenticate=false
```