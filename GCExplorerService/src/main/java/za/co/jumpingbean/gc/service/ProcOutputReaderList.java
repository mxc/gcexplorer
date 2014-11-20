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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/**
 *
 * @author mark
 */
public class ProcOutputReaderList extends ArrayList<String> {

    final String[] output = new String[200];
    private Integer head = 0;
    private Integer tail = 0;
    Thread thread;
    private final Scanner reader;
    private boolean isRunning = true;
    private Process proc;

    public ProcOutputReaderList(Process proc, Scanner reader) {
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
            //try {
                while (isRunning) {
                    if (proc.isAlive()) {
                        if (reader.hasNext()) {
                            String str = reader.nextLine();
                            if (str != null && !str.isEmpty()) {
                                ProcOutputReaderList.this.addString(str);
                            }
                        }
                    } else {
                        isRunning = false;
                    }
                }
           // } catch (Exception ex) {
           //     Logger.getLogger(ProcOutputReaderList.class.getName()).log(Level.SEVERE, null, ex);
           // }
        });
        thread.setName("Process output reader");
        thread.setDaemon(true);
        thread.start();
    }

    public void addString(String msg) {
        synchronized (output) {
            System.out.println(msg);
            output[head] = msg;
            if (head >= 199) {
                head = 0;
            } else {
                head++;
            }
        }
    }

    public List<String> readList() {
        List<String> tmp = new LinkedList<>();
        if (Objects.equals(head, tail)) return tmp;
        synchronized (output) {
            if (head < tail) {
                for (int i = tail; i < output.length; i++) {
                    tmp.add(String.format(output[i]));
                }
                for (int i = 0; i <= head; i++) {
                    tmp.add(String.format(output[i]));
                }
            } else {
                for (int i = tail; i <= head; i++) {
                    tmp.add(String.format(output[i]));
                }
            }
            tail = head;
            return tmp;
        }
    }

    void stop() {
        this.isRunning = false;
    }

}
