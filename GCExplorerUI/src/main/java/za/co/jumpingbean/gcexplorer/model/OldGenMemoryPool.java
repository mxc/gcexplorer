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
public class OldGenMemoryPool extends MemoryPool {

    public OldGenMemoryPool(Timestamp startTime,int numDataPoints) {
        super(numDataPoints, startTime);
        used.setDescription("Old Gen Space Used");
        max.setDescription("Old Gen Space Max");
        free.setDescription("Old Gen Space Free");
        committed.setDescription("Old Gen Space Committed");
    }

}
