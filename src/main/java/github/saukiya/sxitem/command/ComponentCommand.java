package github.saukiya.sxitem.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.item.ItemManager;
import github.saukiya.sxitem.util.Message;
import github.saukiya.util.command.SenderType;
import github.saukiya.util.command.SubCommand;
import github.saukiya.util.nms.ComponentUtil;
import github.saukiya.util.nms.MessageUtil;
import github.saukiya.util.nms.NMS;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ComponentCommand extends SubCommand {
    public ComponentCommand() {
        super("component", 60);
        setArg("[get] [sx-item]");
        setType(SenderType.PLAYER);
    }
    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (!checkArgument(args)) {
            MessageUtil.send(player, "IllegalArgument");
            return;
        }
        String operate = args.length < 2 ? "get" : args[1];
        ItemStack itemStack = args.length < 3 ? player.getInventory().getItemInMainHand() : SXItem.getItemManager().getItem(args[2]);
        if (itemStack == null || itemStack.getType().isAir()) { return;}
        switch (operate) {
            case "set":
                break;
            case "get":
            default:
                sendData(player, itemStack);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        switch (args.length) {
            case 2:
                return Arrays.asList("get");
            case 3:
                if ("get".equals(args[1])) {
                    return SXItem.getItemManager().getItemList().stream().filter(itemName -> itemName.toLowerCase().contains(args[2].toLowerCase())).collect(Collectors.toList());
                } else {
                    //TODO set
                }
            default:
                return new ArrayList<>();
        }
    }

    private void sendData(CommandSender sender, ItemStack itemStack) {
        MessageUtil.Builder cb = MessageUtil.getInst().builder();
        String itemName = itemStack.getItemMeta().getDisplayName();
        if ((itemName == null || itemName.isEmpty())) {
            cb.add("§7[§r");
            if (NMS.compareTo(1,20,1) >= 0) {
                TranslatableComponent component = new TranslatableComponent(itemStack.getTranslationKey());
                cb.add(component);
            } else {
                cb.add(itemStack.getType().name());
            }
            cb.add("§7]");
        } else {
            cb.add("§7[§r"+ itemName + "§7]");
        }
        cb.show(itemStack);
        cb.add("§7[")
                .add(itemStack.getType().name())
                .show(Message.NBT__CLICK_COPY.get())
                .suggestCommand(itemStack.getType().name());
        String keys = String.join("/", ItemManager.getMaterialString(itemStack.getType()));
        if (!keys.isEmpty()) cb.add("-").add(keys).show(Message.NBT__CLICK_COPY.get()).suggestCommand(keys);
        cb.add("§7] ");
        cb.show(itemStack);
        cb.add("§cHas data:");
        cb.send(sender);
        //Components
        ComponentUtil.ItemWrapper wrapper = ComponentUtil.getInst().getItemWrapper(itemStack);
        JsonElement json = wrapper.toJson();
        MessageUtil.send(sender,"§7§m          §cComponents§7§m          ");
        sendComponent(sender, json);
    }

    private void sendComponent(CommandSender sender, JsonElement json) {
        for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet() ) {
            String key = entry.getKey();
            String namespace = key.split(":")[0];
            String componentName = key.split(":")[1];
            JsonElement componentJson = entry.getValue();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            MessageUtil.getInst().builder()
                    .add("§7- ")
                    .add("§c[" + namespace.substring(0, 1).toUpperCase() + namespace.substring(1) + "]")
                    .show(key)
                    .suggestCommand(key)
                    .add(" ")
                    .add("§7" + componentName.substring(0, 1).toUpperCase() + componentName.substring(1))
                    .suggestCommand(key)
                    .show(gson.toJson(componentJson))
                    .send(sender);
        }
    }
    private boolean checkArgument(String[] args) {
        if (args.length > 3) {
            return false;
        }
        if (args.length >= 2) {
            if (!"get".equals(args[1]) && !"set".equals(args[1])) {
                return false;
            }
            if ("set".equals(args[1]) && args.length < 3) {
                return false;
            }
        }

        return true;
    }
}
