package com.tracer0219.treward;

import com.tracer0219.treward.command.TRewardCommand;
import com.tracer0219.treward.database.TRewardSQLManager;
import com.tracer0219.treward.eco.PlayerPointsSupporter;
import com.tracer0219.treward.eco.VaultSupporter;
import com.tracer0219.treward.entity.Reward;
import com.tracer0219.treward.events.PlayerListener;
import com.tracer0219.treward.events.RewardManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public final class TReward extends JavaPlugin {
    public static final double TAX_RATE = 0.05;
    public static final int MIN_POINTS = 0;
    public static final int MIN_COINS = 500;
    private static TReward instance;
    private static TRewardSQLManager sqlM;
    private static PlayerPointsSupporter ppSupporter;
    private static VaultSupporter vaultSupporter;
    private static TRewardCommand tc;
    private static RewardManager rm;
    private static PlayerListener listener;

    public static void update() {
        tc.update();

    }

    public static TReward getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        if (!instance.getDataFolder().exists()) {
            instance.getDataFolder().mkdir();
        }
        ppSupporter = new PlayerPointsSupporter();
        vaultSupporter = new VaultSupporter(instance);
        sqlM = new TRewardSQLManager(instance);
        rm = new RewardManager(instance, sqlM, vaultSupporter, ppSupporter);
        tc = new TRewardCommand(this, rm);
        getCommand("treward").setExecutor(tc);
        listener = new PlayerListener(rm);
        getServer().getPluginManager().registerEvents(listener, this);

        new BukkitRunnable() {
            @Override
            public void run() {
                List<Reward> rewards = rm.removeRewardTimedOut();
                for (Reward reward : rewards) {
                    getLogger().info("悬赏 "+reward.toString()+"已经超时已被移除");
                }
            }
        }.runTaskTimer(this,0L,20L);
    }


    @Override
    public void onDisable() {
        sqlM.close();
        getLogger().info("Disabled TReward! v1.0 byTracer");
    }

    public static void msg(CommandSender sender, String msg) {
        msg = ChatColor.translateAlternateColorCodes('&', msg);
        sender.sendMessage("§7§l[悬赏]: §r" + msg);
    }
}
