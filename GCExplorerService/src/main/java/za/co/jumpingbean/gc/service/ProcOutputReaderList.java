/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.jumpingbean.gc.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mark
 */
public class ProcOutputReaderList extends ArrayList<String> {

    final String[] output = new String[200];
    private Integer counter = 0;
    Thread thread;
    private final BufferedReader reader;
    private boolean isRunning = true;
    private Process proc;

    public ProcOutputReaderList(Process proc, BufferedReader reader) {
        if (proc.isAlive()) {
            isRunning = true;
        } else {
            isRunning = false;
        }
        this.proc = proc;
        this.reader = reader;
        Arrays.fill(output, "");
    }

    public void init() {
        thread = new Thread(() -> {
            try {
                while (isRunning) {
                    if (proc.isAlive()) {
                        String str = reader.readLine();
                        if (str != null) {
                            ProcOutputReaderList.this.addString(str);
                        }
                    } else {
                        isRunning = false;
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(ProcOutputReaderList.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        thread.setName("Process output reader");
        thread.setDaemon(true);
        thread.start();
    }

    public void addString(String msg) {
        synchronized (output) {
            output[counter] = msg;
            if (counter >= 199) {
                counter = 0;
            } else {
                counter++;
            }
        }
    }

    public List<String> readList() {
        List<String> tmp = new LinkedList<>();
        synchronized (output) {
            for (String str : output) {
                tmp.add(str);
            }
        }
        Arrays.fill(output, 0, 199, "");
        counter = 0;
        return tmp;
    }

    void stop() {
        this.isRunning = false;
    }

}
