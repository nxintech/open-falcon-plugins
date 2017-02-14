#!/usr/local/env groovy

import CatalinaMonitor

static void main() {
    def cm = new CatalinaMonitor("localhost", 10053)
    cm.javalangFetchAll()
    cm.catalinaFetchALL()
    cm.dumpEntryList()
}
main()