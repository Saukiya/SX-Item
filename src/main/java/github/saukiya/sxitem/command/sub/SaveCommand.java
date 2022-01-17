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
        super("save");
        setArg(" <ItemName> [Type]");
        setType(SenderType.PLAYER);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            MessageUtil.send(sender, Message.getMsg(Message.ADMIN__NO_FORMAT));
            return;
        }
        String itemName = args[1];
        Player player = (Player) sender;
        ItemStack itemStack = player.getEquipment().getItemInHand();
        if (itemStack.getType() == Material.AIR) {
            MessageUtil.send(player, Message.getMsg(Message.ADMIN__NO_ITEM));
            return;
        }
        if (SXItem.getItemManager().hasItem(itemName)) {
            MessageUtil.send(player, Message.getMsg(Message.ADMIN__HAS_ITEM, itemName));
            return;
        }
        try {
            if (SXItem.getItemManager().saveItem(itemName, itemStack, args.length > 2 ? args[2] : "Default")) {
                MessageUtil.send(sender, Message.getMsg(Message.ADMIN__SAVE_ITEM, itemName));
            } else {
                MessageUtil.send(sender, Message.getMsg(Message.ADMIN__SAVE_NO_TYPE, itemName));
            }
        } catch (IOException e) {
            e.printStackTrace();
            MessageUtil.send(sender, Message.getMsg(Message.ADMIN__SAVE_ITEM_ERROR, itemName));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 3) {
            return new ArrayList<>(ItemManager.getGenerators().keySet());
        }
        return null;
    }
}
