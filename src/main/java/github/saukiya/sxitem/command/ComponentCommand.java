package github.saukiya.sxitem.command;

import com.google.gson.Gson;
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
import java.util.stream.Stream;

public class ComponentCommand extends SubCommand {

    private final Map<String, List<String>> preInput = new HashMap<>();

    private final Gson gson = new Gson();

    public ComponentCommand() {
        super("component", 60);
        setArg("<all/set> <key> <json>");
        preInput.put("minecraft:item_name", Collections.singletonList("\"defaultName\""));
        preInput.put("minecraft:max_damage", Collections.singletonList("1"));
        preInput.put("minecraft:max_stack_size", Collections.singletonList("10"));
        preInput.put("minecraft:hide_tooltip", Collections.singletonList("{}"));
        preInput.put("minecraft:attribute_modifiers", Arrays.asList(
                        "[{type:\"generic.scale\",id:\"example:grow\",amount:3,operation:\"add_multiplied_base\",slot:\"hand\"}]",
                        "[{type:\"generic.gravity\",id:\"example:gravity\",amount:-0.9,operation:\"add_multiplied_base\",slot:\"hand\"}]",
                        "[{type:\"generic.step_height\",id:\"example:step_height\",amount:3,operation:\"add_multiplied_base\",slot:\"hand\"}]"
                )
        );
        preInput.put("minecraft:rarity", Arrays.asList("common", "uncommon", "rare", "epic"));

        List<String> emptyList = Collections.emptyList();
        ComponentUtil.getInst().getItemKeys().forEach(key -> preInput.putIfAbsent(key, emptyList));
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
                sender.sendMessage("Console Use: /si component [item] <player>");
            }
            return;
        }

        val wrapper = ComponentUtil.getInst().getItemWrapper(itemStack);
        JsonObject json = (JsonObject) wrapper.toJson();

        switch (operate) {
            case "set":
                if (args.length < 4) {
                    MessageUtil.send(sender, Message.ADMIN__NO_FORMAT.get());
                    return;
                }
                json.add(args[2], JsonParser.parseString(args[3]));
                wrapper.setFromJson(json);
                wrapper.save();
                sender.sendMessage("§7Set: " + args[2]);
                break;
            case "remove":
                if (args.length < 3) {
                    MessageUtil.send(sender, Message.ADMIN__NO_FORMAT.get());
                    return;
                }
                wrapper.set(args[2], null);
                wrapper.save();
                sender.sendMessage("§cRemove: " + args[2]);
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
                    return Stream.of("all", "set", "remove").filter(Key -> Key.contains(args[1])).collect(Collectors.toList());
                case 3:
                    if ("set|remove".contains(args[1])) {
                        if (args[2].isEmpty()) {
                            return preInput.entrySet().stream().filter(entry -> !entry.getValue().isEmpty()).map(Map.Entry::getKey).collect(Collectors.toList());
                        } else {
                            return preInput.keySet().stream().filter(key -> key.contains(args[2])).collect(Collectors.toList());
                        }
                    }
                    return Collections.emptyList();
                case 4:
                    return args[3].isEmpty() ? preInput.getOrDefault(args[2], Collections.emptyList()) : Collections.emptyList();
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
        for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
            String key = entry.getKey();
            String jsonString = ComponentUtil.getGson().toJson(entry.getValue());

            val messageBuilder = MessageUtil.getInst().builder();

            if (sender instanceof Player) {
                int index = key.indexOf(':');
                messageBuilder
                        .add("§4[X]").show("§cRemove " + key).runCommand("/si component remove " + key)
                        .add("§8-§c[" + key.substring(0, index) + "]").show(key).suggestCommand(key)
                        .add(" §7" + key.substring(index + 1)).show(jsonString).suggestCommand(gson.toJson(entry.getValue()))
                        .send(sender);
            } else {
                messageBuilder
                        .add("§c " + String.format("%-25s", key))
                        .add((jsonString.indexOf('\n') < 0 ? '\t' : '\n') + jsonString)
                        .send(sender);
            }
        }
    }
}
