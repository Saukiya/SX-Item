package github.saukiya.sxitem.data.item.impl;

import github.saukiya.sxitem.data.item.IGenerator;
import github.saukiya.tools.util.ReMaterial;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * 基础物品生成器(数字ID/英文ID)
 */
public class GeneratorReMaterial extends IGenerator {

    public GeneratorReMaterial() {
        super("ReMaterial", new MemoryConfiguration(), "ReMaterial");
    }

    @Override
    public String getType() {
        return "ReMaterial";
    }

    @Override
    public String getName() {
        return "ReMaterial";
    }

    @Override
    protected ItemStack getItem(Player player, Object... args) {
        if (args.length > 0 && args[0] instanceof String) {
            return ReMaterial.getItem((String) args[0]);
        }
        return null;
    }
}
