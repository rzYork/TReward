package com.tracer0219.treward.eco;


import com.tracer0219.treward.TReward;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import static com.tracer0219.treward.TReward.TAX_RATE;

public class PlayerPointsSupporter implements IEcoSupporter {

    private static PlayerPointsAPI ppAPI;
    private static PlayerPoints pp;

    public PlayerPointsSupporter() {

        if (!hookPlayerPoints()) {
            new RuntimeException("HOOK PLAYER POINTS FAILED!").printStackTrace();
        }
        ppAPI = pp.getAPI();
    }

    private boolean hookPlayerPoints() {
        pp = (PlayerPoints) TReward.getInstance().getServer().getPluginManager().getPlugin("PlayerPoints");
        return pp != null;
    }


    @Override
    public void deposit(OfflinePlayer p, double amount) {
        if (amount + balance(p) < 0) {
            amount = -balance(p);
        }
        ppAPI.give(p.getUniqueId(), (int) amount);
    }

    @Override
    public void withdraw(OfflinePlayer p, double amount) {
        deposit(p, -amount);
    }


    @Override
    public double balance(OfflinePlayer p) {
        return ppAPI.look(p.getUniqueId());
    }

    @Override
    public double calServiceCharge(double amount) {
        return amount * TAX_RATE;
    }

    @Override
    public double afterTax(double amount) {
        return amount + calServiceCharge(amount);
    }
}
