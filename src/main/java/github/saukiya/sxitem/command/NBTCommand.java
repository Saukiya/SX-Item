package github.saukiya.sxitem.command;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.item.ItemManager;
import github.saukiya.sxitem.util.Message;
import github.saukiya.util.base.Base;
import github.saukiya.util.command.SubCommand;
import github.saukiya.util.nms.MessageUtil;
import github.saukiya.util.nms.NbtUtil;
import lombok.val;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
        super("nbt", 40);
        setArg("<get/set/remove> <key> <value>");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        ItemStack item;
        String operation = "get";
        if (sender instanceof Player) {
            Player player = (Player) sender;
            item = player.getEquipment().getItemInHand();
            operation = args.length > 1 ? args[1] : operation;
        } else {
            item = SXItem.getItemManager().getItem(args.length > 1 ? args[1] : "Default-1");
        }
        if (item.getType() == Material.AIR) {
            MessageUtil.send(sender, Message.GIVE__NO_ITEM.get());
            return;
        }
        NbtUtil.ItemWrapper itemWrapper = NbtUtil.getInst().getItemTagWrapper(item);
        switch (operation) {
            case "get":
                val cb = MessageUtil.getInst().builder()
                        .add("§7[")
                        .add(item.getType().name())
                        .show(Message.NBT__CLICK_COPY.get())
                        .suggestCommand(item.getType().name());
                String keys = String.join("/", ItemManager.getMaterialString(item.getType()));
                if (!keys.isEmpty()) cb.add("-").add(keys).show(Message.NBT__CLICK_COPY.get()).suggestCommand(keys);
                cb.add("] ");
                if (itemWrapper != null) cb.add("§cItem-NBT").show(itemWrapper.toString());
                cb.send(sender);
                sendNBT("", itemWrapper, sender);
                break;
            case "set":
                if (args.length < 4) {
                    MessageUtil.send(sender, Message.ADMIN__NO_FORMAT.get());
                    return;
                }
                itemWrapper.set(args[2], args[3]);
                itemWrapper.save();
                break;
            case "remove":
                if (args.length < 3) {
                    MessageUtil.send(sender, Message.ADMIN__NO_FORMAT.get());
                    return;
                }
                itemWrapper.remove(args[2]);
                itemWrapper.save();
                break;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return args.length == 2 ? SXItem.getItemManager().getItemList().stream().filter(itemName -> itemName.contains(args[1])).collect(Collectors.toList()) : null;
    }

    public static void sendNBT(String prefix, Base.Compound tagCompound, CommandSender sender) {
        if (tagCompound == null) return;
        String path, typeShow, nbtShow;
        // TODO 往Base.Compound加入
        Map<String, Object> test = NbtUtil.getInst().getNMSValue(((NbtUtil.ItemWrapper) tagCompound).getHandle());
        for (Map.Entry<String, Object> entry : test.entrySet()) {
            SXItem.getInst().getLogger().warning(entry.getKey() + ": " + entry.getValue());
        }
//        for (Map.Entry<String, TagBase> entry : tagCompound.entrySet()) {
//            TagBase tagBase = entry.getValue();
//            path = prefix + entry.getKey();
//            typeShow = tagBase.getTypeId().name();
//            if (tagBase.getTypeId() == TagType.COMPOUND) {
//                sendNBT(path + '.', (TagCompound) tagBase, sender);
//                continue;
//            }
//            if (tagBase.getTypeId() == TagType.LIST) {
//                TagList tagList = (TagList) tagBase;
//                if (!tagList.isEmpty())
//                    typeShow += "-" + tagList.get(0).getTypeId();
//                nbtShow = ((TagList) tagBase).stream().flatMap(tag -> Arrays.stream(tag.getValue().toString().split("\n"))).collect(Collectors.joining("\n"));
//            } else {
//                nbtShow = entry.getValue().toString();
//            }
//            MessageUtil.getInst().builder().add("§7- ").add("§c[Type-" + typeShow.charAt(0) + "]").show(typeShow).add(" ").add("§7" + path).suggestCommand(path).show(nbtShow).send(sender);
//        }
    }
}
