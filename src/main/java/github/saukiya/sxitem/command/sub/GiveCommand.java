package github.saukiya.sxitem.command.sub;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.command.SubCommand;
import github.saukiya.sxitem.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Saukiya
 */
public class GiveCommand extends SubCommand {

    public GiveCommand() {
        super("give");
        setArg(" <ItemName> <Player> <Amount>");
    }

    /**
     * TODO 需重制: 无法重复检测map+无法直接give简化id
     */
    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            SXItem.getItemManager().sendItemMapToPlayer(sender, sender instanceof Player ? new String[]{""} : new String[0]);
            return;
        }
        Player player = null;

        if (!SXItem.getItemManager().hasItem(args[1])) {
            SXItem.getItemManager().sendItemMapToPlayer(sender, args[1]);
            return;
        }
        if (args.length > 2) {
            player = Bukkit.getPlayerExact(args[2]);
            if (player == null) {
                sender.sendMessage(Message.getMsg(Message.ADMIN__NO_ONLINE));
                return;
            }
        } else if (sender instanceof Player) {
            player = (Player) sender;
        }

        int amount = args.length > 3 ? Integer.parseInt(args[3].replaceAll("[^\\d]", "")) : 1;

        if (player != null) {
            Inventory inv = player.getInventory();
            for (int i = 0; i < amount; i++) {
                ItemStack itemStack = SXItem.getItemManager().getItem(args[1], player);
                if (inv.firstEmpty() != -1) {
                    inv.addItem(itemStack);
                } else {
                    Item item = player.getWorld().dropItem(player.getLocation(), itemStack);
                    item.setPickupDelay(40);
                }
            }
            sender.sendMessage(Message.getMsg(Message.ADMIN__GIVE_ITEM, player.getName(), String.valueOf(amount), args[1]));
        } else {
            sender.sendMessage(Message.getMsg(Message.ADMIN__NO_CONSOLE));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return SXItem.getItemManager().getItemList().stream().filter(itemName -> itemName.contains(args[1])).collect(Collectors.toList());
        }
        if (args.length == 4) {
            return Collections.singletonList("1");
        }
        return null;
    }
}
