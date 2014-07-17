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

    private final GCExplorer app;

    public CellNumberFormatter(GCExplorer app) {
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

