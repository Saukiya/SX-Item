package github.saukiya.sxitem.util;

import com.google.gson.JsonElement;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import org.bukkit.craftbukkit.v1_21_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MessageUtil_v1_21_R1 extends MessageUtil {

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
            net.minecraft.world.item.ItemStack nmsCopy = CraftItemStack.asNMSCopy(item);
            Map<String, Object> map = new HashMap<>();
            map.put("minecraft:enchantments", Map.of("minecraft:sharpness", 1));
            map.put("minecraft:lore", Arrays.asList("\"12345\"", "\"67890\""));
            current.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new Item(item.getType().getKey().getKey(), item.getAmount(), toMap(nmsCopy.d()))));
            return this;
        }

        public Map<String, Object> toMap(DataComponentPatch patch) {
            Map<String, Object> map = new HashMap<>();
            for (Map.Entry<DataComponentType<?>, Optional<?>> entry : patch.b()) {
                if (entry.getValue().isEmpty()) continue;
                DataComponentType<?> key = entry.getKey();
                Object value;
                if (DataComponents.b.equals(key)) {
                    value = ((CustomData) entry.getValue().get()).d().toString();
                    map.put(key.toString(), value);
                } else {
                    continue;
                }
                System.out.println(key + "\t" + value);
            }
            return map;
        }
    }
}
