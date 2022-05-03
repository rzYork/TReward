package com.tracer0219.treward.eco;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

import static com.tracer0219.treward.TReward.TAX_RATE;
import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getServer;

public class VaultSupporter implements IEcoSupporter {

    private static Economy econ = null;

    public VaultSupporter(JavaPlugin plugin) {
        if (!setupEconomy()) {
            getServer().getPluginManager().disablePlugin(plugin);
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().info("null vault");
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            getLogger().info("null rsp");
            return false;
        }
        econ = rsp.getProvider();
        if(econ==null)
            getLogger().info("null econ");
        return (econ != null);
    }

    @Override
    public void deposit(OfflinePlayer p, double amount) {
        econ.depositPlayer(p, amount);
    }

    @Override
    public void withdraw(OfflinePlayer p, double amount) {
        econ.withdrawPlayer(p, amount);
    }

    @Override
    public double balance(OfflinePlayer p){
        return econ.getBalance(p);
    }

    @Override
    public double calServiceCharge(double amount){
        return amount*TAX_RATE;
    }

    @Override
    public double afterTax(double amount){
        return amount+calServiceCharge(amount);
    }


}
