package github.saukiya.sxitem.util;

import github.saukiya.tools.helper.PlaceholderHelper;
import github.saukiya.tools.nms.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @deprecated
 */
public class CommandUtil {

    private static final Pattern PATTERN = Pattern.compile("^\\[(.*?)] *(.+)");

    private static final Plugin plugin = Bukkit.getPluginManager().getPlugin("SX-Item");

    @Deprecated
    public static void run(Player player, List<String> commandList) {
        int delay = 0;
        Runnable runnable;
        for (String cmd : PlaceholderHelper.setPlaceholders(player, commandList)) {
            String command = cmd.replace("%player%", player.getName()).replace('&', '§');
            Matcher matcher = PATTERN.matcher(command);
            if (matcher.find()) {
                if (matcher.group(1).equals("DELAY")) {
                    delay += Integer.parseInt(matcher.group(2));
                    continue;
                }
                runnable = () -> run(player, matcher.group(1), matcher.group(2));
            } else {
                runnable = () -> Bukkit.dispatchCommand(player, command);
            }
            Bukkit.getScheduler().runTaskLater(plugin, runnable, delay);
        }
    }

    @Deprecated
    public static void run(Player player, String command) {
        command = PlaceholderHelper.setPlaceholders(player, command).replace("%player%", player.getName()).replace('&', '§');
        Matcher matcher = PATTERN.matcher(command);
        if (matcher.find()) {
            run(player, matcher.group(1), matcher.group(2));
        } else {
            Bukkit.dispatchCommand(player, command);
        }
    }

    private static void run(Player player, String type, String command) {
        switch (type) {
            case "CONSOLE":
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                break;
            case "MESSAGE":
                player.sendMessage(command);
                break;
            case "CHAT":
                player.chat(command);
                break;
            case "BC":
                Bukkit.broadcastMessage(command);
                break;
            case "SOUND":
                String[] split = command.split(":");
                player.playSound(player.getLocation(), Sound.valueOf(split[0]), Float.parseFloat(split[1]), Float.parseFloat(split[2]));
                break;
            case "ACTIONBAR":
                MessageUtil.getInst().sendActionBar(player, command);
                break;
            case "TITLE":
                String[] args = command.split(":");
                int fadeIn = 5, stay = 20, fadeOut = 5;
                if (args.length > 4) {
                    fadeIn = Integer.parseInt(args[2]);
                    stay = Integer.parseInt(args[3]);
                    fadeOut = Integer.parseInt(args[4]);
                }
                MessageUtil.getInst().sendTitle(player, args[0], args.length > 1 ? args[1] : null, fadeIn, stay, fadeOut);
                break;
            default:
                plugin.getLogger().warning("No Command To Type: " + type + " -> " + command);
                break;
        }
    }
}
