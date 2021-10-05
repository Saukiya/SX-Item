package github.saukiya.sxitem.util;

import github.saukiya.sxitem.SXItem;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Item;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * @author Saukiya
 */

public enum Message {
    ELEVATOR__UP,
    ELEVATOR__DOWN,

    ADMIN__NO_ITEM,
    ADMIN__GIVE_ITEM,
    ADMIN__HAS_ITEM,
    ADMIN__SAVE_ITEM,
    ADMIN__SAVE_NO_TYPE,
    ADMIN__SAVE_ITEM_ERROR,
    ADMIN__GIVE_KIT,
    ADMIN__NO_PERMISSION_CMD,
    ADMIN__NO_CMD,
    ADMIN__NO_FORMAT,
    ADMIN__NO_ONLINE,
    ADMIN__NO_CONSOLE,
    ADMIN__PLUGIN_RELOAD,

    COMMAND__GIVE,
    COMMAND__SAVE,
    COMMAND__NBT,
    COMMAND__KIT,
    COMMAND__CHAT,
    COMMAND__CMD,
    COMMAND__BLOCK,
    COMMAND__CHEST,
    COMMAND__ENTITY,
    COMMAND__ATTACK,
    COMMAND__PLUGINS,
    COMMAND__RELOAD;

    private static final Map<Material, String> itemRam = new HashMap<>();
    @Getter
    private static YamlConfiguration messages;

    /**
     * 加载Message类
     */
    public static void loadMessage() {
        File file = new File(SXItem.getInst().getDataFolder(), "Message.yml");
        if (!file.exists()) {
            SXItem.getInst().getLogger().info("create Message.yml");
            SXItem.getInst().saveResource("Message.yml", true);
        }
        messages = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * 获取String
     *
     * @param loc  Message
     * @param args Object...
     * @return String
     */
    public static String getMsg(Message loc, Object... args) {
        return ChatColor.translateAlternateColorCodes('&', MessageFormat.format(messages.getString(loc.toString(), "Null Message: " + loc), args));
    }

    /**
     * 获取List
     *
     * @param loc  Message
     * @param args Object...
     * @return List
     */
    public static List<String> getStringList(Message loc, Object... args) {
        List<String> list = messages.getStringList(loc.toString());
        if (list.size() == 0) return Collections.singletonList("Null Message: " + loc);
        IntStream.range(0, list.size()).forEach(i -> list.set(i, MessageFormat.format(list.get(i), args).replace("&", "§")));
        return list;
    }

    /**
     * 发送消息给玩家 - 支持PlaceholderAPI
     *
     * @param entity LivingEntity
     * @param loc    Message
     * @param args   Object...
     */
    public static void send(LivingEntity entity, Message loc, Object... args) {
        send(entity, getMsg(loc, args));
    }

    public static void send(LivingEntity entity, String msg) {
        if (entity instanceof Player player) {
            msg = PlaceholderAPI.setPlaceholders(player, msg);
            if (msg.startsWith("[ACTIONBAR]")) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(msg.substring(11)));
            } else if (msg.startsWith("[TITLE]")) {
                String[] split = msg.substring(7).split(":");
                player.sendTitle(split[0], split.length > 1 ? split[1] : null, 5, 20, 5);
            } else {
                player.sendMessage(msg);
            }
        }
    }


    public static TextComponent getTextComponent(String msg, String command, String showText) {
        TextComponent tc = new TextComponent(msg);
        if (showText != null)
            tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7" + showText)));
        if (command != null) tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        return tc;
    }

    public static TranslatableComponent showItem(@Nonnull Material material) {
        return new TranslatableComponent((material.isBlock() ? "block" : "item") + "." + material.getKey().getNamespace() + "." + material.getKey().getKey());
    }

    public static BaseComponent showItem(@Nullable ItemStack item) {
        if (item == null) item = new ItemStack(Material.AIR);
        ItemMeta meta = item.getItemMeta();
        BaseComponent bc = meta != null && meta.hasDisplayName() ? new TextComponent(meta.getDisplayName()) : showItem(item.getType());
        NBTTagCompound nbt = CraftItemStack.asNMSCopy(item).getTag();
        bc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new Item(item.getType().getKey().getKey(), item.getAmount(), ItemTag.ofNbt(nbt == null ? null : nbt.toString()))));
        return bc;
    }

    public static void send(CommandSender sender, TextComponent tc) {
        if (sender instanceof Player player) {
            player.spigot().sendMessage(tc);
        } else {
            sender.sendMessage(tc.getText());
        }
    }

    @Override
    public String toString() {
        return name().replace("__", ".");
    }
}