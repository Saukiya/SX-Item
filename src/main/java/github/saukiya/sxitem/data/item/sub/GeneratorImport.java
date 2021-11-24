package github.saukiya.sxitem.data.item.sub;

import github.saukiya.sxitem.data.item.IGenerator;
import github.saukiya.sxitem.util.MessageUtil;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

/**
 * @author Saukiya
 */
@NoArgsConstructor
public class GeneratorImport implements IGenerator {

    String pathName;

    String key;

    ConfigurationSection config;

    ItemStack item;

    private GeneratorImport(String pathName, String key, ConfigurationSection config) {
        this.pathName = pathName;
        this.key = key;
        this.config = config;
        this.item = config.getItemStack("Item");
    }

    @Override
    public String getType() {
        return "Import";
    }

    @Override
    public IGenerator newGenerator(String pathName, String key, ConfigurationSection config) {
        return new GeneratorImport(pathName, key, config);
    }

    @Override
    public String getPathName() {
        return pathName;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public BaseComponent getNameComponent() {
        return MessageUtil.getInst().showItem(item);
    }

    @Override
    public String getName() {
        return item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : item.getType().name();
    }

    @Override
    public ConfigurationSection getConfig() {
        return config;
    }

    @Override
    public ItemStack getItem(@Nullable Player player) {
        return item.clone();
    }

    @Override
    public ConfigurationSection saveItem(ItemStack saveItem, ConfigurationSection config) {
        config.set("Item", saveItem);
        return config;
    }
}
