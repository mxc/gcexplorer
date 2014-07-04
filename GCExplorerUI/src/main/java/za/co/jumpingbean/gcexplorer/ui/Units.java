package za.co.jumpingbean.gcexplorer.ui;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mark
 */
public enum Units {
    GB((int) Math.pow(1024,3)),MB((int) Math.pow(1024,2)),KB(1024),B(1);
    
    private final int conversionUnits;
    
    Units(int conversionUnits){
            this.conversionUnits=conversionUnits;
    }
    
    public int getConversionUnits(){
        return this.conversionUnits;
    }
}
