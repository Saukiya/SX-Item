package github.saukiya.sxitem.command;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.item.IGenerator;
import github.saukiya.sxitem.util.Config;
import github.saukiya.sxitem.util.Message;
import github.saukiya.util.command.SubCommand;
import github.saukiya.util.nms.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Saukiya
 */
public class GiveCommand extends SubCommand implements Listener {

    public GiveCommand() {
        super("give", 0);
        setArg("[item] <player> <count> <key:value...>");
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
        if (player == null) {
            MessageUtil.send(sender, Message.ADMIN__NO_ONLINE.get());
            return;
        }

        int amount = args.length > 3 ? Integer.parseInt(args[3].replaceAll("\\D", "")) : 1;

        Map<String, String> otherMap = null;
        if (args.length > 4) {
            otherMap = new HashMap<>();
            for (int i = 4; i < args.length; i++) {
                String[] splits = args[i].split(":");
                otherMap.put(splits[0], splits[1]);
            }
        }

        Inventory inv = player.getInventory();
        for (int i = 0; i < amount; i++) {
            ItemStack itemStack = SXItem.getItemManager().getItem(ig, player, otherMap);

            if (inv.firstEmpty() != -1) {
                inv.addItem(itemStack);
            } else if (Config.getConfig().getBoolean(Config.GIVE_OVERFLOW_DROP)) {
                Item item = player.getWorld().dropItem(player.getLocation(), itemStack);
                item.setMetadata("SX-Item|DropData", new FixedMetadataValue(SXItem.getInst(), player.getName()));
                item.setPickupDelay(40);
            } else {
                SXItem.getInst().getLogger().warning("Give Error Player:" + player.getName() + " ItemKey:" + args[1]);
                ItemMeta meta = itemStack.getItemMeta();
                MessageUtil.send(player, Message.GIVE__GIVE_ITEM_ERROR.get(meta.hasDisplayName() ? meta.getDisplayName() : args[1]));
            }
        }
        MessageUtil.send(sender, Message.GIVE__GIVE_ITEM.get(player.getName(), String.valueOf(amount), args[1]));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return SXItem.getItemManager().getItemList().stream().filter(itemName -> itemName.toLowerCase().contains(args[1].toLowerCase())).collect(Collectors.toList());
        }
        if (args.length == 4) {
            return Collections.singletonList("1");
        }
        return args.length == 3 ? null : Collections.emptyList();
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
