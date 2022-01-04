package github.saukiya.sxitem.util;

import github.saukiya.sxitem.nbt.NBTTagWrapper;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.minecraft.server.v1_12_R1.Item;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class MessageUtil_v1_12_R1 extends MessageUtil {

    @Override
    public ComponentBuilder componentBuilder() {
        return new ComponentBuilderImpl();
    }

    class ComponentBuilderImpl extends ComponentBuilder {

        @Override
        public ComponentBuilder add(Material material) {
            Item item = CraftMagicNumbers.getItem(material);
            add(new TranslatableComponent((item.k() ? new net.minecraft.server.v1_12_R1.ItemStack(item).a() : item.getName()) + ".name"));
            return this;
        }

        @Override
        public ComponentBuilder show(String text) {
            current.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7" + text)}));
            return this;
        }

        @Override
        public ComponentBuilder show(ItemStack item) {
            NBTTagWrapper wrapper = NbtUtil.getInst().createTagWrapper();
            wrapper.set("id", "minecraft:" + item.getType().name().toLowerCase(Locale.ROOT));
            wrapper.set("Count", (byte) item.getAmount());
            wrapper.set("Damage", item.getDurability());
            wrapper.set("tag", NbtUtil.getInst().getItemNBT(item));
            current.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[]{new TextComponent(wrapper.nbtToString())}));
            return this;
        }
    }
}
