package com.tracer0219.treward.events;

import com.tracer0219.treward.TReward;
import com.tracer0219.treward.database.TRewardSQLManager;
import com.tracer0219.treward.eco.PlayerPointsSupporter;
import com.tracer0219.treward.eco.VaultSupporter;
import com.tracer0219.treward.entity.Reward;
import mc.obliviate.bloksqliteapi.sqlutils.SQLTable;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RewardManager implements IRewardManager {
    private TReward instance;
    private TRewardSQLManager sqlM;
    private VaultSupporter vaultSupporter;
    private PlayerPointsSupporter ppSupporter;
    private SQLTable rewardTable, taskTable;

    public RewardManager(TReward instance, TRewardSQLManager sqlM, VaultSupporter vaultSupporter, PlayerPointsSupporter ppSupporter) {
        this.instance = instance;
        this.sqlM = sqlM;
        this.vaultSupporter = vaultSupporter;
        this.ppSupporter = ppSupporter;
        this.rewardTable = sqlM.getRewardTable();
        this.taskTable = sqlM.getTaskTable();
    }


    @Override
    public List<OfflinePlayer> findSubscribers(Integer task) {
        List<OfflinePlayer> list = new ArrayList<>();
        if (task == null) return list;
        ResultSet rs = taskTable.select("task", String.valueOf(task));
        try {
            while (rs.next()) {
                list.add(Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("player"))));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public void createReward(Reward reward) {
        while (rewardTable.exist((reward.getId()))) {
            reward = reward.respawn();
        }

        vaultSupporter.withdraw(reward.getCreator(), coinsNeed(reward));
        ppSupporter.withdraw(reward.getCreator(), pointsNeed(reward));

        rewardTable.insert(rewardTable.createUpdate(reward.getId())
                .putData("id", reward.getId())
                .putData("target", reward.getTarget().getUniqueId().toString())
                .putData("publisher", reward.getCreator().getUniqueId().toString())
                .putData("points", reward.getPoints())
                .putData("coins", reward.getCoins())
        );

        TReward.updateGUI();


    }


    @Override
    public boolean isBalanceEnough(Reward reward) {
        OfflinePlayer creator = reward.getCreator();

        double coins = vaultSupporter.afterTax(reward.getCoins());
        double points = ppSupporter.afterTax(reward.getPoints());
        if (vaultSupporter.balance(creator) < coins) {
            return false;
        }
        if (ppSupporter.balance(creator) < points) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isBalanceEnough(Reward reward, int times) {
        return isBalanceEnough(new Reward(reward.getCreator(), reward.getTarget(), reward.getCoins() * times, reward.getPoints() * times));
    }


    @Override
    public boolean removeReward(Reward reward) {
        return removeReward(reward.getId());
    }

    @Override
    public boolean removeReward(int id) {
        if (!rewardTable.exist(id)) {
            return false;
        }
        rewardTable.delete(id);
        TReward.updateGUI();
        return true;
    }

    @Override
    public Reward findReward(int id) {
        ResultSet r = rewardTable.select(String.valueOf(id));

        try {
            if (!r.next()) {
                return null;
            }

            return new Reward(
                    Bukkit.getOfflinePlayer(UUID.fromString(r.getString("publisher"))),
                    Bukkit.getOfflinePlayer(UUID.fromString(r.getString("target"))),
                    r.getInt("coins"),
                    r.getInt("points"),
                    r.getInt("id")

            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Reward> findAll() {
        ResultSet r = rewardTable.selectAll();
        List<Reward> list = new ArrayList<>();
        try {
            while (r.next()) {
                Reward reward = new Reward(
                        Bukkit.getOfflinePlayer(UUID.fromString(r.getString("publisher"))),
                        Bukkit.getOfflinePlayer(UUID.fromString(r.getString("target"))),
                        r.getInt("coins"),
                        r.getInt("points"),
                        r.getInt("id")
                );
                list.add(reward);
                if (reward.getTarget() == null) {
                    Bukkit.getLogger().info("target is not null");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public double coinsNeed(Reward reward) {
        return vaultSupporter.afterTax(reward.getCoins());
    }

    @Override
    public double pointsNeed(Reward reward) {
        return ppSupporter.afterTax(reward.getPoints());
    }

    @Override
    public double coinsChargeNeed(Reward reward) {
        return vaultSupporter.calServiceCharge(reward.getCoins());
    }

    @Override
    public double pointsChargeNeed(Reward reward) {
        return ppSupporter.calServiceCharge(reward.getPoints());
    }

    @Override
    public double coinsNeed(Reward reward, int times) {
        return coinsNeed(new Reward(reward.getCreator(), reward.getTarget(), reward.getCoins() * times, reward.getPoints()));
    }

    @Override
    public double pointsNeed(Reward reward, int times) {
        return pointsNeed(new Reward(reward.getCreator(), reward.getTarget(), reward.getCoins(), reward.getPoints() * times));
    }

    @Override
    public double coinsChargeNeed(Reward reward, int times) {
        return coinsChargeNeed(new Reward(reward.getCreator(), reward.getTarget(), reward.getCoins() * times, reward.getPoints()));
    }

    @Override
    public double pointsChargeNeed(Reward reward, int times) {
        return pointsChargeNeed(new Reward(reward.getCreator(), reward.getTarget(), reward.getCoins(), reward.getPoints() * times));
    }

    @Override
    public boolean acceptTask(OfflinePlayer challenger, Reward reward) {
        return acceptTask(challenger, reward.getId());
    }


    @Override
    public boolean acceptTask(OfflinePlayer challenger, int id) {
        Reward reward = findReward(id);
        if (challenger == reward.getTarget()) {
            return false;
        }
        taskTable.insert(taskTable.createUpdate(challenger.getUniqueId().toString()).putData("player", challenger.getUniqueId().toString()).putData("task", id));
        TReward.updateGUI();
        return true;
    }

    @Override
    public void doneTask(OfflinePlayer p, Reward reward) {
        doneTask(p, reward.getId());

    }

    @Override
    public void doneTask(OfflinePlayer p, int id) {
        Reward r = findReward(id);
        vaultSupporter.deposit(p, r.getCoins());
        ppSupporter.deposit(p, r.getPoints());
        removeReward(id);
        finishTask(p, id);
    }

    @Override
    public OfflinePlayer findTarget(OfflinePlayer p) {
        Integer task = findTask(p);
        if (task == null)
            return null;

        Reward reward = findReward(task);
        if (reward == null) {
            taskTable.delete(p.getUniqueId().toString());
            return null;
        }
        return reward.getTarget();

    }

    @Override
    public Integer findTask(OfflinePlayer p) {
        ResultSet select = taskTable.select(p.getUniqueId().toString());
        try {
            if (!select.next())
                return null;
            return select.getInt("task");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void finishTask(OfflinePlayer p, int id) {
        if (taskTable.exist(p.getUniqueId().toString()))
            taskTable.delete(p.getUniqueId().toString());
        TReward.updateGUI();
    }


}

