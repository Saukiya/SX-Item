package github.saukiya.sxitem.data.item.impl;

import github.saukiya.sxitem.data.item.IGenerator;
import github.saukiya.tools.util.XMaterial;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * 无配置物品生成器，将指令中的材质参数交给 XMaterial 及运行时材质回退层解析。
 * 类名和类型名继续保留 {@code ReMaterial}，以兼容依赖该生成器类型的现有扩展。
 */
public class GeneratorReMaterial extends IGenerator {

    /**
     * 创建共享生成器；该生成器不持有具体物品配置，材质由每次调用参数决定。
     */
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
            return XMaterial.getItem((String) args[0]);
        }
        return null;
    }
}
