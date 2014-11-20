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

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author mark
 */
public class ProcessParams {

    private String port;
    private String classPath;
    private String mainClass;
    private List<String> gcOptions;
    private String logFilename="";

    //private List<String> filterOptions;
    public ProcessParams(String port, String classPath, String mainClass) {
        checkNullEmptyString(port);
        checkValidPort(port);
        checkNullEmptyString(mainClass);
        if (classPath == null) {
            classPath = "";
        }
        this.port = port;
        this.classPath = classPath;
        this.mainClass = mainClass;
        this.gcOptions = new LinkedList<>();
    }

    public ProcessParams(String port, String classPath, String mainClass, List<String> gcOptions) {
        this(port, classPath, mainClass);
        this.gcOptions = gcOptions;
        Pattern pattern = Pattern.compile("-Xloggc:(gc-\\d+\\.log)");
        for (String option : gcOptions) {
            Matcher matcher = pattern.matcher(option);
            if (matcher.find()) {
                logFilename = matcher.group(1);
            }
        }
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
        if (classPath == null) {
            classPath = "";
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

    public String getStartupParameters() {
        StringBuilder str = new StringBuilder("System Info:\n\r");
        for (String tmpString : gcOptions) {
            str.append(tmpString).append("\n\r");
        }
        return str.toString();
    }

    String getLogFilename() {
        return this.logFilename;
    }

}
