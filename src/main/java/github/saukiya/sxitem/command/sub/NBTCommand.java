package github.saukiya.sxitem.command.sub;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.command.SenderType;
import github.saukiya.sxitem.command.SubCommand;
import github.saukiya.sxitem.data.item.ItemManager;
import github.saukiya.sxitem.nbt.*;
import github.saukiya.sxitem.util.ComponentBuilder;
import github.saukiya.sxitem.util.MessageUtil;
import github.saukiya.sxitem.util.NbtUtil;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
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
        setArg(" <set/remove/all> <key> <value>");
        setType(SenderType.PLAYER);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        ItemStack item = player.getEquipment().getItemInHand();
        if (item.getType() != Material.AIR) {
            if (args.length < 2 || args[1].equals("all")) {
                ComponentBuilder cb = MessageUtil.getInst().componentBuilder()
                        .add("§7[")
                        .add(item.getType().name())
                        .show("点击复制")
                        .suggestCommand(item.getType().name());
                String keys = String.join("/", ItemManager.getMaterialString(item.getType()));
                if (keys.length() != 0) cb.add("-").add(keys).show("点击复制").suggestCommand(keys);
                cb.add("] §c物品拥有以下NBT:");
                cb.send(sender);
                sendNBT("§7", NbtUtil.getInst().getItemTag(item), sender);
                return;
            }

            NBTItemWrapper tagWrapper = NbtUtil.getInst().getItemTagWrapper(item);
            String key = args.length > 2 ? args[2] : null;
            String value = args.length > 3 ? args[3] : null;
            if (args[1].equals("set") && value != null) {
                tagWrapper.set(key, value);
                tagWrapper.save();
            } else if (args[1].equals("remove")) {
                tagWrapper.remove(key);
                tagWrapper.save();
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return args.length == 2 ? SXItem.getItemManager().getItemList().stream().filter(itemName -> itemName.contains(args[1])).collect(Collectors.toList()) : null;
    }

    public static void sendNBT(String prefix, TagCompound tagCompound, CommandSender sender) {
        if (tagCompound == null) return;
        String path, typeShow, nbtShow;
        for (Map.Entry<String, TagBase> entry : tagCompound.entrySet()) {
            TagBase tagBase = entry.getValue();
            path = prefix + entry.getKey();
            typeShow = tagBase.getTypeId().name();
            if (tagBase.getTypeId() == TagType.COMPOUND) {
                sendNBT(path + '.', (TagCompound) tagBase, sender);
                continue;
            }
            if (tagBase.getTypeId() == TagType.LIST) {
                TagList tagList = (TagList) tagBase;
                if (tagList.size() != 0)
                    typeShow += "-" + tagList.get(0).getTypeId();
                nbtShow = ((TagList) tagBase).stream().flatMap(tag -> Arrays.stream(tag.getValue().toString().split("\n"))).collect(Collectors.joining("\n"));
            } else {
                nbtShow = entry.getValue().getValue().toString();
            }
            MessageUtil.getInst().componentBuilder().add("§7- ").add("§c[Type]").show(typeShow).add(" ").add(path).show(nbtShow).send(sender);
        }
    }
}
