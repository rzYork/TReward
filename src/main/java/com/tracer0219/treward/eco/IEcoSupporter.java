package com.tracer0219.treward.eco;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public interface IEcoSupporter {
    void deposit(OfflinePlayer p, double amount);

    void withdraw(OfflinePlayer p, double amount);

    double balance(OfflinePlayer p);

    double calServiceCharge(double amount);

    double afterTax(double amount);
}
