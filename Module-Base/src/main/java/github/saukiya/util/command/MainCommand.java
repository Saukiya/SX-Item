package github.saukiya.util.command;

import github.saukiya.util.nms.MessageUtil;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Saukiya
 */
@AllArgsConstructor
@RequiredArgsConstructor
public class MainCommand implements CommandExecutor, TabCompleter {

    final List<SubCommand> COMMANDS = new ArrayList<>();

    final JavaPlugin plugin;

    Function<String, String> message = String::toUpperCase;

    /**
     * 注册指令
     * 遇到重复的指令时，会删除之前的指令
     */
    public void register(SubCommand command) {
        if (plugin.isEnabled()) return;
        command.mainCommand = this;
        COMMANDS.removeIf(subCommand -> subCommand.cmd.equals(command.cmd));
        COMMANDS.add(command);
    }

    public void onEnable(String command) {
        plugin.getCommand(command).setExecutor(this);
        plugin.getCommand(command).setTabCompleter(this);
        Collections.sort(COMMANDS);
        COMMANDS.stream().filter(subCommand -> subCommand instanceof Listener).forEach(subCommand -> Bukkit.getPluginManager().registerEvents((Listener) subCommand, plugin));
        COMMANDS.forEach(SubCommand::onEnable);
        plugin.getLogger().info("Load " + COMMANDS.size() + " Commands");
    }

    public void onReload() {
        COMMANDS.forEach(SubCommand::onReload);
    }

    public void onDisable() {
        COMMANDS.forEach(SubCommand::onDisable);
    }

    private SenderType getType(CommandSender sender) {
        return sender instanceof Player ? SenderType.PLAYER : SenderType.CONSOLE;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        SenderType type = getType(sender);
        if (args.length == 0) {
            sender.sendMessage("§0-§8 --§7 ---§c ----§4 -----§b " + plugin.getName() + "§4 -----§c ----§7 ---§8 --§0 - §0Author " + plugin.getDescription().getAuthors());
            String color = "§7";
            for (SubCommand sub : COMMANDS) {
                if (sub.isUse(sender, type) && !sub.hide) {
                    color = color.isEmpty() ? "§7" : "";
                    sub.sendIntroduction(sender, color, label);
                }
            }
            return true;
        }
        for (SubCommand sub : COMMANDS) {
            if (sub.cmd.equalsIgnoreCase(args[0])) {
                if (!sub.isUse(sender, type)) {
                    MessageUtil.send(sender, message.apply("ADMIN.NO_PERMISSION_CMD"));
                } else {
                    sub.onCommand(sender, args);
                }
                return true;
            }
        }
        MessageUtil.send(sender, message.apply("ADMIN.NO_CMD"));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        SenderType type = getType(sender);
        if (args.length == 1) {
            return COMMANDS.stream().filter(sub -> sub.cmd.contains(args[0]) && sub.isUse(sender, type) && !sub.hide).map(sub -> sub.cmd).collect(Collectors.toList());
        }
        return COMMANDS.stream().filter(sub -> sub.cmd.equalsIgnoreCase(args[0]) && sub.isUse(sender, type)).findFirst().map(sub -> sub.onTabComplete(sender, args)).orElse(null);
    }
}
