package github.saukiya.sxitem.command.sub;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.command.SubCommand;
import github.saukiya.sxitem.data.item.ItemManager;
import github.saukiya.sxitem.event.SXItemReloadEvent;
import github.saukiya.sxitem.util.Config;
import github.saukiya.sxitem.util.Message;
import github.saukiya.sxitem.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

/**
 * @author Saukiya
 */
public class ReloadCommand extends SubCommand {

    public ReloadCommand() {
        super("reload");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Config.loadConfig();
        Config.setup();
        Message.loadMessage();
        SXItem.getRandomManager().loadData();
        ItemManager.loadMaterialData();
        SXItem.getItemManager().loadItemData();
        SXItem.getMainCommand().onReload();
        SXItem.getSdf().remove();
        Bukkit.getOnlinePlayers().forEach(player -> SXItem.getItemManager().updateItem(player, player.getInventory().getContents()));
        Bukkit.getPluginManager().callEvent(SXItemReloadEvent.getInst());
        MessageUtil.send(sender, Message.ADMIN__PLUGIN_RELOAD.get());
    }
}
