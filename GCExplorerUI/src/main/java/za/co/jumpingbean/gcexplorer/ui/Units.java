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
