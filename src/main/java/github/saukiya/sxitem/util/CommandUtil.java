package github.saukiya.sxitem.util;

import github.saukiya.sxitem.SXItem;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Saukiya
 */

public class CommandUtil {

    private static final Pattern PATTERN = Pattern.compile("^\\[(.*?)] *(.+)");

    /**
     * 快速执行指令列表
     *
     * @param player      Player
     * @param commandList List
     */
    public static void run(Player player, List<String> commandList) {
        int delay = 0;
        Runnable runnable;
        for (String cmd : PlaceholderUtil.setPlaceholders(player, commandList)) {
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
            Bukkit.getScheduler().runTaskLater(SXItem.getInst(), runnable, delay);
        }
    }

    /**
     * 快速执行指令
     *
     * @param player  Player
     * @param command Command
     */
    public static void run(Player player, String command) {
        command = PlaceholderUtil.setPlaceholders(player, command).replace("%player%", player.getName()).replace('&', '§');
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
                if (args.length > 4) {
                    MessageUtil.getInst().sendTitle(player, args[0], args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]));
                } else {
                    MessageUtil.getInst().sendTitle(player, args[0], args.length > 1 ? args[1] : null, 5, 20, 5);
                }
                break;
            default:
                SXItem.getInst().getLogger().warning("No Command To Type: " + type + " -> " + command);
                break;
        }
    }
}
