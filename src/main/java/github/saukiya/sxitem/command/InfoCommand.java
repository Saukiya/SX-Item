package github.saukiya.sxitem.command;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.item.IGenerator;
import github.saukiya.tools.command.SubCommand;
import github.saukiya.tools.nms.NMS;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InfoCommand extends SubCommand implements Listener {

    public InfoCommand() {
        super("info", 0);
        setArg("[item] <player> <key:value...>");
        setHide(true);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            SXItem.getItemManager().sendItemInfoToPlayer(sender, sender instanceof Player ? "" : null);
            return;
        }
        Player player = null;

        IGenerator ig = SXItem.getItemManager().getGenerator(args[1]);
        if (ig == null) {
            SXItem.getItemManager().sendItemInfoToPlayer(sender, args[1]);
            return;
        }
        if (args.length > 2) {
            player = Bukkit.getPlayerExact(args[2]);
        } else if (sender instanceof Player) {
            player = (Player) sender;
        }

        Map<String, String> otherMap = null;
        if (args.length > 2) {
            otherMap = new HashMap<>();
            for (int i = 2; i < args.length; i++) {
                String[] splits = args[i].split(":", 2);
                if (splits.length == 1) continue;
                otherMap.put(splits[0], splits[1]);
            }
        }
        ItemStack itemStack = SXItem.getItemManager().getItem(ig, player, otherMap);
        if (NMS.compareTo(1, 20, 5) >= 0) {
            ComponentCommand.sendData(sender, itemStack);
        }
        NBTCommand.sendNBT(sender, itemStack);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        switch (args.length) {
            case 2:
                return SXItem.getItemManager().getItemList().stream().filter(itemName -> itemName.toLowerCase().contains(args[1].toLowerCase())).collect(Collectors.toList());
            case 3:
                return null;
            default:
                return Collections.emptyList();
        }
    }

    @EventHandler
    void on(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        if (event.getItem().hasMetadata("SX-Item|DropData") && !player.isOp() && event.getItem().getMetadata("SX-Item|DropData").stream().noneMatch(data -> data.value().equals(player.getName()))) {
            event.getItem().setPickupDelay(5);
            event.setCancelled(true);
        }
    }
}
