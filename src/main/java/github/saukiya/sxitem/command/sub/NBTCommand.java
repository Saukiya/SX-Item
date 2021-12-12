package github.saukiya.sxitem.command.sub;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.command.SubCommand;
import github.saukiya.sxitem.nms.TagBase;
import github.saukiya.sxitem.nms.TagCompound;
import github.saukiya.sxitem.util.MessageUtil;
import github.saukiya.sxitem.util.NbtUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * NBT显示指令
 *
 * @author Saukiya
 */
public class NBTCommand extends SubCommand {

    public NBTCommand() {
        super("nbt");
        setArg(" <ItemName>");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        ItemStack item;
        Player player;
        if (args.length > 1 && SXItem.getItemDataManager().hasItem(args[1])) {
            item = SXItem.getItemDataManager().getItem(args[1], sender instanceof Player ? (Player) sender : null);
        } else if (args.length > 1 && (player = Bukkit.getPlayerExact(args[1])) != null) {
            EntityEquipment eq = player.getEquipment();
            item = eq.getItemInMainHand();
        } else if (sender instanceof Player) {
            player = (Player) sender;
            EntityEquipment eq = player.getEquipment();
            item = eq.getItemInMainHand();
        } else {
            SXItem.getItemDataManager().sendItemMapToPlayer(sender);
            return;
        }
        sender.sendMessage("§c物品拥有以下NBT:");
        sendNBT("§7", NbtUtil.getInst().getItemNBT(item), sender);
    }

    public void sendNBT(String prefix, TagCompound tagCompound, CommandSender sender) {
        for (Map.Entry<String, TagBase> entry : tagCompound.entrySet()) {
            TagBase tagBase = entry.getValue();
            if (tagBase instanceof TagCompound) {
                sendNBT(prefix + entry.getKey() + '.', (TagCompound) tagBase, sender);
            } else {
                MessageUtil.getInst().send(sender, MessageUtil.getInst().getTextComponent(prefix + entry.getKey(), entry.getValue().toString(), null));
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return args.length == 2 ? SXItem.getItemDataManager().getItemList().stream().filter(itemName -> itemName.contains(args[1])).collect(Collectors.toList()) : null;
    }
}
