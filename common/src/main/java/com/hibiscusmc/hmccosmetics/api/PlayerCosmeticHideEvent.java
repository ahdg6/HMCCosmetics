package com.hibiscusmc.hmccosmetics.api;

import com.hibiscusmc.hmccosmetics.user.CosmeticUser;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerCosmeticHideEvent extends Event implements Cancellable {

    private CosmeticUser user;
    private CosmeticUser.HiddenReason reason;
    private boolean isCancelled;

    public PlayerCosmeticHideEvent(CosmeticUser user, CosmeticUser.HiddenReason reason) {
        this.user = user;
        this.reason = reason;
        this.isCancelled = false;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public CosmeticUser getUser() {
        return user;
    }
    public CosmeticUser.HiddenReason getReason() {
        return reason;
    }
}
