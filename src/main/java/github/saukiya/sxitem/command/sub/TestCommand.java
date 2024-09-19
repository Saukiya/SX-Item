package github.saukiya.sxitem.command.sub;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.command.SubCommand;
import github.saukiya.sxitem.nbt.NBTItemWrapper;
import github.saukiya.sxitem.nbt.TagCompound;
import github.saukiya.sxitem.util.ComponentUtil;
import github.saukiya.sxitem.util.MessageUtil;
import github.saukiya.sxitem.util.NMS;
import github.saukiya.sxitem.util.NbtUtil;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TestCommand extends SubCommand {
    public TestCommand() {
        super("test", -1);
        setHide();
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        ItemStack itemStack;
        sender.sendMessage("*该指令用于测试插件运行是否正常*");
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
            sender.sendMessage(Arrays.toString(args));
            if (args.length > 1) {
                switch (args[1]) {
                    case "show":
                        MessageUtil.getInst().componentBuilder().add(player.getEquipment().getItemInHand()).send(player);
                        return;
                    case "tran":
                        MessageUtil.getInst().componentBuilder().add(new TranslatableComponent(args.length > 2 ? args[2] : "null")).send(player);
                        return;
                }
            }
            sender.sendMessage("调用此指令前，请保证手中持有任意物品，这个物品在测试结束后会删除");
            itemStack = player.getEquipment().getItemInHand();
            sender.sendMessage("手持物品通过");
        } else {
            itemStack = SXItem.getItemManager().getItem(args.length > 1 ? args[1] : "Default-1", null);
        }
//        NbtUtil.getInst().test(itemStack);

        Object nmsCopyItem = ComponentUtil.getInst().getNMSCopyItem(itemStack);
        Map<String, Object> input = new HashMap<>();
        input.put("minecraft:item_name", "默认名称(无法被铁砧修改)");
        input.put("minecraft:custom_name", "带稀有度颜色的名称(可铁砧修改)§c红色");
        input.put("minecraft:rarity", "epic");
        Object inputComponentMap = ComponentUtil.getInst().valueToMap(input);
        ComponentUtil.getInst().setDataComponentMap(nmsCopyItem, inputComponentMap);
        ComponentUtil.getInst().setBukkitItem(itemStack, nmsCopyItem);

        SXItem.getInst().getLogger().info(
                "mapToJson: " +
                ComponentUtil.getInst().mapToJson(
                        ComponentUtil.getInst().getDataComponentMap(
                                ComponentUtil.getInst().getNMSCopyItem(itemStack)
                        )
                )
        );

        long startTime = System.nanoTime();
        for (String key : SXItem.getItemManager().getItemList()) {
            try {
                SXItem.getItemManager().getItem(key, player);
            } catch (Exception e) {
                sender.sendMessage("物品: " + key + " 有问题");
            }
        }
        sender.sendMessage("获取SX物品通过-耗时: " + (startTime - System.nanoTime()) + " nano");

        NBTItemWrapper itemWrapper = NbtUtil.getInst().getItemTagWrapper(itemStack);
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

        itemWrapper = NbtUtil.getInst().getItemTagWrapper(itemStack);
        TagCompound tagCompound = NbtUtil.getInst().toTag(itemWrapper);
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
        sender.sendMessage("调用NBT物品通过");
        Bukkit.dispatchCommand(sender, "sxi nbt all");

        MessageUtil.send(sender, "[TITLE]测试Title:fadein-20 stay-100 fadeOut-100:20:100:100");
        MessageUtil.send(sender, "[ACTIONBAR]测试ActionBar");

        // 1.11.2以下不支持在componentBuilder中带有long[]类型的nbt
        if (NMS.compareTo(1, 11, 2) < 0) {
            itemWrapper.remove("test.longArray");
            itemWrapper.save();
        }

        MessageUtil.getInst().componentBuilder()
                .add("测试ComponentBuilder: ")
                .show(" 显示1-打开百度 ")
                .openURL("https://www.baidu.com/")
                .add(" 文本2 ")
                .show(Arrays.asList("显示2-执行指令", "显示2-/list"))
                .runCommand("/list")
                .add(itemStack)
                .add(" ")
                .add(itemStack.getType())
                .show("显示4-显示物品默认名\n显示4-喵喵喵")
                .suggestCommand("喵喵喵")
                .send(sender);
        sender.sendMessage("调用MessageUtil通过");
//        itemStack.setAmount(0);

        sender.sendMessage("基本测试完毕");
    }
}
