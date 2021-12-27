package github.saukiya.sxitem.util;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class MessageUtil_v1_15_R1 extends MessageUtil {

    @Override
    public ComponentBuilder componentBuilder() {
        return new ComponentBuilderImpl();
    }

    class ComponentBuilderImpl extends ComponentBuilder {

        @Override
        public ComponentBuilder show(String text) {
            current.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7" + text)}));
            return this;
        }

        @Override
        public ComponentBuilder show(ItemStack item) {
            current.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[]{new TextComponent(CraftItemStack.asNMSCopy(item).save(new NBTTagCompound()).toString())}));
            return this;
        }
    }
}
