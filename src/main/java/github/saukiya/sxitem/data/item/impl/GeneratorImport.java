package github.saukiya.sxitem.data.item.impl;

import github.saukiya.sxitem.data.item.IGenerator;
import github.saukiya.tools.nms.MessageUtil;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
public class GeneratorImport extends IGenerator {

    ItemStack item;

    public GeneratorImport(String key, ConfigurationSection config, String group) {
        super(key, config, group);
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
        MessageUtil.Builder cb = MessageUtil.getInst().builder();
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            cb.add(item.getItemMeta().getDisplayName());
        } else {
            cb.add(item.getType());
        }
        return cb.getHandle();
    }

    @Override
    protected ItemStack getItem(Player player, Object... args) {
        return item.clone();
    }

    public static void save(ItemStack item, ConfigurationSection config) {
        config.set("Item", item);
    }
}
