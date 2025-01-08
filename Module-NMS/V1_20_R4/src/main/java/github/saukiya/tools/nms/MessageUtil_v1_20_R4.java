package github.saukiya.tools.nms;

import com.google.gson.JsonElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.inventory.ItemStack;

public class MessageUtil_v1_20_R4 extends MessageUtil {

    @Override
    public Builder builder() {
        return new BuilderImpl();
    }

    static class BuilderImpl extends Builder {

        @Override
        public Builder show(String text) {
            current.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7" + text)));
            return this;
        }

        @Override
        public Builder show(ItemStack item) {
            Object nmsCopy = NbtUtil.getInst().getNMSItem(item);
            Object component = ComponentUtil.getInst().getDataComponentMap(nmsCopy);
            JsonElement data = ComponentUtil.getInst().mapToJson(component);
            current.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new Item(item.getType().getKey().getKey(), item.getAmount(), data)));
            return this;
        }

        @ToString
        @Getter
        @AllArgsConstructor
        class Item extends Content {

            public String id;
            public int count;
            public Object components;

            @Override
            public HoverEvent.Action requiredAction() {
                return HoverEvent.Action.SHOW_ITEM;
            }
        }
    }
}
