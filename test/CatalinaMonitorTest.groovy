import org.junit.Test
import org.junit.After
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import groovy.json.JsonBuilder
import CatalinaMonitor

/**
 * Created on 2017/2/14.
 * author web 
 */


@RunWith(JUnit4.class)
class CatalinaMonitorTest extends GroovyTestCase {
    CatalinaMonitor cm = new CatalinaMonitor("10.211.18.5", 10053)

    @After
    void prettyPrint() {
        def json = new JsonBuilder()
        json.call(cm.entryList)
        println("beginning dump data ...")
        print(json.toPrettyString())
    }

    @Test
    void getAttribute() {
        def vmName = cm.getAttribute("java.lang:type=Runtime", "VmName")
        assertEquals(vmName, "Java HotSpot(TM) 64-Bit Server VM")
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
        cm.catalinaFetchALL()
    }
}