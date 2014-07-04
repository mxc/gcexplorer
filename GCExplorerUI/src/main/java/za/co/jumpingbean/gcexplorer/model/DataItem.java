/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
