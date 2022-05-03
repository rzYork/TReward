package com.tracer0219.treward.entity;

import org.bukkit.OfflinePlayer;
import org.bukkit.block.data.type.RespawnAnchor;

public class Reward {
    private OfflinePlayer creator;

    private OfflinePlayer target;
    private int coins, points;

    private int id;

    public OfflinePlayer getCreator() {
        return creator;
    }

    public OfflinePlayer getTarget() {
        return target;
    }

    public int getCoins() {
        return coins;
    }

    public int getPoints() {
        return points;
    }

    public int getId() {
        return this.id;
    }

    public Reward respawn() {
        return new Reward(creator, target, coins, points);
    }

    public Reward(OfflinePlayer creator, OfflinePlayer target, int coins, int points) {
        id = Long.hashCode(System.currentTimeMillis());
        this.creator = creator;
        this.target = target;
        if (coins < 0 || points < 0) {
            new RuntimeException("coins and points could not be negative").printStackTrace();
        }
        this.coins = coins;
        this.points = points;
    }

    /**
     * 只有在数据库读取已存在的悬赏时使用该构造!
     *
     * @param creator
     * @param target
     * @param coins
     * @param points
     * @param existingId
     */
    public Reward(OfflinePlayer creator, OfflinePlayer target, int coins, int points, int existingId) {
        this(creator, target, coins, points);
        this.id = existingId;
    }


    public boolean isSimilar(Reward reward) {
        return reward.getTarget() == this.getTarget() && reward.getCreator() == this.getCreator() && reward.getPoints() == this.getPoints() && reward.getCoins() == this.getCoins();
    }

    @Override
    public String toString() {
        return "Reward{" +
                "creator=" + creator.getName() +
                ", target=" + target.getName() +
                ", coins=" + coins +
                ", points=" + points +
                '}';
    }
}
