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
public class SurvivorMemoryPool extends MemoryPool {

    public SurvivorMemoryPool(Timestamp startTime,int numDataPoints) {
        super(numDataPoints, startTime);
        used.setDescription("Survivor Space Used");
        max.setDescription("Survivor Space Max");
        free.setDescription("Survivor Space Free");
        committed.setDescription("Survivor Space Committed");
    }

}
