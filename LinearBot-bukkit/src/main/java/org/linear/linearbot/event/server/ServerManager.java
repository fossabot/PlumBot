package org.linear.linearbot.event.server;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.linear.linearbot.LinearBot;
import org.linear.linearbot.config.Config;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ServerManager {

    public static String listOnlinePlayer() {
        List<String> onlinePlayer = new LinkedList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            onlinePlayer.add(p.getName());
        }
        return Arrays.toString(onlinePlayer.toArray()).replace("\\[|\\]", "");
    }

    public static List<String> msgList = new LinkedList<>();

    public static void sendCmd(boolean isGroup, long id, String cmd, boolean disp) {
        if(!Config.CMD()){
            LinearBot.getBot().sendMsg(isGroup, "未开启CMD命令功能", id);
        }

        CommandSender commandSender = new ConsoleSender();
        new BukkitRunnable(){
            @Override
            public void run(){
                msgList.clear();
                Bukkit.dispatchCommand(commandSender, cmd);
            }
        }.runTask(LinearBot.INSTANCE);
        Bukkit.getScheduler().runTaskLaterAsynchronously(LinearBot.INSTANCE, () -> {
            StringBuilder stringBuilder = new StringBuilder();
            if (msgList.size() == 0) {
                msgList.add("无返回值");
            }
            for (String msg : msgList) {
                if (msgList.get(msgList.size() - 1).equalsIgnoreCase(msg)) {
                    stringBuilder.append(msg.replaceAll("§\\S", ""));
                } else {
                    stringBuilder.append(msg.replaceAll("§\\S", "")).append("\n");
                }
            }
            if(!disp){
                msgList.clear();
                LinearBot.getBot().sendMsg(isGroup, "无返回值", id);
                return;
            }
            if(stringBuilder.toString().length()<=5000){
                LinearBot.getBot().sendMsg(isGroup, stringBuilder.toString(), id);
            }else {
                LinearBot.getBot().sendMsg(isGroup, "返回值过长", id);
            }
            msgList.clear();
        }, 4L);
    }

}
