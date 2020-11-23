package fr.naruse.carepackage.api.event;

import fr.naruse.carepackage.carepackage.CarePackage;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerOpenCarePackageEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final CarePackage carePackage;
    private boolean isCancelled = false;
    private final boolean firstOpener;

    public PlayerOpenCarePackageEvent(Player who, CarePackage carePackage, boolean firstOpener) {
        super(who);
        this.carePackage = carePackage;
        this.firstOpener = firstOpener;
    }

    public CarePackage getCarePackage() {
        return carePackage;
    }

    public boolean isFirstOpener() {
        return firstOpener;
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
