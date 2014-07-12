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

public enum DESC {

    EDENSPACEFREE("Eden Space Free"),
    EDENSPACEUSED("Eden Space Used"),
    EDENSPACECOMMITTED("Eden Space Commited"),
    EDENSPACEMAX("Eden Space Max"),
    SURVIVORSPACEFREE("Survivor Space Free"),
    SURVIVORSPACEUSED("Survivor Space Used"),
    SURVIVORSPACECOMMITTED("Survivor Space Committed"),
    SURVIVORSPACEMAX("Survivior Space  Max"),
    OLDGENSPACEFREE("Old Generation Free"),
    OLDGENSPACEUSED("Old Generation Used"),
    OLDGENSPACECOMMITTED("Old Generation Committed"),
    OLDGENSPACEMAX("Old Generation Max"),
    PERMGENSPACEFREE("Perm Gen Free"),
    PERMGENSPACEUSED("Perm Gen Used"),
    PERMGENSPACECOMMITTED("Perm Gen Committed"),
    PERMGENSPACEMAX("Perm Gen Max");

    private final String description;

    DESC(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return this.description;
    }
}