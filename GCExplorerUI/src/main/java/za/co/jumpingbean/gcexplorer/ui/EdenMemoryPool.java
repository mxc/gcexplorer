/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package za.co.jumpingbean.gcexplorer.ui;

import za.co.jumpingbean.gcexplorer.model.MemoryPool;
import java.sql.Timestamp;

/**
 *
 * @author mark
 */
public class EdenMemoryPool extends MemoryPool {

    public EdenMemoryPool(Timestamp startTime,int numDataPoints) {
        super(numDataPoints, startTime);
        used.setDescription("Eden Space Used");
        max.setDescription("Eden Space Max");
        free.setDescription("Eden Space Free");
        committed.setDescription("Eden Space Committed");
    }
    
}
