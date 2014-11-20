/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.jumpingbean.gcexplorer.ui;

import java.util.List;
import java.util.UUID;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.WindowEvent;

/**
 *
 * @author mark
 */
public class GCLogViewForm implements ListChangeListener, EventHandler<WindowEvent> {

    private final GCExplorer app;
    private final UUID procId;

    @FXML
    private TextArea txtGCLogView;
    private final ProcessViewForm form;

    public GCLogViewForm(GCExplorer app, UUID procId,ProcessViewForm form) {
        this.app = app;
        this.procId = procId;
        this.form = form;
        app.getProcessController().getUUIDProcess(procId).addGCLogViewer(this);
    }

    @Override
    public void onChanged(Change change) {
        change.next();
        List<String> list = change.getAddedSubList();
        if (list.isEmpty()) {
            return;
        }
        StringBuilder buf = new StringBuilder();
        for (String str : list) {
            if (str.isEmpty()) {
                continue;
            }
            buf.append(str);
        }
        if (buf.length() > 0) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    txtGCLogView.appendText(buf.toString());
                }
            });
        }
    }

    @Override
    public void handle(WindowEvent event) {
        app.getProcessController().getUUIDProcess(procId).removeLogListener(this);
        form.resetGCLogView();
    }

}
