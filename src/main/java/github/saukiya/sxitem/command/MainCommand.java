package github.saukiya.sxitem.command;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.command.sub.GiveCommand;
import github.saukiya.sxitem.command.sub.NBTCommand;
import github.saukiya.sxitem.command.sub.ReloadCommand;
import github.saukiya.sxitem.command.sub.SaveCommand;
import github.saukiya.sxitem.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Saukiya
 */

public class MainCommand implements CommandExecutor, TabCompleter {

    public MainCommand() {
        new GiveCommand().registerCommand();
        new SaveCommand().registerCommand();
        new NBTCommand().registerCommand();
        new ReloadCommand().registerCommand();
    }

    public void setup(String command) {
        SXItem.getInst().getCommand(command).setExecutor(this);
        SXItem.getInst().getCommand(command).setTabCompleter(this);
        SubCommand.commands.stream().filter(subCommand -> subCommand instanceof Listener).forEach(subCommand -> {
            Bukkit.getPluginManager().registerEvents((Listener) subCommand, SXItem.getInst());
            SXItem.getInst().getLogger().info("RegListener " + subCommand.getClass().getSimpleName());
        });
        SubCommand.commands.forEach(SubCommand::onEnable);
        SXItem.getInst().getLogger().info("Load " + SubCommand.commands.size() + " Commands");
    }

    public void reload() {
        SubCommand.commands.forEach(SubCommand::onReload);
    }

    private SenderType getType(CommandSender sender) {
        return sender instanceof Player ? SenderType.PLAYER : SenderType.CONSOLE;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command arg1, String label, String[] args) {
        SenderType type = getType(sender);
        if (args.length == 0) {
            sender.sendMessage("§0-§8 --§7 ---§c ----§4 -----§b " + SXItem.getInst().getName() + "§4 -----§c ----§7 ---§8 --§0 - §0Author Saukiya");
            String color = "§7";
            for (SubCommand sub : SubCommand.commands) {
                if (sub.isUse(sender, type) && !sub.hide) {
                    color = color.length() > 0 ? "" : "§7";
                    sub.sendIntroduction(sender, color, label);
                }
            }
            return true;
        }
        for (SubCommand sub : SubCommand.commands) {
            if (sub.cmd.equalsIgnoreCase(args[0])) {
                if (!sub.isUse(sender, type)) {
                    sender.sendMessage(Message.getMsg(Message.ADMIN__NO_PERMISSION_CMD));
                } else {
                    sub.onCommand(sender, args);
                }
                return true;
            }
        }
        sender.sendMessage(Message.getMsg(Message.ADMIN__NO_CMD, args[0]));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        SenderType type = getType(sender);
        return args.length == 1 ? SubCommand.commands.stream().filter(sub -> sub.isUse(sender, type) && !sub.hide && sub.cmd.contains(args[0])).map(sub -> sub.cmd).collect(Collectors.toList()) : SubCommand.commands.stream().filter(sub -> sub.cmd.equalsIgnoreCase(args[0])).findFirst().filter(sub -> sub.isUse(sender, type)).map(sub -> sub.onTabComplete(sender, args)).orElse(null);
    }
}
