package github.saukiya.sxitem.util;

import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.craftbukkit.v1_21_R1.CraftRegistry;
import org.bukkit.craftbukkit.v1_21_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class ComponentUtil_v1_21_R1 extends ComponentUtil {

    private final IRegistryCustom registry = CraftRegistry.getMinecraftRegistry();

    private final DynamicOps<JsonElement> jsonDynamic = registry.a(JsonOps.INSTANCE);
    private final DynamicOps<Object> javaDynamic = registry.a(JavaOps.INSTANCE);
    private final DynamicOps<NBTBase> nbtDynamic = registry.a(DynamicOpsNBT.a);

    @Override
    public net.minecraft.world.item.ItemStack getNMSCopyItem(ItemStack itemStack) {
        return CraftItemStack.asNMSCopy(itemStack);
    }

    @Override
    public void setBukkitItem(ItemStack itemStack, Object nmsCopyItem) {
        itemStack.setItemMeta(CraftItemStack.getItemMeta((net.minecraft.world.item.ItemStack) nmsCopyItem));
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
    public NBTBase mapToNBT(Object dataComponentMap) {
        return DataComponentMap.b.encode((DataComponentMap) dataComponentMap, nbtDynamic, nbtDynamic.emptyMap()).getOrThrow();
    }

    @Override
    public NBTBase patchToNBT(Object dataComponentPatch) {
        return DataComponentPatch.b.encode((DataComponentPatch) dataComponentPatch, nbtDynamic, nbtDynamic.emptyMap()).getOrThrow();
    }

    @Override
    public DataComponentMap nbtToMap(Object nbtTagCompound) {
        return DataComponentMap.b.decode(nbtDynamic, (NBTTagCompound) nbtTagCompound).getOrThrow().getFirst();
    }

    @Override
    public DataComponentPatch nbtToPatch(Object nbtTagCompound) {
        return DataComponentPatch.b.decode(nbtDynamic, (NBTTagCompound) nbtTagCompound).getOrThrow().getFirst();
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
