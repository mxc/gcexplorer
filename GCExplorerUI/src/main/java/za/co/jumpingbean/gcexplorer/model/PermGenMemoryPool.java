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
public class PermGenMemoryPool  extends MemoryPool{

    public PermGenMemoryPool(Timestamp startTime,int numDataPoints) {
        super(numDataPoints, startTime);
        used.setDescription("Perm Gen Space Used");
        max.setDescription("Perm Gen Space Max");
        free.setDescription("Perm Gen Space Free");
        committed.setDescription("Perm Gen Space Committed");        
    }
    
}
