package github.saukiya.sxitem.util;

import github.saukiya.sxitem.nbt.NBTTagWrapper;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.inventory.ItemStack;

public class MessageUtil_v1_13_R2 extends MessageUtil {

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
            NBTTagWrapper wrapper = NbtUtil.getInst().createTagWrapper();
            wrapper.set("id", item.getType().getKey().getKey());
            wrapper.set("Count", (byte) item.getAmount());
            wrapper.set("tag", NbtUtil.getInst().getItemNBT(item));
            current.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[]{new TextComponent(wrapper.nbtToString())}));
            return this;
        }
    }
}
