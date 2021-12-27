package github.saukiya.sxitem.util;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ItemTag;
import net.md_5.bungee.api.chat.hover.content.Item;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class MessageUtil_v1_18_R1 extends MessageUtil {

    @Override
    public ComponentBuilder componentBuilder() {
        return new ComponentBuilderImpl();
    }

    class ComponentBuilderImpl extends ComponentBuilder {

        @Override
        public ComponentBuilder show(String text) {
            current.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7" + text)));
            return this;
        }

        @Override
        public ComponentBuilder show(ItemStack item) {
            current.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new Item(item.getType().getKey().getKey(), item.getAmount(), ItemTag.ofNbt(String.valueOf(CraftItemStack.asNMSCopy(item).s())))));
            return this;
        }
    }
}
