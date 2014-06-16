/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.jumpingbean.gc.service;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author mark
 */
public class ProcessParams {

    private String port;
    private String classPath;
    private String mainClass;
    private List<String> gcOptions;

    public ProcessParams(String port, String classPath, String mainClass) {
        checkNullEmptyString(port);
        checkValidPort(port);
        checkNullEmptyString(mainClass);
        if (classPath==null){
            classPath="";
        }
        this.port = port;
        this.classPath = classPath;
        this.mainClass = mainClass;
        this.gcOptions = new LinkedList<>();
    }

    public ProcessParams(String port, String classPath, String mainClass, List<String> gcOptions) {
        this(port, classPath, mainClass);
        this.gcOptions = gcOptions;
    }

    public void addGCOption(String option) {
        checkNullEmptyString(option);
        gcOptions.add(option);
    }

    public void removeGCOption(String option) {
        gcOptions.remove(option);
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        checkNullEmptyString(port);
        checkValidPort(port);
        this.port = port;
    }

    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
        if (classPath==null){
            classPath="";
        }
        this.classPath = classPath;
    }

    public String getMainClass() {
        return mainClass;
    }

    public void setMainClass(String mainClass) {
        checkNullEmptyString(mainClass);
        this.mainClass = mainClass;
    }

    public List<String> getGcOptions() {
        return gcOptions;
    }

    public void setGcOptions(List<String> gcOptions) {
        this.gcOptions = gcOptions;
    }

    private void checkNullEmptyString(String string) throws IllegalArgumentException {
        if (string == null || string.isEmpty()) {
            throw new IllegalArgumentException("parameter may not be null or empty");
        }
    }

    private void checkValidPort(String port) {
        Integer portNum;
        try {
            portNum = Integer.parseInt(port);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("port must be a valid number between 1024 and 65535");
        }
        if (portNum > 1024 && portNum < 65535) {
            return;
        } else {
            throw new IllegalArgumentException("port must be a valid number between 1024 and 65535");
        }
    }

}
