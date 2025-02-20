package github.saukiya.sxitem.command;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.event.SXItemReloadEvent;
import github.saukiya.sxitem.util.Config;
import github.saukiya.sxitem.util.Message;
import github.saukiya.tools.command.SubCommand;
import github.saukiya.tools.nms.MessageUtil;
import github.saukiya.tools.util.LocalizationUtil;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

/**
 * 插件重载指令
 */
public class ReloadCommand extends SubCommand {

    public ReloadCommand() {
        super("reload", 100);
    }

    @SneakyThrows
    @Override
    public void onCommand(CommandSender sender, String[] args) {
        LocalizationUtil.saveResource(SXItem.getInst());
        Config.loadConfig();
        Config.setup();
        Message.loadMessage();
        SXItem.getScriptManager().reload();
        SXItem.getRandomManager().loadData();
        SXItem.getItemManager().loadItemData();
        SXItem.getMainCommand().onReload();
        SXItem.getSdf().remove();
        Bukkit.getPluginManager().callEvent(SXItemReloadEvent.getInst());
        MessageUtil.send(sender, Message.ADMIN__PLUGIN_RELOAD.get());
        Bukkit.getOnlinePlayers().forEach(player -> SXItem.getItemManager().checkUpdateItem(player, player.getInventory().getContents()));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
