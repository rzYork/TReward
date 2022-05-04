package com.tracer0219.treward.entity;

import org.apache.commons.lang.StringUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.data.type.RespawnAnchor;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class Reward {
    private OfflinePlayer creator;

    private OfflinePlayer target;
    private int coins, points;

    private int id;
    private long invalidTimestamp;
    private HashSet<OfflinePlayer> subscribers = new HashSet<>();




    public Date getInvalidTime() {
        return new Date(System.currentTimeMillis() - invalidTimestamp);
    }

    private void setInvalid() {
        this.invalidTimestamp = System.currentTimeMillis();
    }

    public long getInvalidTimestamp() {
        return invalidTimestamp;
    }

    public void setInvalidTimestamp(long invalidTimestamp) {
        this.invalidTimestamp = invalidTimestamp;
    }

    public void subscribe(OfflinePlayer subscriber) {
        this.subscribers.add(subscriber);
    }

    public boolean unsubscribe(OfflinePlayer subscriber) {
        boolean remove = this.subscribers.remove(subscriber);
        if (subscribers.isEmpty())
            this.setInvalid();
        return remove;
    }

    public HashSet<OfflinePlayer> getSubscribers() {
        return new HashSet<>(subscribers);
    }

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
        setInvalid();
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
    public Reward(OfflinePlayer creator, OfflinePlayer target, int coins, int points, int existingId, long invalidTimestamp) {
        this(creator, target, coins, points);
        this.id = existingId;
        this.invalidTimestamp = invalidTimestamp;
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
                ", id=" + id +
                ", invalidTimestamp=" + invalidTimestamp +
                ", subscribers=" + StringUtils.join(subscribers.stream().map(OfflinePlayer::getName).collect(Collectors.toList()).toArray(new String[0]), ",") +
                '}';
    }
}
