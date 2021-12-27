package github.saukiya.sxitem.util;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.minecraft.server.v1_11_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class MessageUtil_v1_11_R1 extends MessageUtil {

    @Override
    public ComponentBuilder componentBuilder() {
        return new ComponentBuilderImpl();
    }

    class ComponentBuilderImpl extends ComponentBuilder {

        @Override
        public ComponentBuilder add(Material material) {
            handle.addExtra(setCurrent(new TranslatableComponent((material.isBlock() ? "block" : "item") + ".minecraft." + material.name().toLowerCase(Locale.ROOT))));
            return this;
        }

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

        @Override
        public void send(CommandSender sender) {
            if (sender instanceof Player) {
                ((Player) sender).spigot().sendMessage(handle);
            } else {
                sender.sendMessage(handle.toLegacyText());
            }
        }
    }
}
