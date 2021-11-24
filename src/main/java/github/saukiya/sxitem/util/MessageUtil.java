package github.saukiya.sxitem.util;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class MessageUtil extends NMS {

    /**
     * 发送消息给玩家 - 支持PlaceholderAPI
     *
     * @param entity LivingEntity
     * @param loc    Message
     * @param args   Object...
     */
    public void send(LivingEntity entity, Message loc, Object... args) {
        send(entity, Message.getMsg(loc, args));
    }

    public abstract void send(LivingEntity entity, String msg);

    public abstract TextComponent getTextComponent(String msg, String command, String showText);

    public abstract TranslatableComponent showItem(@Nonnull Material material);

    public abstract BaseComponent showItem(@Nullable ItemStack item);

    public abstract void send(CommandSender sender, TextComponent tc);

    public static MessageUtil getInst() {
        return NMS.getInst(MessageUtil.class);
    }
}
