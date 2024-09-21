package github.saukiya.util.nms;

import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;
import org.bukkit.craftbukkit.v1_21_R1.CraftRegistry;

public class ComponentUtil_v1_21_R1 extends ComponentUtil {

    private final IRegistryCustom registry = CraftRegistry.getMinecraftRegistry();

    private final DynamicOps<JsonElement> jsonDynamic = registry.a(JsonOps.INSTANCE);
    private final DynamicOps<Object> javaDynamic = registry.a(JavaOps.INSTANCE);

    @Override
    public DataComponentMap getDataComponentMap(Object nmsItem) {
        return ((net.minecraft.world.item.ItemStack) nmsItem).a();
    }

    @Override
    public DataComponentPatch getDataComponentPatch(Object nmsItem) {
        return ((net.minecraft.world.item.ItemStack) nmsItem).d();
    }

    @Override
    public void setDataComponentMap(Object nmsItem, Object dataComponentMap) {
        ((net.minecraft.world.item.ItemStack) nmsItem).b((DataComponentMap) dataComponentMap);
    }

    @Override
    public void setDataComponentPatch(Object nmsItem, Object dataComponentPatch) {
        ((net.minecraft.world.item.ItemStack) nmsItem).b((DataComponentPatch) dataComponentPatch);
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
    public Object valueToPatch(Object javaObject) {
        return DataComponentPatch.b.decode(javaDynamic, javaObject).getOrThrow().getFirst();
    }

    @Override
    public void setComponentMapValue(Object dataComponentMap, String type, Object value) {
        DataComponentType dataComponentType = BuiltInRegistries.aq.a(MinecraftKey.c(type));
        PatchedDataComponentMap map = (PatchedDataComponentMap) dataComponentMap;
        map.b(dataComponentType, value);
    }
}
