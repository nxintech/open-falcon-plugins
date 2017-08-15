import org.junit.Test
import org.junit.Assert
import groovy.json.JsonBuilder
import CatalinaMonitor

/**
 * Created on 2017/2/14.
 * author web 
 */


class CatalinaMonitorTest {
    CatalinaMonitor cm = new CatalinaMonitor("localhost", 10053)

//    @After
    void prettyPrint() {
        def json = new JsonBuilder()
        json.call(cm.entryList)
        print(json.toPrettyString())
    }

    @Test
    void getAttribute() {
        def vmName = cm.getAttribute("java.lang:type=Runtime", "VmName")
        Assert.assertEquals(vmName, "Java HotSpot(TM) 64-Bit Server VM")
    }

    /**
     * Mbean domain: java.lang
     */
    @Test
    void ProcessCpuLoad() { cm.ProcessCpuLoad() }

    // java.lang:type=Threading
    @Test
    void Threading() { cm.Threading() }

    // java.lang:type=Memory
    @Test
    void Memory() { cm.Memory() }

    // java.lang:type=MemoryPool
    @Test
    void MemoryPool() { cm.MemoryPool() }

    // java.lang:type=GarbageCollector
    @Test
    void GarbageCollector() { cm.GarbageCollector() }

    @Test
    void javalangFetchAll() { cm.javalangFetchAll() }


    /**
     * Mbean domain: Catalina
     */
    @Test
    void ThreadPool() { cm.ThreadPool() }

    @Test
    void GlobalRequestProcessor() { cm.GlobalRequestProcessor() }


    /**
     * open-falcon data dump
     */
    @Test
    void fetchAll() {
        cm.javalangFetchAll()
        cm.catalinaFetchAll()
    }

    @Test
    void dump() {
        fetchAll()
        cm.dumpEntryList()
    }
}