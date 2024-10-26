package github.saukiya.tools.command;

import github.saukiya.tools.nms.MessageUtil;
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
 * 主要指令
 * <pre>
 * 例子:
 * {@code
 *  private MainCommand mainCommand;
 *
 *  @Override
 *  public void onLoad() {
 *      // mainCommand = new MainCommand(this);
 *      mainCommand = new MainCommand(this, msg -> "message convert");
 *      mainCommand.register(new GiveCommand()); // extends SubCommand
 *  }
 *
 *  @Override
 *  public void onEnable() {
 *      mainCommand.onEnable("cmdName"); // onReload() onDisable()
 *  }
 * }</pre>
 */
@AllArgsConstructor
@RequiredArgsConstructor
public class MainCommand implements CommandExecutor, TabCompleter {

    final List<SubCommand> COMMANDS = new ArrayList<>();

    final JavaPlugin plugin;

    Function<String, String> message = String::toUpperCase;

    /**
     * 注册次级指令
     * <p>
     * 遇到重复的指令时，会删除之前的次级指令
     * @param command 子指令
     */
    public void register(SubCommand command) {
        if (plugin.isEnabled()) return;
        command.mainCommand = this;
        COMMANDS.removeIf(subCommand -> subCommand.cmd.equals(command.cmd));
        COMMANDS.add(command);
    }

    /**
     * 当插件开启时, 需调用启动函数, 同时会注册事件监听器
     *
     * @param command 指令名称
     */
    public void onEnable(String command) {
        plugin.getCommand(command).setExecutor(this);
        plugin.getCommand(command).setTabCompleter(this);
        Collections.sort(COMMANDS);
        COMMANDS.stream().filter(subCommand -> subCommand instanceof Listener).forEach(subCommand -> Bukkit.getPluginManager().registerEvents((Listener) subCommand, plugin));
        COMMANDS.forEach(SubCommand::onEnable);
        plugin.getLogger().info("Load " + COMMANDS.size() + " Commands");
    }

    /**
     * 当插件重载时, 需要调用重载函数
     */
    public void onReload() {
        COMMANDS.forEach(SubCommand::onReload);
    }

    /**
     * 当插件关闭时, 需要调用关闭函数
     */
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
            MessageUtil.getInst().builder().add("§0-§8 --§7 ---§c ----§4 -----§b " + plugin.getName() + "§4 -----§c ----§7 ---§8 --§0 -").show("§7" + plugin.getDescription().getVersion() + " - " + plugin.getDescription().getAuthors()).send(sender);
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
