package github.saukiya.sxitem.data.item;

import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
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

    public IGenerator(String key, ConfigurationSection config) {
        this.key = key;
        this.config = config;
        YamlConfiguration yaml = new YamlConfiguration();
        config.getValues(false).forEach(yaml::set);
        this.configString = (configString = yaml.saveToString()).substring(0, configString.length() - 1);
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
     * @return
     */
    public abstract BaseComponent getNameComponent();

    /**
     * 获取物品
     *
     * @param player Player
     * @return Item
     */
    protected abstract ItemStack getItem(Player player, Object... args);
}
