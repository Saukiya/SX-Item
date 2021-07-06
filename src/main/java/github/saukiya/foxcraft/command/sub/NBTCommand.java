package github.saukiya.foxcraft.command.sub;

import github.saukiya.foxcraft.SXItem;
import github.saukiya.foxcraft.command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.List;
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
        String str = SXItem.getNbtUtil().getAllNBT(item);
        sender.sendMessage("\n\n" + str + "\n");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return args.length == 2 ? SXItem.getItemDataManager().getItemList().stream().filter(itemName -> itemName.contains(args[1])).collect(Collectors.toList()) : null;
    }
}
