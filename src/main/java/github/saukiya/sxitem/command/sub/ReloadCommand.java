package github.saukiya.sxitem.command.sub;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.command.SubCommand;
import github.saukiya.sxitem.util.Config;
import github.saukiya.sxitem.util.Message;
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
        Message.loadMessage();
        SXItem.getItemDataManager().loadItemData();
        SXItem.getMainCommand().reload();
        Bukkit.getOnlinePlayers().forEach(player -> SXItem.getItemDataManager().updateItem(player, player.getInventory().getContents()));
        sender.sendMessage(Message.getMsg(Message.ADMIN__PLUGIN_RELOAD));
    }
}
