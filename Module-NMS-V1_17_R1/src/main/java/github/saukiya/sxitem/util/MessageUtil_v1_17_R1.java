package github.saukiya.sxitem.util;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Item;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MessageUtil_v1_17_R1 extends MessageUtil {

    @Override
    public void send(LivingEntity entity, String msg) {
        if (entity instanceof Player) {
            Player player = (Player) entity;
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

    @Override
    public TextComponent getTextComponent(String msg, String command, String showText) {
        TextComponent tc = new TextComponent(msg);
        if (showText != null)
            tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7" + showText)));
        if (command != null) tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        return tc;
    }

    @Override
    public TranslatableComponent showItem(@Nonnull Material material) {
        return new TranslatableComponent((material.isBlock() ? "block" : "item") + "." + material.getKey().getNamespace() + "." + material.getKey().getKey());
    }

    @Override
    public BaseComponent showItem(@Nullable ItemStack item) {
        if (item == null) item = new ItemStack(Material.AIR);
        ItemMeta meta = item.getItemMeta();
        BaseComponent bc = meta != null && meta.hasDisplayName() ? new TextComponent(meta.getDisplayName()) : showItem(item.getType());
        NBTTagCompound nbt = CraftItemStack.asNMSCopy(item).getTag();
        bc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new Item(item.getType().getKey().getKey(), item.getAmount(), ItemTag.ofNbt(nbt == null ? null : nbt.toString()))));
        return bc;
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
