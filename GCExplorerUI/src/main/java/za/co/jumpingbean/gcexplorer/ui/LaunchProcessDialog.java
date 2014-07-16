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
package za.co.jumpingbean.gcexplorer.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import za.co.jumpingbean.gcexplorer.model.Utils;

/**
 *
 * @author mark
 */
public class LaunchProcessDialog implements Initializable {

    @FXML
    private Button btnLaunch;
    @FXML
    private TextArea txtStatus;
    @FXML
    private ToggleGroup garbageCollectorGroup;
    @FXML
    private RadioButton rdbSerial;
    @FXML
    private RadioButton rdbG1;
    @FXML
    private RadioButton rdbConcMarkSweep;
    @FXML
    private RadioButton rdbParallel;
    @FXML
    private RadioButton rdbParallelOld;
    @FXML
    private RadioButton rdbUseAdaptiveSizePolicy;
    @FXML
    private RadioButton rdbUseCMSInitiatingOccupancyOnly;
    @FXML
    private TextField txtXms;
    @FXML
    private TextField txtXmx;
    @FXML
    private TextField txtNewSize;
    @FXML
    private TextField txtMaxNewSize;
    @FXML
    private TextField txtNewRatio;
    @FXML
    private TextField txtSurvivorRatio;
    @FXML
    private TextField txtPermSize;
    @FXML
    private TextField txtMaxPermSize;
    @FXML
    private TextField txtInitialTenuringThreshold;
    @FXML
    private TextField txtMaxTenuringThreshold;
    @FXML
    private TextField txtCMSInitiatingOccupancyFraction;
    @FXML
    private TextField txtConcGCThreads;
    @FXML
    private TextField txtParallelGCThreads;
    @FXML
    private TextField txtCMSTriggerRatio;
    @FXML
    private TextField txtCMSTriggerPermRatio;
    @FXML
    private TextField txtG1ReservePercent;
    @FXML
    private TextField txtG1HeapRegionSize;
    @FXML
    private TextField txtInitiatingHeapOccupancyPercent;
    @FXML
    private TextField txtParallelCMSThreads;
    @FXML
    private TextField txtMaxGCPauseMillis;
    @FXML
    private RadioButton rdbUseParNewGC;

    private final GCExplorer app;
    private final MainForm parent;

    public LaunchProcessDialog(GCExplorer app, MainForm parent) {
        this.app = app;
        this.parent = parent;
    }

