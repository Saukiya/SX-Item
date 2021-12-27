package github.saukiya.sxitem.util;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class MessageUtil implements NMS {

    /**
     * 发送消息给玩家 - 支持PlaceholderAPI
     *
     * @param sender CommandSender
     * @param loc    Message
     * @param args   Object...
     */
    public final void send(CommandSender sender, Message loc, Object... args) {
        send(sender, Message.getMsg(loc, args));
    }

    public final void send(CommandSender sender, String msg) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            msg = PlaceholderUtil.setPlaceholders(player, msg);
            if (msg.startsWith("[ACTIONBAR]")) {
                sendActionBar(player, msg.substring(11));
                return;
            } else if (msg.startsWith("[TITLE]")) {
                String[] split = msg.substring(7).split(":");
                sendTitle(player, split[0], split.length > 1 ? split[1] : null, 5, 30, 5);
                return;
            }
        }
        sender.sendMessage(msg);
    }

    public void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }

    public void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }

    public abstract ComponentBuilder componentBuilder();

    public static MessageUtil getInst() {
        return NMS.getInst(MessageUtil.class);
    }
}
