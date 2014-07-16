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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mark Clarke
 */
public class ControlableProcess extends ProcessObject {

    private final ProcessParams params;
    private final Process process;
    private final ProcOutputReaderList procOutputReader;

    public ControlableProcess(String name,Process proc, ProcessParams params,
            ProcOutputReaderList output, JMXQueryRunner qry) {
        super(name,qry);
        this.process = proc;
        this.params = params;
        procOutputReader = output;
    }

    public ProcessParams getParams() {
        return params;
    }
    
    public Process getProcess() {
        return process;
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
