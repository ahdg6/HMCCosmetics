package com.hibiscusmc.hmccosmetics.gui.action.actions;

import com.hibiscusmc.hmccosmetics.gui.action.Action;
import com.hibiscusmc.hmccosmetics.user.CosmeticUser;

public class ActionCosmeticToggle extends Action {

    public ActionCosmeticToggle() {
        super("toggle");
    }

    @Override
    public void run(CosmeticUser user, String raw) {
        if (user.getHidden()) {
            if (user.getHiddenReason() != CosmeticUser.HiddenReason.ACTION && user.getHiddenReason() != CosmeticUser.HiddenReason.COMMAND) return;
            user.showCosmetics();
            return;
        }
        user.hideCosmetics(CosmeticUser.HiddenReason.ACTION);
        return;
    }
}
