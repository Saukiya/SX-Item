package github.saukiya.sxitem.data.item;

import github.saukiya.sxitem.nms.NBTTagWrapper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * 用于表示这个 IGenerator 适合更新
 *
 * @author Saukiya
 */
public interface IUpdate {

    ItemStack update(ItemStack oldItem, NBTTagWrapper oldWrapper, Player player);

    int updateCode();

    boolean isUpdate();
}
