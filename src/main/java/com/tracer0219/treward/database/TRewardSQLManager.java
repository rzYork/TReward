package com.tracer0219.treward.database;

import com.tracer0219.treward.TReward;
import mc.obliviate.bloksqliteapi.SQLHandler;
import mc.obliviate.bloksqliteapi.sqlutils.DataType;
import mc.obliviate.bloksqliteapi.sqlutils.SQLTable;
import org.bukkit.Bukkit;

public class TRewardSQLManager extends SQLHandler {
    private static SQLTable rewardTable, taskTable;

    public SQLTable getRewardTable() {
        return rewardTable;
    }

    public SQLTable getTaskTable() {
        return taskTable;
    }

    public TRewardSQLManager(TReward plugin) {
        super(plugin.getDataFolder().getAbsolutePath());
        super.connect("TRewardDB");
    }


    @Override
    public void onConnect() {
        Bukkit.getLogger().info("Plugin successfully connected to database.");
        rewardTable = createRewardTable();
        taskTable = createPlayerTaskTable();
    }


    private SQLTable createRewardTable() {
        final SQLTable sqlTable = new SQLTable("rewards", "id")
                .addField("id", DataType.INTEGER, true, true, true)
                .addField("target", DataType.TEXT)
                .addField("publisher", DataType.TEXT)
                .addField("points", DataType.INTEGER)
                .addField("coins", DataType.INTEGER);
        return sqlTable.create();
    }

    private SQLTable createPlayerTaskTable() {
        final SQLTable sqlTable = new SQLTable("tasks", "player")
                .addField("player", DataType.TEXT, true, true, true)
                .addField("task", DataType.INTEGER);
        return sqlTable.create();
    }

    public void close(){
        disconnect();
    }

}