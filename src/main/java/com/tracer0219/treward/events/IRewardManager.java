package com.tracer0219.treward.events;

import com.tracer0219.treward.entity.Reward;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public interface IRewardManager {
    //    已经发布的悬赏不可modify

    List<OfflinePlayer> findSubscribers(Integer task);
    void createReward(Reward reward);
    boolean isBalanceEnough(Reward reward);
    boolean isBalanceEnough(Reward reward,int times);
    boolean removeReward(Reward reward);
    boolean removeReward(int id);
    Reward findReward(int id);

    List<Reward> findAll();

    double coinsNeed(Reward reward);
    double pointsNeed(Reward reward);

    double coinsChargeNeed(Reward reward);
    double pointsChargeNeed(Reward reward);

    double coinsNeed(Reward reward,int times);
    double pointsNeed(Reward reward,int times);

    double coinsChargeNeed(Reward reward,int times);
    double pointsChargeNeed(Reward reward,int times);

    boolean acceptTask(OfflinePlayer challenger, Reward reward);
    boolean acceptTask(OfflinePlayer challenger, int id);

    void doneTask(OfflinePlayer p,Reward reward);
    void doneTask(OfflinePlayer p,int id);

    OfflinePlayer findTarget(OfflinePlayer p);

    Integer findTask(OfflinePlayer p);

    void finishTask(OfflinePlayer p,int id);
}
