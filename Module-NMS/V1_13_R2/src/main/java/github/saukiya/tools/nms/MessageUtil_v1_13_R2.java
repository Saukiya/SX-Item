package github.saukiya.tools.nms;

import lombok.val;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.inventory.ItemStack;

public class MessageUtil_v1_13_R2 extends MessageUtil {

    @Override
    public Builder builder() {
        return new BuilderImpl();
    }

    static class BuilderImpl extends Builder {

        @Override
        public Builder show(String text) {
            current.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7" + text)}));
            return this;
        }

        @Override
        public Builder show(ItemStack item) {
            val wrapper = NbtUtil.getInst().createTagWrapper();
            wrapper.set("id", item.getType().getKey().getKey());
            wrapper.set("Count", (byte) item.getAmount());
            wrapper.set("tag", NbtUtil.getInst().getItemNBT(item));
            current.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[]{new TextComponent(wrapper.nbtToString())}));
            return this;
        }
    }
}
