package github.saukiya.sxitem.util;

import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import org.bukkit.craftbukkit.v1_21_R1.CraftRegistry;
import org.bukkit.craftbukkit.v1_21_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ComponentUtil_v1_21_R1 extends ComponentUtil {

    private final IRegistryCustom registry = CraftRegistry.getMinecraftRegistry();

    private final DynamicOps<JsonElement> jsonDynamic = registry.a(JsonOps.INSTANCE);
    private final DynamicOps<Object> javaDynamic = registry.a(JavaOps.INSTANCE);

    public void test(Object... args) {
        ItemStack itemStack = (ItemStack) args[0];
        IRegistryCustom registry = CraftRegistry.getMinecraftRegistry();
        DynamicOps<Object> dynamicOps = registry.a(JavaOps.INSTANCE);
        net.minecraft.world.item.ItemStack nmsCopy = CraftItemStack.asNMSCopy(itemStack);
        DataComponentPatch dataComponentPatch = nmsCopy.d();
//        Object encodeResult = DataComponentPatch.b.encode(dataComponentPatch, dynamicOps, dynamicOps.emptyMap()).getOrThrow();

        Map<String, Object> map = new HashMap<>();
        map.put("minecraft:item_name", "默认名称(无法被铁砧修改)");
        map.put("minecraft:custom_name", "带稀有度颜色的名称(可铁砧修改)§c红色");
        map.put("minecraft:rarity", "epic");
//        DataComponentMap dataComponentMap = nmsCopy.a();
//        Object encodeResult = DataComponentMap.b.encode(dataComponentMap, dynamicOps, dynamicOps.emptyMap()).getOrThrow();
//        SXItem.getInst().getLogger().info("dataResult: " + encodeResult);
        DataComponentMap decodeResult = DataComponentMap.b.decode(dynamicOps, map).getOrThrow().getFirst();
//        SXItem.getInst().getLogger().warning("dataResult: " + decodeResult);
        nmsCopy.b(decodeResult);
//        SXItem.getInst().getLogger().info("nmsCopy.a: " + nmsCopy.a());
        itemStack.setItemMeta(CraftItemStack.getItemMeta(nmsCopy));
    }

    @Override
    public DataComponentMap getDataComponentMap(Object nmsCopyItem) {
        return ((net.minecraft.world.item.ItemStack) nmsCopyItem).a();
    }

    @Override
    public DataComponentPatch getDataComponentPatch(Object nmsCopyItem) {
        return ((net.minecraft.world.item.ItemStack) nmsCopyItem).d();
    }

    @Override
    public void setDataComponentMap(Object nmsCopyItem, Object dataComponentMap) {
        ((net.minecraft.world.item.ItemStack) nmsCopyItem).b((DataComponentMap) dataComponentMap);
    }

    @Override
    public void setDataComponentPatch(Object nmsCopyItem, Object dataComponentPatch) {
        ((net.minecraft.world.item.ItemStack) nmsCopyItem).b((DataComponentPatch) dataComponentPatch);
    }

    @Override
    public JsonElement mapToJson(Object dataComponentMap) {
        return DataComponentMap.b.encode((DataComponentMap) dataComponentMap, jsonDynamic, jsonDynamic.emptyMap()).getOrThrow();
    }

    @Override
    public JsonElement patchToJson(Object dataComponentPatch) {
        return DataComponentPatch.b.encode((DataComponentPatch) dataComponentPatch, jsonDynamic, jsonDynamic.emptyMap()).getOrThrow();
    }

    @Override
    public DataComponentMap jsonToMap(JsonElement jsonElement) {
        return DataComponentMap.b.decode(jsonDynamic, jsonElement).getOrThrow().getFirst();
    }

    @Override
    public DataComponentPatch jsonToPatch(JsonElement jsonElement) {
        return DataComponentPatch.b.decode(jsonDynamic, jsonElement).getOrThrow().getFirst();
    }


    @Override
    public Object mapToValue(Object dataComponentMap) {
        return DataComponentMap.b.encode((DataComponentMap) dataComponentMap, javaDynamic, javaDynamic.emptyMap()).getOrThrow();
    }

    @Override
    public Object patchToValue(Object dataComponentPatch) {
        return DataComponentPatch.b.encode((DataComponentPatch) dataComponentPatch, javaDynamic, javaDynamic.emptyMap()).getOrThrow();
    }

    @Override
    public Object valueToMap(Object javaObject) {
        return DataComponentMap.b.decode(javaDynamic, javaObject).getOrThrow().getFirst();
    }

    @Override
    public Object valueToPach(Object javaObject) {
        return DataComponentPatch.b.decode(javaDynamic, javaObject).getOrThrow().getFirst();
    }
}
