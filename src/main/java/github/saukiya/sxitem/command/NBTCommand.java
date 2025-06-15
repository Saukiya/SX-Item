package github.saukiya.sxitem.command;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.util.Message;
import github.saukiya.tools.command.SubCommand;
import github.saukiya.tools.nbt.TagBase;
import github.saukiya.tools.nbt.TagCompound;
import github.saukiya.tools.nbt.TagList;
import github.saukiya.tools.nbt.TagType;
import github.saukiya.tools.nms.MessageUtil;
import github.saukiya.tools.nms.NbtUtil;
import github.saukiya.tools.util.ReMaterial;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * NBT显示指令
 * <pre>
 * Player:
 *  <code>/si nbt</code> - 查看手持物品的NBT标签
 *  <code>/si nbt set SXItem.ItemKey Default-1</code> - 设置手持物品的指定NBT
 *  <code>/si nbt remove SXItem</code> - 移除手持物品的指定NBT
 * Console:
 *  <code>/si nbt Default-1</code> - 查看Default-1的NBT标签
 *  <code>/si nbt Default-1 player</code> - 查看Default-1的NBT标签 (通过player生成)
 * </pre>
 */
public class NBTCommand extends SubCommand {

    public NBTCommand() {
        super("nbt", 40);
        setArg("<set/remove> <key> <value>");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        ItemStack item;
        String operation = "all";
        if (sender instanceof Player) {
            Player player = (Player) sender;
            item = player.getEquipment().getItemInHand();
            operation = args.length > 1 ? args[1] : operation;
        } else {
            Player player = args.length > 2 ? Bukkit.getPlayerExact(args[2]) : null;
            item = SXItem.getItemManager().getItem(args.length > 1 ? args[1] : null, player);
        }
        if (item.getType() == Material.AIR) {
            MessageUtil.send(sender, Message.GIVE__NO_ITEM.get());
            if (sender instanceof ConsoleCommandSender) {
                sender.sendMessage("Console Use: /si nbt [item] <player>");
            }
            return;
        }
        switch (operation) {
            case "set":
                if (args.length < 4) {
                    MessageUtil.send(sender, Message.ADMIN__NO_FORMAT.get());
                    return;
                }
                NbtUtil.getInst().getItemTagWrapper(item).builder()
                        .set(args[2], args[3])
                        .save();
                sender.sendMessage("§7Set: " + args[2]);
                break;
            case "remove":
                if (args.length < 3) {
                    MessageUtil.send(sender, Message.ADMIN__NO_FORMAT.get());
                    return;
                }
                NbtUtil.getInst().getItemTagWrapper(item).builder()
                        .remove(args[2])
                        .save();
                sender.sendMessage("§cRemove: " + args[2]);
                break;
            default:
                val tag = NbtUtil.getInst().getItemTag(item);
                val cb = MessageUtil.getInst().builder()
                        .show(item)
                        .add("§7[")
                        .add(item.getType().name())
                        .show(Message.NBT__CLICK_COPY.get())
                        .suggestCommand(item.getType().name());
                String keys = ReMaterial.getKey(item.getType());
                if (!keys.isEmpty()) cb.add("-").add(keys).show(Message.NBT__CLICK_COPY.get()).suggestCommand(keys);
                cb.add("] §cItem-NBT:").send(sender);
                sendNBT(sender, tag, "");
                break;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            if (sender instanceof Player) {
                return !args[1].isEmpty() ? Arrays.asList("set", "remove") : Collections.emptyList();
            }
            return SXItem.getItemManager().getItemList().stream().filter(itemName -> itemName.contains(args[1])).collect(Collectors.toList());
        }
        return sender instanceof Player ? Collections.emptyList() : null;
    }

    public static void sendNBT(CommandSender sender, ItemStack item) {
        sendNBT(sender, NbtUtil.getInst().getItemTag(item), "");
    }

    public static void sendNBT(CommandSender sender, TagCompound tagCompound, String prefix) {
        if (tagCompound == null) return;
        String path, typeShow, nbtShow;
        for (Map.Entry<String, TagBase<?>> entry : tagCompound.entrySet()) {
            TagBase<?> tagBase = entry.getValue();
            path = prefix + entry.getKey();
            typeShow = tagBase.getTypeId().name();
            if (tagBase.getTypeId() == TagType.COMPOUND) {
                sendNBT(sender, (TagCompound) tagBase, path + '.');
                continue;
            }
            if (tagBase.getTypeId() == TagType.LIST) {
                TagList tagList = (TagList) tagBase;
                if (!tagList.isEmpty()) {
                    typeShow += "-" + tagList.get(0).getTypeId();
                }
                nbtShow = tagList.stream().flatMap(tag -> Arrays.stream(tag.getValue().toString().split("\n"))).collect(Collectors.joining("\n"));
            } else {
                nbtShow = tagBase.getValue().toString();
            }
            val messageBuilder = MessageUtil.getInst().builder();
            if (sender instanceof Player) {
                try {
                    messageBuilder
                            .add("§4[X]").show("§cRemove " + path).runCommand("/si nbt remove " + path)
                            .add("§8-§c[Type-" + typeShow.charAt(0) + "]").show(typeShow).suggestCommand(path)
                            .add("§7 " + path).show(nbtShow).suggestCommand(nbtShow)
                            .send(sender);
                } catch (Exception e) {
                    // com.google.gson.JsonParseException: Disallowed chat character: '
                    messageBuilder
                            .add("§4[X]").show("§cRemove " + path).runCommand("/si nbt remove " + path)
                            .add("§8-§c[Type-" + typeShow.charAt(0) + "]").show(typeShow).suggestCommand(path)
                            .add("§7 " + path).show("&4Show Error")
                            .send(sender);
                    SXItem.getInst().getLogger().severe(e.getMessage());
                    e.printStackTrace();
                }
            } else {
                messageBuilder
                        .add("§c " + String.format("%-32s", path))
                        .add("\t" + nbtShow.replace("\n", "§c\\n§f"))
                        .send(sender);
            }
        }
    }
}
