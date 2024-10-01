package github.saukiya.sxitem.command;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.item.ItemManager;
import github.saukiya.sxitem.util.Message;
import github.saukiya.util.command.SenderType;
import github.saukiya.util.command.SubCommand;
import github.saukiya.util.nms.MessageUtil;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Saukiya
 */
public class SaveCommand extends SubCommand {

    public SaveCommand() {
        super("save", 20);
        setArg("[item] <type>");
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
        if (args.length == 2 && args[1].isEmpty()) return Arrays.asList("[itemName]", args[1]);
        if (args.length == 3) {
            return new ArrayList<>(ItemManager.getLoadFunction().keySet());
        }
        return Collections.emptyList();
    }
}
