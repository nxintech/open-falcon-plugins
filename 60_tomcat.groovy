#!/usr/local/env groovy

import CatalinaMonitor

static void main() {
    def cm = new CatalinaMonitor("10.211.18.5", 10053)
    cm.javalangFetchAll()
    cm.catalinaFetchALL()
    cm.dumpEntryList()
}
main()