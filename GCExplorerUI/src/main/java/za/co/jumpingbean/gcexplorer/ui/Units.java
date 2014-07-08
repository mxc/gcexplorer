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
    GB((int) Math.pow(1024,3),"GigaBytes"),MB((int) Math.pow(1024,2),"MegaBytes"),KB(1024,"KiloBytes"),B(1,"Bytes");
    
    private final int conversionUnits;
    private final String name;
    
    Units(int conversionUnits,String name){
            this.conversionUnits=conversionUnits;
            this.name=name;
    }

    public String getName() {
        return name;
    }
    
    
    public int getConversionUnits(){
        return this.conversionUnits;
    }
}
