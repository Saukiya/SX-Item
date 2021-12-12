package github.saukiya.sxitem.util;

import net.md_5.bungee.api.chat.*;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MessageUtil_v1_14_R1 extends MessageUtil {

    @Override
    public TextComponent getTextComponent(String msg, String showText, String command) {
        TextComponent tc = new TextComponent(msg);
        if (showText != null)
            tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7" + showText)}));
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
        NBTTagCompound nbt = CraftItemStack.asNMSCopy(item).save(new NBTTagCompound());
        bc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[]{new TextComponent(nbt.toString())}));
        return bc;
    }
}
