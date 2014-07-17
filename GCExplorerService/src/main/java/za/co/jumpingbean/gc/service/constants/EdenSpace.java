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
public class EdenSpace {

    private final HashSet<String> names = new HashSet<>(4);

    public EdenSpace() {
        names.add("Eden Space");
        names.add("PS Eden Space");
        names.add("Par Eden Space");
        names.add("G1 Eden Space");
    }

    private static EdenSpace edenSpace;

    public static boolean isMember(String name) {
        if (edenSpace == null) {
            edenSpace = new EdenSpace();
        }
        return edenSpace.names.contains(name);
    }

}
