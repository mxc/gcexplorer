/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
