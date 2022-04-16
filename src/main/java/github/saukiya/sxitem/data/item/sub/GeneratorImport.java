package github.saukiya.sxitem.data.item.sub;

import github.saukiya.sxitem.data.item.IGenerator;
import github.saukiya.sxitem.util.MessageUtil;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;

/**
 * @author Saukiya
 */
@Getter
public class GeneratorImport extends IGenerator {

    ItemStack item;

    public GeneratorImport(String key, ConfigurationSection config) {
        super(key, config);
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

    public static BiConsumer<ItemStack, ConfigurationSection> saveFunc() {
        return (item, config) -> config.set("Item", item);
    }
}
