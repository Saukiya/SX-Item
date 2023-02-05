package github.saukiya.sxitem.command.sub;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.command.SenderType;
import github.saukiya.sxitem.command.SubCommand;
import github.saukiya.sxitem.data.item.ItemManager;
import github.saukiya.sxitem.util.Message;
import github.saukiya.sxitem.util.MessageUtil;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Saukiya
 */
public class SaveCommand extends SubCommand {
    public SaveCommand() {
        super("save", 3);
        setArg("<ItemName> [Type]");
        setType(SenderType.PLAYER);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            MessageUtil.send(sender, Message.ADMIN__NO_FORMAT.get());
            return;
        }
        String itemName = args[1];
        Player player = (Player) sender;
        ItemStack itemStack = player.getEquipment().getItemInHand();
        if (itemStack.getType() == Material.AIR) {
            MessageUtil.send(player, Message.GIVE__NO_ITEM.get());
            return;
        }
        if (SXItem.getItemManager().hasItem(itemName)) {
            MessageUtil.send(player, Message.SAVE__HAS_ITEM.get(itemName));
            return;
        }
        try {
            if (SXItem.getItemManager().saveItem(itemName, itemStack.clone(), args.length > 2 ? args[2] : "Default")) {
                MessageUtil.send(sender, Message.SAVE__SAVE_ITEM.get(itemName));
            } else {
                MessageUtil.send(sender, Message.SAVE__SAVE_NO_TYPE.get(itemName));
            }
        } catch (IOException e) {
            e.printStackTrace();
            MessageUtil.send(sender, Message.SAVE__SAVE_ITEM_ERROR.get(itemName));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 3) {
            return new ArrayList<>(ItemManager.getLoadFunction().keySet());
        }
        return null;
    }
}
