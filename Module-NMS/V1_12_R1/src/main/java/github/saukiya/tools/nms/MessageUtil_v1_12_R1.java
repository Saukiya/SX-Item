package github.saukiya.tools.nms;

import lombok.val;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.minecraft.server.v1_12_R1.Item;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers;
import org.bukkit.inventory.ItemStack;

public class MessageUtil_v1_12_R1 extends MessageUtil {

    @Override
    public Builder builder() {
        return new BuilderImpl();
    }

    static class BuilderImpl extends Builder {

        @Override
        public Builder add(Material material) {
            Item item = CraftMagicNumbers.getItem(material);
            add(new TranslatableComponent((item.k() ? new net.minecraft.server.v1_12_R1.ItemStack(item).a() : item.getName()) + ".name"));
            return this;
        }

        @Override
        public Builder show(String text) {
            current.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7" + text)}));
            return this;
        }

        @Override
        public Builder show(ItemStack item) {
//            val wrapper = NbtUtil.getInst().createTagWrapper();
//            wrapper.set("id", "minecraft:" + item.getType().name().toLowerCase(Locale.ROOT)); //  // craftItem.a()
//            wrapper.set("Count", (byte) item.getAmount());
//            wrapper.set("Damage", item.getDurability());
//            wrapper.set("tag", NbtUtil.getInst().getItemNBT(item));
            val nbtCompound = new NBTTagCompound();
            CraftItemStack.asNMSCopy(item).save(nbtCompound);
            current.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[]{new TextComponent(nbtCompound.toString())}));
            return this;
        }
    }
}
