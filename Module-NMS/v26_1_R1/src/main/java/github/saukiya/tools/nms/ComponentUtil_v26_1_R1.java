package github.saukiya.tools.nms;

import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import org.bukkit.craftbukkit.CraftRegistry;

import java.util.List;

/**
 * 组件工具实现 [26.1.x] - Mojang映射(反混淆)
 */
@SuppressWarnings({"rawtypes", "unchecked", "unused"})
public class ComponentUtil_v26_1_R1 extends ComponentUtil {

    private final RegistryAccess registry = CraftRegistry.getMinecraftRegistry();

    private final DynamicOps<JsonElement> jsonDynamic = registry.createSerializationContext(JsonOps.INSTANCE);
    private final DynamicOps<Object> javaDynamic = registry.createSerializationContext(JavaOps.INSTANCE);

    @Override
    public DataComponentMap getDataComponentMap(Object nmsItem) {
        return ((net.minecraft.world.item.ItemStack) nmsItem).getComponents();
    }

    @Override
    public DataComponentPatch getDataComponentPatch(Object nmsItem) {
        return ((net.minecraft.world.item.ItemStack) nmsItem).getComponentsPatch();
    }

    @Override
    public void setDataComponentMap(Object nmsItem, Object dataComponentMap) {
        ((net.minecraft.world.item.ItemStack) nmsItem).applyComponents((DataComponentMap) dataComponentMap);
    }

    @Override
    public void setDataComponentPatch(Object nmsItem, Object dataComponentPatch) {
        ((net.minecraft.world.item.ItemStack) nmsItem).applyComponents((DataComponentPatch) dataComponentPatch);
    }

    @Override
    public JsonElement mapToJson(Object dataComponentMap) {
        return DataComponentMap.CODEC.encode((DataComponentMap) dataComponentMap, jsonDynamic, jsonDynamic.emptyMap()).getOrThrow();
    }

    @Override
    public JsonElement patchToJson(Object dataComponentPatch) {
        return DataComponentPatch.CODEC.encode((DataComponentPatch) dataComponentPatch, jsonDynamic, jsonDynamic.emptyMap()).getOrThrow();
    }

    @Override
    public DataComponentMap jsonToMap(JsonElement jsonElement) {
        return DataComponentMap.CODEC.decode(jsonDynamic, jsonElement).getOrThrow().getFirst();
    }

    @Override
    public DataComponentPatch jsonToPatch(JsonElement jsonElement) {
        return DataComponentPatch.CODEC.decode(jsonDynamic, jsonElement).getOrThrow().getFirst();
    }

    @Override
    public Object mapToValue(Object dataComponentMap) {
        return DataComponentMap.CODEC.encode((DataComponentMap) dataComponentMap, javaDynamic, javaDynamic.emptyMap()).getOrThrow();
    }

    @Override
    public Object patchToValue(Object dataComponentPatch) {
        return DataComponentPatch.CODEC.encode((DataComponentPatch) dataComponentPatch, javaDynamic, javaDynamic.emptyMap()).getOrThrow();
    }

    @Override
    public Object valueToMap(Object javaObject) {
        return DataComponentMap.CODEC.decode(javaDynamic, javaObject).getOrThrow().getFirst();
    }

    @Override
    public Object valueToPatch(Object javaObject) {
        return DataComponentPatch.CODEC.decode(javaDynamic, javaObject).getOrThrow().getFirst();
    }

    @Override
    public void setComponentMapValue(Object dataComponentMap, String type, Object value) {
        DataComponentType dataComponentType = BuiltInRegistries.DATA_COMPONENT_TYPE.getValue(Identifier.parse(type));
        ((PatchedDataComponentMap) dataComponentMap).set(dataComponentType, value);
    }

    @Override
    public Object getComponentMapValue(Object dataComponentMap, String type) {
        DataComponentType dataComponentType = BuiltInRegistries.DATA_COMPONENT_TYPE.getValue(Identifier.parse(type));
        return ((PatchedDataComponentMap) dataComponentMap).get(dataComponentType);
    }

    @Override
    public List<String> getItemKeys() {
        return BuiltInRegistries.DATA_COMPONENT_TYPE.keySet().stream().map(Identifier::toString).toList();
    }
}
