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
package za.co.jumpingbean.gcexplorer.model;

import java.util.Objects;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author mark
 */
public class DataItem {

    private final StringProperty description;
    private final DoubleProperty measure;

    public DataItem(String description, Number value){
        this.description = new SimpleStringProperty(description);
        this.measure = new SimpleDoubleProperty(value.doubleValue());
    }
    
    public String getDescription() {
        return description.getValue();
    }
    
    public void setDescription(String description){
        this.description.setValue(description);
    }

    public StringProperty desciptionProperty() {
        return description;
    }
    

    public DoubleProperty measureProperty() {
        return measure;
    }

    public Double getMeasure(){
        return measure.doubleValue();
    }
    
    public void setMeasure(Number value) {
        this.measure.set(value.doubleValue());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.description);
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
        final DataItem other = (DataItem) obj;
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        return true;
    }
  
}
