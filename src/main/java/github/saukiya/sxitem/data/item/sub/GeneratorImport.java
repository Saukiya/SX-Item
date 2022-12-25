package github.saukiya.sxitem.data.item.sub;

import github.saukiya.sxitem.data.item.IGenerator;
import github.saukiya.sxitem.util.MessageUtil;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.BiConsumer;

/**
 * @author Saukiya
 */
@Getter
public class GeneratorImport extends IGenerator {

    ItemStack item;

    public GeneratorImport(String key, ConfigurationSection config, JavaPlugin plugin) {
        super(key, config, plugin);
        this.item = config.getItemStack("Item");
    }

    @Override
    public String getType() {
        return "Import";
    }

    @Override
    public String getName() {
        return item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : item.getType().name();
    }

    @Override
    public BaseComponent getNameComponent() {
        return MessageUtil.getInst().componentBuilder().add(item).getHandle();
    }

    @Override
    protected ItemStack getItem(Player player, Object... args) {
        return item.clone();
    }

    public static Saver saveFunc() {
        return (item, config) -> config.set("Item", item);
    }
}
