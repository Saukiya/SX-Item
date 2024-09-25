package github.saukiya.sxitem.command;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.item.ItemManager;
import github.saukiya.sxitem.util.Message;
import github.saukiya.util.command.SubCommand;
import github.saukiya.util.nms.ComponentUtil;
import github.saukiya.util.nms.MessageUtil;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class ComponentCommand extends SubCommand {

    private final Map<String, String> preInput = new HashMap<>();

    public ComponentCommand() {
        super("component", 60);
        setArg("<set/remove> <key> <json>");
        preInput.put("minecraft:max_stack_size", "set");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        ItemStack itemStack;
        String operate = "all";
        if (sender instanceof Player) {
            Player player = (Player) sender;
            itemStack = player.getInventory().getItemInMainHand();
            operate = args.length > 1 ? args[1] : operate;
        } else {
            Player player = args.length > 2 ? Bukkit.getPlayerExact(args[2]) : null;
            itemStack = SXItem.getItemManager().getItem(args.length > 1 ? args[1] : null, player);
        }
        if (itemStack.getType().isAir()) {
            MessageUtil.send(sender, Message.GIVE__NO_ITEM.get());
            if (sender instanceof ConsoleCommandSender) {
                sender.sendMessage("Can Use: /si component [item] <player>");
            }
            return;
        }

        val wrapper = ComponentUtil.getInst().getItemWrapper(itemStack);
        JsonObject json = ((JsonObject) wrapper.toJson());
        
        switch (operate) {
            case "set":
                if (args.length < 4) {
                    MessageUtil.send(sender, Message.ADMIN__NO_FORMAT.get());
                    return;
                }
                json.add(args[2], JsonParser.parseString(args[3]));
                break;
            case "all":
            default:
                sendData(sender, itemStack, json);
        }
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            switch (args.length) {
                case 2:
                    return Arrays.asList("all", "set", "remove");
                case 3:
                    return new ArrayList<>(preInput.keySet());
                case 4:
                    return Collections.singletonList(args[3].isEmpty() ? preInput.getOrDefault(args[2], "") : "");
            }
        } else {
            switch (args.length) {
                case 2:
                    return SXItem.getItemManager().getItemList().stream().filter(itemName -> itemName.contains(args[1])).collect(Collectors.toList());
                case 3:
                    return null;
            } 
        }
        return Collections.emptyList();
    }

    private void sendData(CommandSender sender, ItemStack item, JsonObject json) {
        val cb = MessageUtil.getInst().builder()
                .show(item)
                .add("§7[")
                .add(item.getType().name())
                .show(Message.NBT__CLICK_COPY.get())
                .suggestCommand(item.getType().name());
        String keys = String.join("/", ItemManager.getMaterialString(item.getType()));
        if (!keys.isEmpty()) cb.add("-").add(keys).show(Message.NBT__CLICK_COPY.get()).suggestCommand(keys);
        cb.add("§7] §cItem-Components:");
        cb.send(sender);

        sendComponent(sender, json);
    }

    private void sendComponent(CommandSender sender, JsonElement json) {
        for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet() ) {
            String key = entry.getKey();
            String jsonString = ComponentUtil.getGson().toJson(entry.getValue());

            val messageBuilder = MessageUtil.getInst().builder();

            if (sender instanceof Player) {
                int index = key.indexOf(':');
                messageBuilder
                        .add("§7-§c[" + key.substring(0, index) + "]").show(key)
                        .add(" §7" + key.substring(index + 1)).show(jsonString)
                        .send(sender);
            } else {
                messageBuilder
                        .add("§c " + String.format("%-25s", key)).suggestCommand(key)
                        .add((jsonString.indexOf('\n') < 0 ? '\t' : '\n') + jsonString).suggestCommand(jsonString)
                        .send(sender);
            }
        }
    }
}
