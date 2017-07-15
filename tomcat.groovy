import CatalinaMonitor

def cm = new CatalinaMonitor("localhost", 10053)
cm.javalangFetchAll()
cm.catalinaFetchAll()
cm.dumpEntryList()