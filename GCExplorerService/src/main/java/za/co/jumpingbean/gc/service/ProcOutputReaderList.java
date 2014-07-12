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
