package github.saukiya.sxitem.util;

import github.saukiya.sxitem.SXItem;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author Saukiya
 */

public class CommandUtil {
    /**
     * 快速执行指令列表
     *
     * @param player      Player
     * @param commandList List
     */
    public static void onPlayCommand(Player player, List<String> commandList) {
        int delay = 0;
        commandList = PlaceholderAPI.setPlaceholders(player, commandList);
        for (String cmd : commandList) {
            String command = cmd.replace("%player%", player.getName());
            if (command.startsWith("[DELAY] ")) {
                delay += Integer.parseInt(command.substring(8));
            } else {
                Bukkit.getScheduler().runTaskLater(SXItem.getInst(), () -> onPlayerCommand(player, command.replace('&', '§')), delay);
            }
        }
    }

    public static void onPlayerCommand(Player player, String command) {
        if (command.startsWith("[CONSOLE] "))
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.substring(10));
        else if (command.startsWith("[MESSAGE] "))
            player.sendMessage(command.substring(10));
        else if (command.startsWith("[CHAT] "))
            player.chat(command.substring(6));
        else if (command.startsWith("[BC] "))
            Bukkit.broadcastMessage(command.substring(5));
        else if (command.startsWith("[SOUND] ")) {
            String[] split = command.substring(8).split(":");
            player.playSound(player.getLocation(), Sound.valueOf(split[0]), Float.valueOf(split[1]), Float.valueOf(split[2]));
        } else if (command.startsWith("[TITLE] ")) {
            String[] split = command.substring(8).split(":");
            player.sendTitle(split[0], split.length > 1 ? split[1] : null, split.length > 2 ? Integer.parseInt(split[2]) : 5, split.length > 3 ? Integer.parseInt(split[3]) : 30, split.length > 4 ? Integer.parseInt(split[4]) : 5);
        } else
            Bukkit.dispatchCommand(player, command);
    }
}
