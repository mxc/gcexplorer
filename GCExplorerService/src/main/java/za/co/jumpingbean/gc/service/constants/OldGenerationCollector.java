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
public class OldGenerationCollector {

    private final HashSet<String> names = new HashSet<>(4);

    public OldGenerationCollector() {
        names.add("MarkSweepCompact");
        names.add("PS MarkSweep");
        names.add("ConcurrentMarkSweep");
        names.add("G1 Old Generation");
    }

    private static OldGenerationCollector oldGenCollector;

    public static boolean isMember(String name) {
        if (oldGenCollector == null) {
            oldGenCollector = new OldGenerationCollector();
        }
        return oldGenCollector.names.contains(name);
    }
}
