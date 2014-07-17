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
    
    public static UUIDProcess newProcess(UUID id,String initParams,String javaVersion) {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        return new UUIDProcess(id,new EdenMemoryPool(ts,count),
                new SurvivorMemoryPool(ts,count),
                new EmptySurvivorMemoryPool(ts,count),
                new OldGenMemoryPool(ts,count),
                new PermGenMemoryPool(ts,count),initParams,javaVersion);
    }
    
}
