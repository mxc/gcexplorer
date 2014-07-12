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

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mark
 */
public class ProcessObject {

    private final JMXQueryRunner qry;
    private final ProcessParams params;
    private final Process process;
    private final UUID id;
    private final ProcOutputReaderList procOutputReader;

    ProcessObject(Process proc, ProcessParams params, JMXQueryRunner qry,
            ProcOutputReaderList output) {
        this.process = proc;
        this.params = params;
        this.id = UUID.randomUUID();
        this.qry = qry;
        procOutputReader = output;
    }

    public JMXQueryRunner getQry() {
        return qry;
    }

    public ProcessParams getParams() {
        return params;
    }

    public UUID getId() {
        return id;
    }

    public Process getProcess() {
        return process;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.process);
        hash = 29 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ProcessObject other = (ProcessObject) obj;
        if (!Objects.equals(this.process, other.process)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    String readProcessOutputLine() {
        StringBuilder str = new StringBuilder(200);
        List<String> list = procOutputReader.readList();
        for (String tmpStr : list) {
            str.append(tmpStr);
        }
        return str.toString();
    }

    public void stop() {
        this.procOutputReader.stop();
        this.process.destroy();
        try {
            synchronized (process) {
                process.wait(500L);
                process.exitValue();
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(GeneratorService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalThreadStateException ex) {
            throw new IllegalStateException("failed to stop process" + process.toString());
        }            
    }

}
