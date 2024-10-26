package github.saukiya.tools.nms;

import lombok.val;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class MessageUtil_v1_8_R3 extends MessageUtil {

    @Override
    public void sendActionBar(Player player, String message) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        PacketPlayOutChat packet = new PacketPlayOutChat(null, (byte) ChatMessageType.ACTION_BAR.ordinal());
        packet.components = new BaseComponent[]{new TextComponent(message)};
        craftPlayer.getHandle().playerConnection.sendPacket(packet);
    }

    @Override
    public void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        CraftPlayer craftPlayer = (CraftPlayer) player;

        PacketPlayOutTitle packetSubtitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, CraftChatMessage.fromString(title)[0], fadeIn, stay, fadeOut);
        craftPlayer.getHandle().playerConnection.sendPacket(packetSubtitle);

        if (subtitle != null) {
            packetSubtitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, CraftChatMessage.fromString(subtitle)[0], fadeIn, stay, fadeOut);
            craftPlayer.getHandle().playerConnection.sendPacket(packetSubtitle);
        }
    }

    @Override
    public Builder builder() {
        return new BuilderImpl();
    }

    static class BuilderImpl extends Builder {

        @Override
        public Builder add(Material material) {
            Item item = CraftMagicNumbers.getItem(material);
            add(new TranslatableComponent((item.k() ? new net.minecraft.server.v1_8_R3.ItemStack(item).a() : item.getName()) + ".name"));
            return this;
        }

        @Override
        public Builder show(String text) {
            current.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7" + text)}));
            return this;
        }

        @Override
        public Builder show(ItemStack item) {
            val wrapper = NbtUtil.getInst().createTagWrapper();
            wrapper.set("id", "minecraft:" + item.getType().name().toLowerCase(Locale.ROOT));
            wrapper.set("Count", (byte) item.getAmount());
            wrapper.set("Damage", item.getDurability());
            wrapper.set("tag", NbtUtil.getInst().getItemNBT(item));
            current.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[]{new TextComponent(wrapper.nbtToString())}));
            return this;
        }

        @Override
        public void send(CommandSender sender) {
            if (sender instanceof Player) {
                ((Player) sender).spigot().sendMessage(handle);
            } else {
                sender.sendMessage(handle.toLegacyText());
            }
        }
    }
}
