package github.saukiya.sxitem.util;

import github.saukiya.util.nms.MessageUtil;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * @see github.saukiya.util.nms.MessageUtil.Builder
 * @deprecated
 */
@AllArgsConstructor
public class ComponentBuilder {

    MessageUtil.Builder target;

    @Deprecated
    public ComponentBuilder add(BaseComponent base) {
        target.add(base);
        return this;
    }

    @Deprecated
    public ComponentBuilder add(String text) {
        target.add(text);
        return this;
    }

    @Deprecated
    public ComponentBuilder add(Material material) {
        target.add(material);
        return this;
    }

    @Deprecated
    public ComponentBuilder add(ItemStack item) {
        target.add(item);
        return this;
    }

    @Deprecated
    public ComponentBuilder show(List<String> list) {
        target.show(list);
        return this;
    }

    @Deprecated
    public ComponentBuilder show(String text) {
        target.show(text);
        return this;
    }

    @Deprecated
    public ComponentBuilder show(ItemStack item) {
        target.show(item);
        return this;
    }

    @Deprecated
    public ComponentBuilder openURL(String value) {
        target.openURL(value);
        return this;
    }

    @Deprecated
    public ComponentBuilder runCommand(String value) {
        target.runCommand(value);
        return this;
    }

    @Deprecated
    public ComponentBuilder suggestCommand(String value) {
        target.suggestCommand(value);
        return this;
    }

    @Deprecated
    public void send(CommandSender sender) {
        target.send(sender);
    }
}
