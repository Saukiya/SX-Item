package github.saukiya.sxitem.util;

import net.md_5.bungee.api.chat.*;
import net.minecraft.server.v1_11_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;

public class MessageUtil_v1_11_R1 extends MessageUtil {

    @Override
    public void send(CommandSender sender, TextComponent tc) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.spigot().sendMessage(tc);
        } else {
            sender.sendMessage(tc.getText());
        }
    }

    @Override
    public TextComponent getTextComponent(String msg, String command, String showText) {
        TextComponent tc = new TextComponent(msg);
        if (showText != null)
            tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7" + showText)}));
        if (command != null) tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        return tc;
    }

    @Override
    public TranslatableComponent showItem(@Nonnull Material material) {
        return new TranslatableComponent((material.isBlock() ? "block" : "item") + ".minecraft." + material.name().toLowerCase(Locale.ROOT));
    }

    @Override
    public BaseComponent showItem(@Nullable ItemStack item) {
        if (item == null) item = new ItemStack(Material.AIR);
        ItemMeta meta = item.getItemMeta();
        BaseComponent bc = meta != null && meta.hasDisplayName() ? new TextComponent(meta.getDisplayName()) : showItem(item.getType());
        NBTTagCompound nbt = CraftItemStack.asNMSCopy(item).save(new NBTTagCompound());
        bc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[]{new TextComponent(nbt.toString())}));
        return bc;
    }
}
