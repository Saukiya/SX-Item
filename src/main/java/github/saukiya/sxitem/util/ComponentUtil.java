package github.saukiya.sxitem.util;

import com.google.gson.JsonElement;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

public abstract class ComponentUtil implements NMS {

    @Getter
    private final static ComponentUtil inst = NMS.getInst(ComponentUtil.class, "v1_21_R1", "v1_8_R3");

    public final Object getNMSItem(ItemStack itemStack) {
        return NbtUtil.getInst().getNMSItem(itemStack);
    }

    public final void setNMSItem(ItemStack itemStack, Object nmsCopyItem) {
        NbtUtil.getInst().setNMSItem(itemStack, nmsCopyItem);
    }

    public abstract Object getDataComponentMap(Object nmsCopyItem);
    public abstract void setDataComponentMap(Object nmsCopyItem, Object dataComponentMap);

    public abstract JsonElement mapToJson(Object dataComponentMap);
    public abstract Object jsonToMap(JsonElement jsonElement);

    public abstract Object mapToValue(Object dataComponentMap);
    public abstract Object valueToMap(Object javaObject);

    public abstract Object getDataComponentPatch(Object nmsCopyItem);
    public abstract void setDataComponentPatch(Object nmsCopyItem, Object dataComponentPatch);

    public abstract JsonElement patchToJson(Object dataComponentPatch);
    public abstract Object jsonToPatch(JsonElement jsonElement);

    public abstract Object patchToValue(Object dataComponentPatch);
    public abstract Object valueToPach(Object javaObject);
}
