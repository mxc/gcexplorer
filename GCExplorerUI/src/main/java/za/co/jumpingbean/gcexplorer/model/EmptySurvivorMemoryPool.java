/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package za.co.jumpingbean.gcexplorer.model;

import java.sql.Timestamp;

/**
 *
 * @author mark
 */
public class EmptySurvivorMemoryPool extends MemoryPool {

    public EmptySurvivorMemoryPool(Timestamp startTime,int numDataPoints) {
        super(numDataPoints, startTime);
        used.setDescription("Empty Survivor Space Used");
        max.setDescription("Empty Survivor Space Max");
        free.setDescription("Empty Survivor Space Free");
        committed.setDescription("Empty Survivor Space Committed");        
    }
    
}
