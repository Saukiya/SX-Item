package github.saukiya.sxitem.command;

import com.google.gson.JsonParser;
import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.item.IGenerator;
import github.saukiya.sxitem.util.Message;
import github.saukiya.util.command.SubCommand;
import github.saukiya.util.nbt.TagCompound;
import github.saukiya.util.nms.*;
import lombok.val;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class TestCommand extends SubCommand {
    public TestCommand() {
        super("test", -1);
        setHide();
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        ItemStack item;
        String operation = "all";
        Player player;
        sender.sendMessage("*该指令用于测试插件运行是否正常*");
        if (sender instanceof Player) {
            player = (Player) sender;
            item = player.getEquipment().getItemInHand();
            operation = args.length > 1 ? args[1] : operation;
        } else {
            player = args.length > 2 ? Bukkit.getPlayerExact(args[2]) : null;
            item = SXItem.getItemManager().getItem(args.length > 1 ? args[1] : "Default-1", player);
        }
        if (item.getType() == Material.AIR) {
            MessageUtil.send(sender, Message.GIVE__NO_ITEM.get());
            return;
        }
        switch (operation) {
            case "show":
                MessageUtil.getInst().builder().add(player.getEquipment().getItemInHand()).send(player);
                return;
            case "tran":
                MessageUtil.getInst().builder().add(new TranslatableComponent(args.length > 2 ? args[2] : "null")).send(player);
                return;
            case "clear":
                ItemUtil.getInst().clearAttribute(item);
                return;
        }

        Map<String, Object> input = new HashMap<>();
        input.put("minecraft:dyed_color", Collections.singletonMap("rgb", 16747238));
        input.put("minecraft:enchantment_glint_override", true);
        input.put("minecraft:food", ComponentUtil.getGson().fromJson("{can_always_eat:true,eat_seconds:5,nutrition:3,saturation:1}", Map.class));
        val wrapper = ComponentUtil.getInst().getItemWrapper(item);
        wrapper.setFromValue(input).save();
        SXItem.getInst().getLogger().warning("component: \n" + wrapper.toJsonString());
        sender.sendMessage("组件功能通过");

        val itemWrapper = NbtUtil.getInst().getItemTagWrapper(item);
        itemWrapper.set("test.string", "2333");
        itemWrapper.set("test.byteArray", new byte[]{1, 5, 10, 50, 100});
        itemWrapper.set("test.intArray", new int[]{1, 50, 100, 5000, 10000});
        itemWrapper.set("test.longArray", new long[]{1, 500, 10000, 5000000, 100000000});
        itemWrapper.set("test.boolean", true);
        itemWrapper.set("test.byte", (byte) 50);
        itemWrapper.set("test.short", (byte) 100);
        itemWrapper.set("test.int", 5000);
        itemWrapper.set("test.long", 10000L);
        itemWrapper.set("test.float", 500000.5F);
        itemWrapper.set("test.double", 1000000.5D);
        itemWrapper.set("test.stringList", Arrays.asList("测试文本一", "测试文本二"));
        TagCompound compound = new TagCompound();
        compound.set("string-1", "描述1");
        compound.set("string-2", "描述2");
        itemWrapper.set("test.nbtList", Arrays.asList(compound, compound));
        itemWrapper.save();
        sender.sendMessage("设置NBT物品通过");

        TagCompound tagCompound = (TagCompound) NbtUtil.getInst().toTag(itemWrapper);
        Map<String, Object> map = new HashMap<>();
        map.put("test.string", tagCompound.getString("test.string"));
        map.put("test.byteArray", tagCompound.getByteArray("test.byteArray"));
        map.put("test.intArray", tagCompound.getIntArray("test.intArray"));
        map.put("test.longArray", tagCompound.getLongArray("test.longArray"));
        map.put("test.boolean", tagCompound.getBoolean("test.boolean"));
        map.put("test.byte", tagCompound.getByte("test.byte"));
        map.put("test.short", tagCompound.getShort("test.short"));
        map.put("test.int", tagCompound.getInt("test.int"));
        map.put("test.long", tagCompound.getLong("test.long"));
        map.put("test.float", tagCompound.getFloat("test.float"));
        map.put("test.double", tagCompound.getDouble("test.double"));
        map.put("test.stringList", tagCompound.getStringList("test.stringList"));
        map.put("test.nbtList", tagCompound.getList("test.nbtList"));
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() == null) {
                sender.sendMessage("Error: " + entry.getKey());
            }
        }
        tagCompound.remove("test");
        SXItem.getInst().getLogger().info("tag-toString: \n" + tagCompound);
        SXItem.getInst().getLogger().info("tag: \n" + tagCompound.toJson()); // TODO 有问题
        sender.sendMessage("调用NBT物品通过");
        Bukkit.dispatchCommand(sender, "sxi nbt all");

        MessageUtil.send(sender, "[TITLE]测试Title:fadein-20 stay-100 fadeOut-100:20:100:100");
        MessageUtil.send(sender, "[ACTIONBAR]测试ActionBar");

        Map<String, Long> timeConsuming = new TreeMap<>();
        for (IGenerator generator : SXItem.getItemManager().getGeneratorList()) {
            long startTime = System.nanoTime();
            try {
                SXItem.getItemManager().getItem(generator, player);
                timeConsuming.put(generator.getKey(), System.nanoTime() - startTime);
            } catch (Exception e) {
                sender.sendMessage("物品: " + generator.getKey() + " 有问题");
            }
        }
        timeConsuming.forEach((k,v) -> sender.sendMessage("物品 " + k + " 耗时: " + (v / 1000000D) + " ms"));
        sender.sendMessage("获取SX物品通过");

        // 1.11.2以下不支持在componentBuilder中带有long[]类型的nbt
        if (NMS.compareTo(1, 11, 2) < 0) {
            itemWrapper.remove("test.longArray");
            itemWrapper.save();
        }

        MessageUtil.getInst().builder()
                .add("测试ComponentBuilder: ")
                .show(" 显示1-打开百度 ")
                .openURL("https://www.baidu.com/")
                .add(" 文本2 ")
                .show(Arrays.asList("显示2-执行指令", "显示2-/list"))
                .runCommand("/list")
                .add(item)
                .add(" ")
                .add(item.getType())
                .show("显示4-显示物品默认名\n显示4-喵喵喵")
                .suggestCommand("喵喵喵")
                .send(sender);
        sender.sendMessage("调用MessageUtil通过");
//        itemStack.setAmount(0);

        sender.sendMessage("基本测试完毕");
        // 低版本无法使用JsonParser.parseString
        new JsonParser().parse("{attributes:[{id:\"generic.follow_range\", base:100.0}]}");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return args.length == 2 && args[1].isEmpty() ? Arrays.asList("show", "trans", "clear") : Collections.emptyList();
    }
}
