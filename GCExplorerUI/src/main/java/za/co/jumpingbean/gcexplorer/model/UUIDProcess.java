/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package za.co.jumpingbean.gcexplorer.model;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import za.co.jumpingbean.gcexplorer.ui.EdenMemoryPool;

/**
 *
 * @author mark
 */
public class UUIDProcess {
    
    private final UUID id;
    private final EdenMemoryPool edenPool;
    private final SurvivorMemoryPool survivorPool;
    private final OldGenMemoryPool oldGenPool;
    private final List<MemoryPool> list;
    
    public UUIDProcess(UUID id, EdenMemoryPool edenPool, SurvivorMemoryPool survivorPool, OldGenMemoryPool oldGenPool) {
        this.id = id;
        this.edenPool = edenPool;
        this.survivorPool = survivorPool;
        this.oldGenPool = oldGenPool;
        list = new LinkedList<>();
        list.add(this.edenPool);
        list.add(this.survivorPool);
        list.add(this.oldGenPool);
    }

    public String getDescription(){
        return id.toString();
    }
    
    public UUID getId() {
        return id;
    }

    public EdenMemoryPool getEdenPool() {
        return edenPool;
    }

    public SurvivorMemoryPool getSurvivorPool() {
        return survivorPool;
    }

    public OldGenMemoryPool getOldGenPool() {
        return oldGenPool;
    }

    public List<MemoryPool> getDataItems() {
         return list;
    }
    
}
