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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import za.co.jumpingbean.gc.service.enums.EdenSpace;
import za.co.jumpingbean.gc.service.enums.JVMCollector;
import za.co.jumpingbean.gc.service.enums.OldGenerationCollector;
import za.co.jumpingbean.gc.service.enums.OldGenerationSpace;
import za.co.jumpingbean.gc.service.enums.PermGen;
import za.co.jumpingbean.gc.service.enums.SurvivorSpace;
import za.co.jumpingbean.gc.service.enums.YoungGenerationCollector;

/**
 *
 * @author mark
 */
public class JMXQueryRunner {

    private final MBeanServerConnection server;
    private GarbageCollectorMXBean oldGenCollector;
    private GarbageCollectorMXBean youngGenCollector;
    private MemoryPoolMXBean edenSpace;
    private MemoryPoolMXBean survivorSpace;
    private MemoryPoolMXBean permGen;
    private MemoryPoolMXBean oldGen;


    public JMXQueryRunner(String port) throws IOException, MalformedObjectNameException {
        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi:"
                + "//127.0.0.1:" + port + "/jmxrmi");
        JMXConnector conn = JMXConnectorFactory.connect(url);
        server = conn.getMBeanServerConnection();
    }

    public void init() {
        try {
            this.getCollectors();
            this.getMemoryPools();
        } catch (IOException | MalformedObjectNameException ex) {
            throw new IllegalStateException("error initialising gc and memory pool mbeans");
        }
    }

    private void getCollectors() throws MalformedObjectNameException,
            IOException {
        Set<ObjectName> gcNames = server.queryNames(new ObjectName(
                ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE
                + ",name=*"), null);
        for (ObjectName objName : gcNames) {
            GarbageCollectorMXBean bean = (ManagementFactory.
                    newPlatformMXBeanProxy(server, objName.toString(),
                            GarbageCollectorMXBean.class));
            try {
                OldGenerationCollector.getEnum(bean.getName());
                oldGenCollector = bean;
            } catch (IllegalArgumentException ex) {
                YoungGenerationCollector.getEnum(bean.getName());
                youngGenCollector = bean;
            }
        }
    }

    private void getMemoryPools() throws MalformedObjectNameException,
            IOException {
        Set<ObjectName> memPoolNames = server.queryNames(new ObjectName(
                ManagementFactory.MEMORY_POOL_MXBEAN_DOMAIN_TYPE
                + ",name=*"), null);
        for (ObjectName objName : memPoolNames) {
            MemoryPoolMXBean bean = ManagementFactory.newPlatformMXBeanProxy(server,
                    objName.toString(), MemoryPoolMXBean.class);
            try {
                EdenSpace.getEnum(bean.getName());
                this.edenSpace = bean;
            } catch (IllegalArgumentException ex) {

            }
            try {
                SurvivorSpace.getEnum(bean.getName());
                this.survivorSpace = bean;
            } catch (IllegalArgumentException ex) {

            }
            try {
                PermGen.getEnum(bean.getName());
                this.permGen = bean;
            } catch (IllegalArgumentException ex) {

            }
            try {
                OldGenerationSpace.getEnum(bean.getName());
                this.oldGen = bean;
            } catch (IllegalArgumentException ex) {

            }
        }
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

    public MemoryPoolMXBean getPermGen() {
        return permGen;
    }

    public MemoryPoolMXBean getOldGen() {
        return oldGen;
    }


}
