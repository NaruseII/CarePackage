package fr.naruse.carepackage.api.event;

import fr.naruse.carepackage.carepackage.CarePackage;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CarePackageDestroyEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final CarePackage carePackage;

    public CarePackageDestroyEvent(CarePackage carePackage) {
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
}
