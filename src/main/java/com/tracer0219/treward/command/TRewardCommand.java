package com.tracer0219.treward.command;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.tracer0219.treward.TReward;
import com.tracer0219.treward.entity.Reward;
import com.tracer0219.treward.events.RewardManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

import static java.lang.Integer.*;
import static org.apache.commons.lang.StringUtils.isNumeric;

public class TRewardCommand implements CommandExecutor {
    private TReward instance;
    private RewardManager manager;

    public TRewardCommand(TReward instance, RewardManager manager) {
        this.instance = instance;
        this.manager = manager;

    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (s.equalsIgnoreCase("treward")) {

            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("?")) {
                    msg(sender, "&e/treward create <悬赏玩家名称> <单次金币> <单次点券> <悬赏次数>");
                    msg(sender, "&e/treward list");
                    msg(sender, "&e/treward remove <id>");
                    msg(sender, "&e/treward accept <id>");
                    msg(sender, "&e/treward giveup");
                    msg(sender, "&e/treward task");
                    msg(sender, "&e/treward gui");
                    return true;
                } else if (args[0].equalsIgnoreCase("create")) {
                    return false;
                } else if (args[0].equalsIgnoreCase("list")) {
                    List<Reward> all = manager.findAll();
                    if (all.isEmpty()) {
                        msg(sender, "&b&l暂无悬赏!");
                        return true;
                    }
                    for (Reward reward : all) {
                        msg(sender, "&e悬赏ID: " + reward.getId() + " 悬赏发起者: " + reward.getCreator().getName() + " 悬赏目标: " + reward.getTarget().getName() + " 悬赏金币: " + reward.getCoins() + " 悬赏点券: " + reward.getPoints());
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("remove")) {
                    return false;
                } else if (args[0].equalsIgnoreCase("giveup")) {
                    if (!(sender instanceof Player)) {
                        msg(sender, "&7&l只有玩家可以放弃悬赏!");
                        return true;
                    }
                    Player p = (Player) sender;

                    Integer task = manager.findTask(p);
                    if (manager.findTarget(p) == null || task == null) {
                        msg(p, "&e&l你当前没有任何悬赏!");
                        return true;
                    }
                    manager.finishTask(p, task);

                    msg(p, "&4&l已经放弃悬赏!");
                    return true;
                } else if (args[0].equalsIgnoreCase("task")) {
                    if (!(sender instanceof Player)) {
                        msg(sender, "&7&l只有玩家可以查看悬赏!");
                        return true;
                    }
                    Player p = (Player) sender;
                    Integer task = manager.findTask(p);
                    if (manager.findTarget(p) == null || task == null) {
                        msg(p, "&e&l你当前没有任何悬赏!");
                        return true;
                    }
                    Reward reward = manager.findReward(task);
                    if (reward == null) {
                        msg(p, "&4&l当前悬赏已经撤销或者结束!");
                        manager.finishTask(p, task);
                        return true;
                    }
                    msg(p, "&e悬赏发起者: &b" + reward.getCreator().getName());

                    msg(p, "&e悬赏人物  : &b" + reward.getTarget().getName());

                    msg(p, "&e悬赏金币  : &b" + reward.getCoins());

                    msg(p, "&e悬赏点券 : &b" + reward.getPoints());
                    return true;

                } else if (args[0].equalsIgnoreCase("gui")) {
                    if (!(sender instanceof Player)) {
                        msg(sender, "&7&l只有玩家可以打开gui!");
                        return true;
                    }
                    openGUI((Player) sender);
                    return true;
                }
            } else if (args.length == 5) {

                if (args[0].equalsIgnoreCase("create")) {
                    if (!(sender instanceof Player)) {
                        msg(sender, "只有玩家可以发起悬赏");
                        return true;
                    }
                    String targetName = args[1];
                    if (!isNumeric(args[2]) || !isNumeric(args[3]) || !isNumeric(args[4])) {
                        msg(sender, "&4&l请输入有效的数字");
                        return true;
                    }
                    Player creator = (Player) sender;
                    int coins = parseInt(args[2]);
                    int points = parseInt(args[3]);
                    int times = parseInt(args[4]);

                    if (coins < TReward.MIN_COINS) {
                        msg(creator, "&7&l单次悬赏最低金币金额为&e&l&n" + TReward.MIN_COINS);
                        return true;
                    }
                    if (points < TReward.MIN_POINTS) {
                        msg(creator, "&7&l单次悬赏最低金币金额为&e&l&n" + TReward.MIN_COINS);
                        return true;
                    }

                    if (times < 1) {
                        msg(creator, "&7&l悬赏发布次数最低为&e&l&n 1 ");
                        return true;
                    }

                    Player target = Bukkit.getPlayer(targetName);
                    if (target == null) {
                        msg(creator, "&7&l无效的玩家或玩家不在线!");
                        return true;
                    }
                    Reward reward = new Reward(creator, target, coins, points);

                    if (!manager.isBalanceEnough(reward, times)) {
                        msg(creator, "&4&l您的余额不足!");
                        return true;
                    }


                    for (int i = 0; i < times; i++) {
                        manager.createReward(reward);
                    }

                    bro("&4&l============悬赏公告============");
                    bro("&r&b&l&n" + creator.getName() + "&r&4&l 发起了对 &r&b&l&n" + targetName + "&r&4&l 的 &r&b&l&n" + times + "&r&4&l 次悬赏");
                    bro("&e单次悬赏金币: " + (coins) + ".00");
                    bro("&e单次悬赏点券: " + (points) + ".00");
                    bro("&4&l============悬赏公告============");

                    msg(creator, "&4&l已经成功发布了对&r&7[&b&l&n" + targetName + "&r&7]&4&l的悬赏共计&r&b&l&n" + times + "&r&4&l次");
                    msg(creator, "&b==========================");
                    msg(creator, " &7金币税后共计&b&n" + manager.coinsNeed(reward, times) + "&r&7(含手续费&b&n" + manager.coinsChargeNeed(reward, times) + "&r&7)");
                    msg(creator, " &7点券税后共计&b&n" + manager.pointsNeed(reward, times) + "&r&7(含手续费&b&n" + manager.pointsChargeNeed(reward, times) + "&r&7)");
                    msg(creator, "&b==========================");

                    return true;

                }
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("remove")) {
                    String idStr = args[1];
                    int id;
                    try {
                        id = parseInt(idStr);
                    } catch (NumberFormatException e) {
                        msg(sender, "&7无效的ID格式");
                        return true;
                    }
                    Reward reward = manager.findReward(id);
                    if (reward == null) {
                        msg(sender, "&7不存在的悬赏ID");
                        return true;
                    }
                    if (sender != reward.getCreator() && !(sender instanceof ConsoleCommandSender)) {
                        msg(sender, "&4你不是该悬赏的发布者！无法删除!");
                        return true;
                    }
                    if (!manager.removeReward(reward)) {
                        msg(sender, "&4&L删除数据不存在！请联系管理员");
                        return true;
                    }
                    msg(sender, "&4&l已成功撤销悬赏!");
                    return true;
                } else if (args[0].equalsIgnoreCase("accept")) {
                    if (!(sender instanceof Player)) {
                        msg(sender, "&7&l只有玩家可以接受悬赏!");
                        return true;
                    }
                    Player challenger = (Player) sender;
                    String idStr = args[1];
                    int id;
                    try {
                        id = parseInt(idStr);
                    } catch (NumberFormatException e) {
                        msg(challenger, "&7无效的ID格式");
                        return true;
                    }
                    if (manager.findTarget(challenger) != null) {
                        msg(challenger, "&e&l你当前还有未完成的悬赏!");
                        return true;
                    }

                    Reward reward = manager.findReward(id);
                    if (reward == null) {
                        msg(challenger, "&7不存在的悬赏ID");
                        return true;
                    }


                    if (!manager.acceptTask(challenger, reward)) {
                        msg(challenger, "&4&l你无法接受对你自己的悬赏!");
                        return true;
                    }

                    msg(challenger, "&4&l已成功接受对玩家&b&l&n" + reward.getTarget().getName() + "&r&4&l的悬赏!");
                    return true;
                }
            }

        }
        return false;

    }


    public void update() {
        guis.entrySet().stream().forEach(e -> {
            createGUI(e.getKey());
            e.getValue().update();
        });

    }

    private HashMap<Player, ChestGui> guis = new HashMap<>();

    private void createGUI(Player createdTo) {
        ChestGui gui = new ChestGui(6, "悬赏");
        PaginatedPane pages = new PaginatedPane(0, 0, 9, 5);
        List<Reward> all = manager.findAll();
        List<GuiItem> tasks = new ArrayList<>();
        all.sort(new Comparator<Reward>() {
            @Override
            public int compare(Reward o1, Reward o2) {
                int result=o2.getPoints()-o1.getPoints();
                if(result==0)
                    result=o2.getCoins()-o1.getPoints();
                return result;
            }
        });

        for (Reward reward : all) {
            int id = reward.getId();
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setDisplayName("§7悬赏ID: " + reward.getId());
            List<String> lore = new ArrayList<>();
            lore.add("§7悬赏目标: §4" + reward.getTarget().getName());
            lore.add("§7悬赏发起者: §e" + reward.getCreator().getName());
            lore.add("§7悬赏金币: §e" + reward.getCoins());
            lore.add("§7悬赏点券: §e" + reward.getPoints());
            lore.add("§4§l<左键接受悬赏>");
            lore.add("§7发布者可右击撤销悬赏");
            List<OfflinePlayer> subscribers = manager.findSubscribers(reward.getId());
            lore.add("§7已接收悬赏的玩家: ");
            subscribers.stream().forEach(s->{
                lore.add("§7 - "+s.getName());
            });
            if(subscribers.isEmpty())
                lore.add("§7 - 暂无");
            meta.setLore(lore);
            meta.setOwningPlayer(reward.getTarget());
            head.setItemMeta(meta);
            GuiItem item = new GuiItem(head, e -> {
                switch (e.getClick()) {
                    case LEFT:
                    case SHIFT_LEFT:
                        Bukkit.dispatchCommand(e.getWhoClicked(), "treward accept " + id);
                        e.getWhoClicked().closeInventory();
                        break;
                    case RIGHT:
                    case SHIFT_RIGHT:
                        Bukkit.dispatchCommand(e.getWhoClicked(), "treward remove " + id);
                        e.getWhoClicked().closeInventory();
                        break;
                }
            });
            tasks.add(item);
        }
        pages.populateWithGuiItems(tasks);

        gui.addPane(pages);
        ItemStack stack = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta itemMeta3 = stack.getItemMeta();
        itemMeta3.setDisplayName("§7-");
        stack.setItemMeta(itemMeta3);


        OutlinePane background = new OutlinePane(0, 5, 9, 1);
        background.addItem(new GuiItem(stack,e->{e.setCancelled(true);}));
        background.setRepeat(true);
        background.setPriority(Pane.Priority.LOWEST);

        gui.addPane(background);

        StaticPane navigation = new StaticPane(0, 5, 9, 1);
        ItemStack previousPage = new ItemStack(Material.RED_WOOL);
        ItemMeta itemMeta1 = previousPage.getItemMeta();
        itemMeta1.setDisplayName("§7上一页");
        previousPage.setItemMeta(itemMeta1);
        navigation.addItem(new GuiItem(previousPage, event -> {
            event.setCancelled(true);
            if (!(event.getWhoClicked() instanceof Player)) {
                return;
            }
            Player p = (Player) event.getWhoClicked();
            if (pages.getPage() > 0) {
                pages.setPage(pages.getPage() - 1);

                gui.update();
            }
        }), 0, 0);

        ItemStack nextPage = new ItemStack(Material.GREEN_WOOL);
        ItemMeta itemMeta2 = nextPage.getItemMeta();
        itemMeta2.setDisplayName("§7下一页");
        nextPage.setItemMeta(itemMeta2);
        navigation.addItem(new GuiItem(nextPage, event -> {
            event.setCancelled(true);
            if (!(event.getWhoClicked() instanceof Player)) {
                return;
            }
            Player p = (Player) event.getWhoClicked();
            if (pages.getPage() < pages.getPages() - 1) {
                pages.setPage(pages.getPage() + 1);

                gui.update();
            }
        }), 8, 0);

        ItemStack info = new ItemStack(Material.WITHER_SKELETON_SKULL);
        ItemMeta itemMeta = info.getItemMeta();
        itemMeta.setLore(new ArrayList<>(Arrays.asList("§e左键查看当前个人悬赏任务", "§c右键放弃当前悬赏任务", "§7中键退出")));
        info.setItemMeta(itemMeta);

        navigation.addItem(new GuiItem(info, event -> {
            event.setCancelled(true);
            if (!(event.getWhoClicked() instanceof Player)) {
                return;
            }
            Player p = (Player) event.getWhoClicked();
            switch (event.getClick()) {

                case LEFT:
                case SHIFT_LEFT:
                    Bukkit.dispatchCommand(p, "treward task");
                    p.closeInventory();
                    break;
                case RIGHT:
                case SHIFT_RIGHT:
                    Bukkit.dispatchCommand(p, "treward giveup");
                    p.closeInventory();
                    break;
                case MIDDLE:
                    p.closeInventory();
                    break;


            }
        }), 4, 0);

        gui.addPane(navigation);
        guis.put(createdTo, gui);
    }

    private void openGUI(Player p) {
        if (guis.get(p) == null) {
            createGUI(p);
        }
        guis.get(p).update();
        guis.get(p).show(p);
    }

    public static void msg(CommandSender sender, String msg) {
        msg = ChatColor.translateAlternateColorCodes('&', msg);
        sender.sendMessage("§7§l[悬赏]: §r" + msg);
    }

    public static void bro(String msg) {
        Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }


}
