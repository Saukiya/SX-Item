package github.saukiya.sxitem.command.sub;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.command.SenderType;
import github.saukiya.sxitem.command.SubCommand;
import github.saukiya.sxitem.data.item.IGenerator;
import github.saukiya.sxitem.data.item.ItemManager;
import github.saukiya.sxitem.util.Message;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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
            sender.sendMessage(Message.getMsg(Message.ADMIN__NO_FORMAT));
            return;
        }
        String itemName = args[1];
        Player player = (Player) sender;
        ItemStack itemStack = player.getEquipment().getItemInMainHand();
        if (itemStack.getType() == Material.AIR) {
            player.sendMessage(Message.getMsg(Message.ADMIN__NO_ITEM));
            return;
        }
        if (SXItem.getItemManager().hasItem(itemName)) {
            player.sendMessage(Message.getMsg(Message.ADMIN__HAS_ITEM, itemName));
            return;
        }
        try {
            if (SXItem.getItemManager().saveItem(itemName, itemStack, args.length > 2 ? args[2] : "Default")) {
                sender.sendMessage(Message.getMsg(Message.ADMIN__SAVE_ITEM, itemName));
            } else {
                sender.sendMessage(Message.getMsg(Message.ADMIN__SAVE_NO_TYPE, itemName));
            }
        } catch (IOException e) {
            e.printStackTrace();
            sender.sendMessage(Message.getMsg(Message.ADMIN__SAVE_ITEM_ERROR, itemName));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 3) {
            return ItemManager.getGenerators().stream().map(IGenerator::getType).collect(Collectors.toList());
        }
        return null;
    }
}