    protected void launchProcess(ActionEvent e) {
        //garbageCollectorGroup.getSelectedToggle();
        UUID procId;
        String selectedGC = (String) garbageCollectorGroup.getSelectedToggle().getUserData();
        List<String> gcOptions;
        try {
            gcOptions = getSelectedOptions();
        } catch (IllegalStateException ex) {
            return;
        }
        try {
            procId = app.getProcessController().launchProcess(selectedGC, gcOptions);
        } catch (IOException |
                IllegalStateException |
                NullPointerException ex) {
            Logger.getLogger(GCExplorer.class.getName()).log(Level.SEVERE, null, ex);
            StringBuilder str = new StringBuilder("Error launching process: ");
            str.append(ex.getMessage());
            txtStatus.setText(str.toString());
            return;
        }

        StringBuilder strBuilder = new StringBuilder(txtStatus.getText());
        strBuilder.append("\n\r");
        strBuilder.append("Process id:");
        strBuilder.append(procId.toString());
        strBuilder.append(" Started with GC:");
        strBuilder.append(selectedGC);
        txtStatus.setText(strBuilder.toString());

        try {
            parent.addPane(procId,false);
            ((Stage)this.btnLaunch.getScene().getWindow()).close();
        } catch (IOException ex) {
            String txt = txtStatus.getText();
            txtStatus.setText(txt + "\n\rThere was an error adding tab to display");
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.rdbConcMarkSweep.setUserData("-XX:+UseConcMarkSweepGC");
        this.rdbSerial.setUserData("-XX:+UseSerialGC");
        this.rdbG1.setUserData("-XX:+UseG1GC");
        this.rdbParallel.setUserData("-XX:+UseParallelGC");
        this.rdbParallelOld.setUserData("-XX:+UseParallelOldGC");
        this.rdbUseAdaptiveSizePolicy.setUserData("-XX:+UseAdaptiveSizePolicy");
        this.rdbUseCMSInitiatingOccupancyOnly.setUserData("-XX:+UseCMSInitiatingOccupancyOnly");

        this.rdbSerial.setOnAction(new EventHandler() {

            @Override
            public void handle(Event event) {
                boolean disable = false;
                if (rdbSerial.isSelected()) {
                    disable = true;
                }
                setCMSOptionsState(disable);
                setCommonCMSG1State(disable);
                setG1OptionsState(disable);
                setParallelGCOptionsState(disable);
                setCommonParallelG1State(disable);
            }

        });

        this.rdbConcMarkSweep.setOnAction(new EventHandler() {

            @Override
            public void handle(Event event) {
                boolean disable = false;
                if (rdbConcMarkSweep.isSelected()) {
                    disable = true;
                }
                setCMSOptionsState(!disable);
                setCommonCMSG1State(!disable);
                setG1OptionsState(disable);
                setParallelGCOptionsState(disable);
                setCommonParallelG1State(disable);
            }

        });

        this.rdbG1.setOnAction(new EventHandler() {

            @Override
            public void handle(Event event) {
                boolean disable = false;
                if (rdbG1.isSelected()) {
                    disable = true;
                }
                setCMSOptionsState(disable);
                setCommonCMSG1State(!disable);
                setG1OptionsState(!disable);
                setParallelGCOptionsState(disable);
                setCommonParallelG1State(!disable);
            }
        });

        this.rdbParallel.setOnAction(new EventHandler() {

            @Override
            public void handle(Event event) {
                boolean disable = false;
                if (rdbParallel.isSelected()) {
                    disable = true;
                }
                setCMSOptionsState(disable);
                setCommonCMSG1State(disable);
                setG1OptionsState(disable);
                setParallelGCOptionsState(!disable);
                setCommonParallelG1State(!disable);
            }

        });

        this.rdbParallelOld.setOnAction(new EventHandler() {

            @Override
            public void handle(Event event) {
                boolean disable = false;
                if (rdbParallelOld.isSelected()) {
                    disable = true;
                }
                setCMSOptionsState(disable);
                setCommonCMSG1State(disable);
                setG1OptionsState(disable);
                setParallelGCOptionsState(!disable);

            }

        });

        rdbUseAdaptiveSizePolicy.setOnAction(new EventHandler() {

            @Override
            public void handle(Event event) {
                if (rdbUseAdaptiveSizePolicy.isSelected() && !rdbUseAdaptiveSizePolicy.isDisabled()) {
                    txtNewRatio.disableProperty().set(true);
                    txtMaxNewSize.disableProperty().set(true);
                    txtNewSize.disableProperty().set(true);
                    txtSurvivorRatio.disableProperty().set(true);
                } else {
                    txtNewRatio.disableProperty().set(false);
                    txtMaxNewSize.disableProperty().set(false);
                    txtNewSize.disableProperty().set(false);
                    txtSurvivorRatio.disableProperty().set(false);
                }
            }

        });

        this.btnLaunch.setOnAction(this::launchProcess);
        this.rdbSerial.fireEvent(new ActionEvent());
    }

    private void setCMSOptionsState(boolean state) {
        rdbUseCMSInitiatingOccupancyOnly.disableProperty().set(state);
        txtCMSInitiatingOccupancyFraction.disableProperty().set(state);
        txtCMSTriggerPermRatio.disableProperty().set(state);
        txtCMSTriggerRatio.disableProperty().set(state);
        rdbUseParNewGC.disableProperty().set(state);
        txtParallelCMSThreads.disableProperty().set(state);
    }

    private void setParallelGCOptionsState(boolean state) {
        this.rdbUseAdaptiveSizePolicy.disableProperty().set(state);
        this.rdbUseAdaptiveSizePolicy.fireEvent(new ActionEvent());
        this.txtMaxGCPauseMillis.disableProperty().set(state);
        txtParallelGCThreads.disableProperty().set(state);
    }

    private void setCommonCMSG1State(boolean state) {
        txtConcGCThreads.disableProperty().set(state);
    }

    private void setCommonParallelG1State(boolean state) {
        txtParallelGCThreads.disableProperty().set(state);
    }

    private void setG1OptionsState(boolean state) {
        txtG1HeapRegionSize.disableProperty().set(state);
        txtG1ReservePercent.disableProperty().set(state);
        txtInitiatingHeapOccupancyPercent.disableProperty().set(state);
    }

    private List<String> getSelectedOptions() {
        ArrayList<String> list = new ArrayList<>();
        StringBuilder errorMsg = new StringBuilder();

        this.addTextFieldOptionBetween(txtCMSInitiatingOccupancyFraction, "-XX:CMSInitiatingOccupancyFraction="
                + txtCMSInitiatingOccupancyFraction.getText(), "CMSInitiatingOccupancyFraction", 0, 100, list, errorMsg);

        this.addTextFieldOption(txtCMSTriggerPermRatio, "-XX:CMSTriggerPermRatio="
                + txtCMSTriggerPermRatio.getText(), "CMSTriggerPermRatio", list, errorMsg);

        this.addTextFieldOption(txtCMSTriggerRatio, "-XX:CMSTriggerRatio="
                + txtCMSTriggerPermRatio.getText(), "CMSTriggerRatio", list, errorMsg);

        this.addTextFieldOption(txtConcGCThreads, "-XX:ConcGCThreads="
                + txtConcGCThreads.getText(), "ConcGCThreads", list, errorMsg);

        this.addTextFieldOption(txtG1HeapRegionSize, "-XX:G1HeapRegionSize="
                + txtG1HeapRegionSize.getText(), "G1HeapRegionSize", list, errorMsg);

        this.addTextFieldOptionBetween(txtG1ReservePercent, "-XX:G1ReservePercent="
                + txtG1ReservePercent.getText(), "G1ReservePercent=", 0, 100, list, errorMsg);

        this.addTextFieldOption(txtInitiatingHeapOccupancyPercent, "-XX:InitiatingHeapOccupancyPercent="
                + txtInitiatingHeapOccupancyPercent.getText(), "InitiatingHeapOccupancyPercent", list, errorMsg);

        this.addTextFieldOption(txtMaxGCPauseMillis, "-XX:MaxGCPauseMillis="
                + txtMaxGCPauseMillis.getText(), "MaxGCPauseMillis", list, errorMsg);

        this.addTextFieldOption(txtMaxNewSize, "-XX:MaxNewSize="
                + txtMaxNewSize.getText() + "m", "MaxNewSize", list, errorMsg);

        this.addTextFieldOption(txtMaxPermSize, "-XX:MaxPermSize="
                + txtMaxPermSize.getText() + "m", "MaxPermSize", list, errorMsg);

        this.addTextFieldOptionBetween(txtMaxTenuringThreshold, "-XX:MaxTenuringThreshold="
                + txtMaxTenuringThreshold.getText(), "MaxTenuringThreshold", 0, 15, list, errorMsg);

        int tmpMax = 0;
        if (!txtMaxTenuringThreshold.getText().isEmpty()) {
            try {
                String text = txtMaxTenuringThreshold.getText();
                tmpMax = Integer.parseInt(text);
                tmpMax = tmpMax > 15 ? 15 : tmpMax;
            } catch (NumberFormatException ex) {
                tmpMax = 0;
            }
        }
        this.addTextFieldOptionBetween(txtInitialTenuringThreshold, "-XX:InitialTenuringThreshold="
                + txtInitialTenuringThreshold.getText(), "InitialTenuringThreshold", 0, tmpMax, list, errorMsg);

        this.addTextFieldOption(txtNewRatio, "-XX:NewRatio="
                + txtNewRatio.getText(), "-XX:NewRatio=", list, errorMsg);

        this.addTextFieldOption(txtNewSize, "-XX:NewSize="
                + txtNewSize.getText() + "m", "-XX:NewSize=", list, errorMsg);

        this.addTextFieldOption(txtParallelCMSThreads, "-XX:ParallelCMSThreads="
                + txtParallelCMSThreads.getText(), "-XX:ParallelCMSThreads=", list, errorMsg);

        this.addTextFieldOption(txtParallelGCThreads, "-XX:ParallelGCThreads="
                + txtParallelGCThreads.getText(), "-XX:ParallelGCThreads=", list, errorMsg);

        this.addTextFieldOption(txtPermSize, "-XX:PermSize="
                + txtPermSize.getText() + "m", "-XX:PermSize==", list, errorMsg);

        this.addTextFieldOption(txtSurvivorRatio, "-XX:SurvivorRatio="
                + txtSurvivorRatio.getText(), "-XX:SurvivorRatio=", list, errorMsg);

        int xms = -1;
        if (!txtXms.isDisabled()
                && !txtXms.getText().isEmpty()) {
            if (isNumber(txtXms.getText())) {
                xms = getNumber(txtXms.getText());
                list.add("-Xms" + txtXms.getText() + "m");
            } else {
                errorMsg.append("-Xms must be a number\n\r");
            }
        }

        int xmx = -1;
        if (!txtXmx.isDisabled()
                && !txtXmx.getText().isEmpty()) {
            if (isNumber(txtXmx.getText())) {
                xmx = Integer.parseInt(txtXmx.getText());
                list.add("-Xmx" + txtXmx.getText() + "m");
            } else {
                errorMsg.append("-Xmx must be a number\n\r");
            }
        }

        if (xms != -1 && xmx != -1 && xms > xmx) {
            errorMsg.append("-Xms cannot be greater than -Xmx");
        }

        if (!rdbUseAdaptiveSizePolicy.isDisabled() && rdbUseAdaptiveSizePolicy.isSelected()) {
            list.add("-XX:+UseAdaptiveSizePolicy");
        }
        if (!rdbUseCMSInitiatingOccupancyOnly.isDisabled() && rdbUseCMSInitiatingOccupancyOnly.isSelected()) {
            list.add("-XX:+UseCMSInitiatingOccupancyOnly");
        }
        if (!rdbUseParNewGC.isDisabled() && rdbUseParNewGC.isSelected()) {
            list.add("-XX:-UseParNewGC");
        }
        if (errorMsg.toString().isEmpty()) {
            return list;
        } else {
            txtStatus.setText(errorMsg.toString());
            throw new IllegalStateException("Invalid parameter inputs");
        }
    }

    private void addTextFieldOption(TextField field, String param, String fieldLabel, List<String> list, StringBuilder errorMsg) {
        if (!field.isDisabled()
                && !field.getText().isEmpty()) {
            if (isNumber(field.getText())) {
                list.add(param);
            } else {
                errorMsg.append(fieldLabel).append(" must be a number\n\r");
            }
        }
    }

//    private void addTextFieldOptionGreaterThan(TextField field, String param, String fieldLabel, int maxVal,
//            List<String> list, StringBuilder errorMsg) {
//        if (!field.isDisabled()
//                && !field.getText().isEmpty()) {
//            if (isNumber(field.getText())) {
//                int num = getNumber(field.getText());
//                if (num < maxVal) {
//                    list.add(param);
//                }else{
//                    errorMsg.append(fieldLabel).append(" must be less than ").append(maxVal).append("\n\r");
//                }
//            } else {
//                errorMsg.append(fieldLabel).append(" must be a number\n\r");
//            }
//        }
//    }
    private void addTextFieldOptionBetween(TextField field, String param, String fieldLabel,
            int minVal, int maxVal, List<String> list, StringBuilder errorMsg) {
        if (!field.isDisabled()
                && !field.getText().isEmpty()) {
            if (isNumber(field.getText())) {
                int num = getNumber(field.getText());
                if (num < maxVal && num > minVal) {
                    list.add(param);
                } else {
                    errorMsg.append(fieldLabel).append(" must be greater than ").
                            append(minVal).append(" less than ").append(maxVal).append("\n\r");
                }
            } else {
                errorMsg.append(fieldLabel).append(" must be a number\n\r");
            }
        }
    }

    private boolean isNumber(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private int getNumber(String text) {
        int number = Integer.parseInt(text);
        return number;
    }

}
