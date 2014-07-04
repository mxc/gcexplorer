/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.jumpingbean.gcexplorer.ui;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import za.co.jumpingbean.gcexplorer.model.UUIDProcess;

/**
 *
 * @author mark
 */
public class CellNumberFormatter implements Callback<TableColumn<UUIDProcess, Number>, TableCell<UUIDProcess, Number>> {

    private final Main app;

    public CellNumberFormatter(Main app) {
            this.app = app;
    }

    @Override
    public TableCell<UUIDProcess, Number> call(TableColumn<UUIDProcess, Number> param) {
        TextFieldTableCell<UUIDProcess, Number> cellFormatter = new TextFieldTableCell<>(
                new StringConverter<Number>() {

                    DecimalFormat fmt = new DecimalFormat();

                    @Override
                    public String toString(Number num) {
                        BigDecimal tmp = new BigDecimal(num.doubleValue());
                        tmp.setScale(3, RoundingMode.HALF_UP);
                        if (app.getUnits().equals(Units.B)) {
                            fmt.setMaximumFractionDigits(0);
                            fmt.setMinimumFractionDigits(0);
                            return fmt.format(tmp.doubleValue());
                        } else {
                            fmt.setMaximumFractionDigits(3);
                            fmt.setMinimumFractionDigits(3);
                            return fmt.format(tmp.doubleValue());
                        }
                    }

                    @Override

                    public Double fromString(String string) {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                });
        cellFormatter.setAlignment(Pos.CENTER_RIGHT);
        return cellFormatter;
    }
}

