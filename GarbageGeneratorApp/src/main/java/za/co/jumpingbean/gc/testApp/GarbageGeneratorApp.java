/* 
 * Copyright (C) 2014 Mark Clarke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package za.co.jumpingbean.gc.testApp;


import za.co.jumpingbean.gc.testApp.jmx.GCGenerator;
import java.lang.management.ManagementFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author mark
 */
public class GarbageGeneratorApp {

    private MBeanServer server;
    private final GCGenerator bean;

    public GarbageGeneratorApp() {
        server = ManagementFactory.getPlatformMBeanServer();
        System.out.println("Starting up a new GC Generator App");
        //Analiser analiser = new Analiser(new MetricRegistry());
        LocalObjectGenerator sl = new LocalObjectGenerator();
        LongLivedObjectGenerator ll = new LongLivedObjectGenerator();

        bean = new GCGenerator(sl, ll, this);
        try {
            ObjectName name = new ObjectName("JumpingBean:name=GCGenerator");
            server.registerMBean(bean, name);
        } catch (InstanceAlreadyExistsException | MBeanRegistrationException |
                MalformedObjectNameException | NotCompliantMBeanException ex) {
            System.out.println("Error initialising MBean. Exiting...");
            System.exit(1);
        }
    }

    public boolean isRunning() {
        return bean.isRunning();
    }

    public static void main(String[] args) {
        System.out.println("Starting Generator....");
        GarbageGeneratorApp gen = new GarbageGeneratorApp();
        synchronized (gen) {
            System.out.println("Generator started...");
            while (gen.isRunning()) {
                try {
                    System.out.println("waiting....");
                    gen.wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(GarbageGeneratorApp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
