package github.saukiya.sxitem.util;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.*;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MessageUtil_v1_8_R3 extends MessageUtil {

    @Override
    public void send(LivingEntity entity, String msg) {
        if (entity instanceof CraftPlayer) {
            CraftPlayer player = (CraftPlayer) entity;
            msg = PlaceholderAPI.setPlaceholders(player, msg);
            if (msg.startsWith("[ACTIONBAR]")) {
                ChatMessageType position = ChatMessageType.ACTION_BAR;
                BaseComponent[] components = new ComponentBuilder(msg.substring(11)).create();
                PacketPlayOutChat packet = new PacketPlayOutChat(null, (byte) position.ordinal());
                packet.components = components;
                player.getHandle().playerConnection.sendPacket(packet);
            } else if (msg.startsWith("[TITLE]")) {
                String[] split = msg.substring(7).split(":");

                PacketPlayOutTitle packetSubtitle;
                packetSubtitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, CraftChatMessage.fromString(split[0])[0], 5, 20, 5);
                player.getHandle().playerConnection.sendPacket(packetSubtitle);

                if (split.length > 1) {
                    packetSubtitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, CraftChatMessage.fromString(split[1])[0], 5, 20, 5);
                    player.getHandle().playerConnection.sendPacket(packetSubtitle);
                }
            } else {
                player.sendMessage(msg);
            }
        }
    }

    @Override
    public TextComponent getTextComponent(String msg, String command, String showText) {
        TextComponent tc = new TextComponent(msg);
        if (showText != null)
            tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7" + showText).create()));
        if (command != null) tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        return tc;
    }

    @Override
    public TranslatableComponent showItem(@Nonnull Material material) {
        TranslatableComponent translatableComponent;
        return null;
//        return new TranslatableComponent((material.isBlock() ? "block" : "item") + "." + material.getNamespace() + "." + material.getKey().getKey());
    }
    @Override
    public BaseComponent showItem(@Nullable ItemStack item) {
        if (item == null) item = new ItemStack(Material.AIR);
        ItemMeta meta = item.getItemMeta();
        BaseComponent bc = meta != null && meta.hasDisplayName() ? new TextComponent(meta.getDisplayName()) : showItem(item.getType());
        NBTTagCompound nbt = CraftItemStack.asNMSCopy(item).getTag();

        return null;
//        bc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new Item(item.getType().getKey().getKey(), item.getAmount(), ItemTag.ofNbt(nbt == null ? null : nbt.toString()))));
//        return bc;
    }

    @Override
    public void send(CommandSender sender, TextComponent tc) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.spigot().sendMessage(tc);
        } else {
            sender.sendMessage(tc.getText());
        }
    }
}
