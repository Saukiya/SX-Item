package github.saukiya.sxitem.util;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public abstract class ComponentBuilder {

    protected TextComponent handle;

    protected BaseComponent current;

    ComponentBuilder() {
        handle = setCurrent(new TextComponent(""));
    }

    public ComponentBuilder add(BaseComponent base) {
        handle.addExtra(setCurrent(base));
        return this;
    }

    public ComponentBuilder add(String text) {
        handle.addExtra(setCurrent(new TextComponent(text)));
        return this;
    }

    public ComponentBuilder add(Material material) {
        handle.addExtra(setCurrent(new TranslatableComponent((material.isBlock() ? "block" : "item") + "." + material.getKey().getNamespace() + "." + material.getKey().getKey())));
        return this;
    }

    public ComponentBuilder add(ItemStack item) {
        if (item == null) item = new ItemStack(Material.AIR);
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            add(meta.getDisplayName());
        } else {
            add(item.getType());
        }
        return show(item);
    }

    public ComponentBuilder show(List<String> list) {
        return show(String.join("\n", list));
    }

    public abstract ComponentBuilder show(String text);

    public abstract ComponentBuilder show(ItemStack item);

    public ComponentBuilder openURL(String value) {
        return click(ClickEvent.Action.OPEN_URL, value);
    }

    public ComponentBuilder runCommand(String value) {
        return click(ClickEvent.Action.RUN_COMMAND, value);
    }

    public ComponentBuilder suggestCommand(String value) {
        return click(ClickEvent.Action.SUGGEST_COMMAND, value);
    }

    protected ComponentBuilder click(ClickEvent.Action action, String value) {
        current.setClickEvent(new ClickEvent(action, value));
        return this;
    }

    public void send(CommandSender sender) {
        sender.spigot().sendMessage(handle);
    }

    public TextComponent getHandle() {
        return handle;
    }

    protected <V extends BaseComponent> V setCurrent(V baseComponent) {
        return (V) (current = baseComponent);
    }
}
