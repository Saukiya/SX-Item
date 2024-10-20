package github.saukiya.sxitem.data.item;

import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Saukiya TODO 添加RandomGenerator
 */
@Getter
public abstract class IGenerator {

    protected String key;

    protected ConfigurationSection config;

    protected String configString;

    protected String group;

    public IGenerator(String key, ConfigurationSection config, String group) {
        this.key = key;
        this.config = config;
        YamlConfiguration yaml = new YamlConfiguration();
        config.getValues(false).forEach(yaml::set);
        this.configString = yaml.saveToString().trim();
        this.group = group;
    }

    /**
     * 返回生成器类型
     *
     * @return Type
     */
    public abstract String getType();

    /**
     * 返回物品展示名
     *
     * @return Name
     */
    public abstract String getName();

    /**
     * 返回展示组件
     *
     * @return BaseComponent
     */
    public BaseComponent getNameComponent() {
        return new TextComponent(getName());
    }

    /**
     * 获取物品
     *
     * @param player Player
     * @return Item
     */
    protected abstract ItemStack getItem(Player player, Object... args);

    public interface Loader {
        IGenerator apply(String key, ConfigurationSection config, String group);
    }

    public interface Saver {
        void apply(ItemStack item, ConfigurationSection config);
    }
}
