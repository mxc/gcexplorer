/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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