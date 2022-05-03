package com.tracer0219.treward.events;

import com.tracer0219.treward.TReward;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.ThreadUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.List;

public class PlayerListener implements Listener {
    private RewardManager manager;

    public PlayerListener(RewardManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        if(e.getEntity().getKiller()!=null) {
            kill(e.getEntity().getKiller(),e.getEntity());
            return;
        }
        Player victim=e.getEntity();
        if(victim.getLastDamageCause() instanceof EntityDamageByEntityEvent){
            EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) victim.getLastDamageCause();
            Entity damager = entityDamageByEntityEvent.getDamager();
            if(damager instanceof Projectile){
                Projectile projectile=(Projectile) damager;
                if(projectile.getShooter()!=null&&projectile.getShooter() instanceof Player){
                    kill((Player) projectile.getShooter(),victim);
                }
            }
        }
    }

    private void kill(Player killer,Player victim){
        if(manager.findTarget(killer)!=null&&manager.findTarget(killer)==victim){
            Integer task = manager.findTask(killer);
            manager.doneTask(killer,task);
            TReward.msg(killer,"&4&l你已经完成了悬赏!");
            List<OfflinePlayer> subscribers = manager.findSubscribers(task);
            subscribers.stream().forEach(offP->{
                manager.finishTask(offP,task);
                if(offP.isOnline()){
                    TReward.msg((Player)offP,"&4&l&n您的当前悬赏任务已被他人抢先完成!");
                }
            });
        }
    }
}
