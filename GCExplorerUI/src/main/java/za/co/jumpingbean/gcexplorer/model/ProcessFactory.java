/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package za.co.jumpingbean.gcexplorer.model;

import java.sql.Timestamp;
import java.util.UUID;
import za.co.jumpingbean.gcexplorer.ui.EdenMemoryPool;

/**
 *
 * @author mark
 */
public class ProcessFactory {

    static final int count = 40;
    
    public static UUIDProcess newProcess(UUID id,String initParams) {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        return new UUIDProcess(id,new EdenMemoryPool(ts,count),
                new SurvivorMemoryPool(ts,count),new OldGenMemoryPool(ts,count),
                new PermGenMemoryPool(ts,count),initParams);
    }
    
}
