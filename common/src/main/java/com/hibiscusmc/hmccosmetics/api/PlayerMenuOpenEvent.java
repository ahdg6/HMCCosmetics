package com.hibiscusmc.hmccosmetics.api;

import com.hibiscusmc.hmccosmetics.gui.Menu;
import com.hibiscusmc.hmccosmetics.user.CosmeticUser;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerMenuOpenEvent extends Event implements Cancellable {

    private CosmeticUser user;
    private Menu menu;
    private boolean isCancelled;

    public PlayerMenuOpenEvent(CosmeticUser user, Menu menu) {
        this.user = user;
        this.menu = menu;
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

    public Menu getMenu() {
        return menu;
    }
}
