package fr.naruse.carepackage.api.event;

import fr.naruse.carepackage.carepackage.CarePackage;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CarePackageSpawnEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final CarePackage carePackage;
    private boolean isCancelled;

    public CarePackageSpawnEvent(CarePackage carePackage) {
        this.carePackage = carePackage;
    }

    public CarePackage getCarePackage() {
        return carePackage;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.isCancelled = b;
    }
}
