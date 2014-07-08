/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.jumpingbean.gc.service;

import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Set;
import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import za.co.jumpingbean.gc.service.constants.EdenSpace;
import za.co.jumpingbean.gc.service.constants.OldGenerationCollector;
import za.co.jumpingbean.gc.service.constants.OldGenerationSpace;
import za.co.jumpingbean.gc.service.constants.PermGen;
import za.co.jumpingbean.gc.service.constants.SurvivorSpace;
import za.co.jumpingbean.gc.service.constants.YoungGenerationCollector;
import za.co.jumpingbean.gc.testApp.jmx.GCGeneratorMBean;

/**
 *
 * @author mark
 */
public class JMXQueryRunner {

    private MBeanServerConnection server;
    private GarbageCollectorMXBean oldGenCollector;
    private GarbageCollectorMXBean youngGenCollector;
    private MemoryPoolMXBean edenSpace;
    private MemoryPoolMXBean survivorSpace;
    private MemoryPoolMXBean permGenSpace;
    private MemoryPoolMXBean oldGenSpace;
    private GCGeneratorMBean garbageGenerator;

    private JMXQueryRunner(String port) throws IOException {
        HashMap<String, String> map = new HashMap<>();
        map.put("jmx.remote.x.request.waiting.timeout", "3000");
        JMXConnector conn = null;
        try {
            JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi:"
                    + "//127.0.0.1:" + port + "/jmxrmi");
            conn = JMXConnectorFactory.connect(url, map);
            server = conn.getMBeanServerConnection();
        } catch (IOException ex) {
            if (conn != null) {
                conn.close();
            }
            server = null;
            throw new IOException("Time out connecting to JMX port.");
        }
    }

    public static JMXQueryRunner createJXMQueryRunner(String port) throws
            IOException {
        JMXQueryRunner qry = new JMXQueryRunner(port);
        qry.init();
        return qry;
    }

    /**
     * Must be called after JMXConnection creation to initialise MBeans
     */
    public void init() {
        try {
            this.getCollectors();
            this.getMemoryPools();
            this.getGCGeneratorBean();
        } catch (IOException | MalformedObjectNameException ex) {
            throw new IllegalStateException("error initialising gc and memory pool mbeans");
        }
    }

    private void getCollectors() throws MalformedObjectNameException,
            IOException, IllegalStateException {
        Set<ObjectName> gcNames = server.queryNames(new ObjectName(
                ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE
                + ",name=*"), null);
        for (ObjectName objName : gcNames) {
            GarbageCollectorMXBean bean = (ManagementFactory.
                    newPlatformMXBeanProxy(server, objName.toString(),
                            GarbageCollectorMXBean.class));
            if (OldGenerationCollector.isMember(bean.getName())) {
                oldGenCollector = bean;
            } else if (YoungGenerationCollector.isMember(bean.getName())) {
                youngGenCollector = bean;
            } else {
                throw new IllegalStateException("Collector not found");
            }
        }
    }

    private void getMemoryPools() throws MalformedObjectNameException,
            IOException, IllegalStateException {
        Set<ObjectName> memPoolNames = server.queryNames(new ObjectName(
                ManagementFactory.MEMORY_POOL_MXBEAN_DOMAIN_TYPE
                + ",name=*"), null);
        for (ObjectName objName : memPoolNames) {
            MemoryPoolMXBean bean = ManagementFactory.newPlatformMXBeanProxy(server,
                    objName.toString(), MemoryPoolMXBean.class);
            if (EdenSpace.isMember(bean.getName())) {
                this.edenSpace = bean;
            } else if (SurvivorSpace.isMember(bean.getName())) {
                this.survivorSpace = bean;
            } else if (PermGen.isMember(bean.getName())) {
                this.permGenSpace = bean;
            } else if (OldGenerationSpace.isMember(bean.getName())) {
                this.oldGenSpace = bean;
            } else if (bean.getName().equals("Compressed Class Space")
                    || bean.getName().equals("Code Cache")) {
                //Not mapped yet, from G1 collector
            } else {
                throw new IllegalStateException("Memory pool not found " + bean.getName());
            }
        }
    }

    private void getGCGeneratorBean() throws IOException, MalformedObjectNameException {
        ObjectName objName = new ObjectName("JumpingBean:name=GCGenerator");
        garbageGenerator = JMX.newMBeanProxy(server, objName, GCGeneratorMBean.class);
    }

    public GarbageCollectorMXBean getOldGenCollector() {
        return oldGenCollector;
    }

    public GarbageCollectorMXBean getYoungGenCollector() {
        return youngGenCollector;
    }

    public MemoryPoolMXBean getEdenSpace() {
        return edenSpace;
    }

    public MemoryPoolMXBean getSurvivorSpace() {
        return survivorSpace;
    }

    public MemoryPoolMXBean getPermGenSpace() {
        return permGenSpace;
    }

    public MemoryPoolMXBean getOldGenSpace() {
        return oldGenSpace;
    }

    public GCGeneratorMBean getGCGenerator(){
        return this.garbageGenerator;
    }

    public String getGCInfo() {
        DecimalFormat df = new DecimalFormat("#,###,##0.000");
        StringBuilder str = new StringBuilder("Young GCs: Count ");
        str.append(youngGenCollector.getCollectionCount()).append("  ");
        str.append("Time ").append(df.format((double)youngGenCollector.getCollectionTime()/1000d));
        str.append("  |  ");
        str.append("Old Gen GCs: Count").append(oldGenCollector.getCollectionCount()).append("   ");
        str.append("Time ").append(df.format((double)oldGenCollector.getCollectionTime()/1000d));
        return str.toString();
    }
}
