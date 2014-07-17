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
package za.co.jumpingbean.gc.service.constants;

import java.util.HashSet;

/**
 *
 * @author mark
 */
public class SurvivorSpace {

    private final HashSet<String> names = new HashSet<>(4);

    public SurvivorSpace(){
        names.add("Survivor Space");
        names.add("PS Survivor Space");
        names.add("Par Survivor Space");
        names.add("G1 Survivor Space");
    }

    private static SurvivorSpace survivorSpace;
    
    public static boolean isMember(String name) {
        if(survivorSpace==null){
            survivorSpace = new SurvivorSpace();
        }
        return survivorSpace.names.contains(name);
    }

}
