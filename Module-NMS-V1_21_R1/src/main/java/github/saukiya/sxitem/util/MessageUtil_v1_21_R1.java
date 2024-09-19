package github.saukiya.sxitem.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.JsonOps;
import github.saukiya.sxitem.SXItem;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_21_R1.CraftRegistry;
import org.bukkit.craftbukkit.v1_21_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MessageUtil_v1_21_R1 extends MessageUtil {

    @Override
    public ComponentBuilder componentBuilder() {
        return new ComponentBuilderImpl();
    }

    class ComponentBuilderImpl extends ComponentBuilder {

        @Override
        public ComponentBuilder show(String text) {
            current.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7" + text)));
            return this;
        }

        @Override
        public ComponentBuilder show(ItemStack item) {
            net.minecraft.world.item.ItemStack nmsCopy = CraftItemStack.asNMSCopy(item);
            DynamicOps<JsonElement> dynamicOps = CraftRegistry.getMinecraftRegistry().a(JsonOps.INSTANCE);
            JsonObject data = (JsonObject) net.minecraft.world.item.ItemStack.b.encode(nmsCopy, dynamicOps, dynamicOps.emptyMap()).getOrThrow();
            current.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new Item(item.getType().getKey().getKey(), item.getAmount(), data.get("components"))));
            return this;
        }

    @ToString
    @Getter
    @AllArgsConstructor
    class Item extends Content {

        public String id;
        public int count;
        public Object components;

        @Override
        public HoverEvent.Action requiredAction() {
            return HoverEvent.Action.SHOW_ITEM;
        }
    }

    class DeprecatedFunction {

        @Deprecated
        public Map<String, Object> toMap(DataComponentPatch patch) {
            Map<String, Object> map = new HashMap<>();
            for (Map.Entry<DataComponentType<?>, Optional<?>> patchEntry : patch.b()) {
                if (patchEntry.getValue().isEmpty()) continue;
                DataComponentType<?> key = patchEntry.getKey();
                Object value = patchEntry.getValue().get();
                Object data = null;
                switch (key.toString()) {
                    case "minecraft:damage":
                    case "minecraft:max_damage":
                    case "minecraft:max_stack_size":
                    case "minecraft:repair_cost":
                    case "minecraft:ominous_bottle_amplifier":
                        data = value;
                        break;
                    case "minecraft:rarity":
                        data = value.toString().toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");
                        break;
                    case "minecraft:custom_name":
                    case "minecraft:item_name":
                        IChatBaseComponent custom_name = (IChatBaseComponent) value;
                        data = IChatBaseComponent.ChatSerializer.a(custom_name, CraftRegistry.getMinecraftRegistry());
                        break;
                    case "minecraft:lore":
                        ItemLore lore = (ItemLore) value;
                        data = lore.a().stream().map(line -> IChatBaseComponent.ChatSerializer.a(line, CraftRegistry.getMinecraftRegistry())).toArray();
                        break;
                    case "minecraft:attribute_modifiers":
                        ItemAttributeModifiers attribute_modifiers = (ItemAttributeModifiers) value;
                        data = attribute_modifiers.b().stream().filter(attribute -> attribute.b().c() != 0).map(attribute -> {
                            Map<String, Object> attributeMap = new HashMap<>();
                            attributeMap.put("type", attribute.a().e().get().a().a());
                            attributeMap.put("slot", attribute.c().c());
                            attributeMap.put("id", attribute.a().e().get().b().toString());
                            attributeMap.put("amount", attribute.b().c());
                            attributeMap.put("operation", attribute.b().d().c());
                            return attributeMap;
                        }).collect(Collectors.toList());
                        break;
                    case "minecraft:custom_data":
                        CustomData custom_data = (CustomData) value;
                        data = custom_data.d().toString();
                        break;
                    case "minecraft:enchantments":
                        ItemEnchantments enchantments = (ItemEnchantments) value;
                        data = enchantments.b().stream().collect(Collectors.toMap(entry -> entry.getKey().g(), Object2IntMap.Entry::getIntValue));
                        break;
                }
                if (data == null) {
                    SXItem.getInst().getLogger().warning(key + "\t" + value);
                    continue;
                } else {
                    SXItem.getInst().getLogger().info(key + "\t" + data);
                }
                map.put(key.toString(), data);
            }
            return map;
        }
    }
    }
}
