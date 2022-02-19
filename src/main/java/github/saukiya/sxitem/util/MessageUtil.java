package github.saukiya.sxitem.util;

import github.saukiya.sxitem.helper.PlaceholderHelper;
import lombok.Getter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class MessageUtil implements NMS {

    @Getter
    private static final MessageUtil inst = NMS.getInst(MessageUtil.class, "v1_16_R3", "v1_13_R2", "v1_12_R1", "v1_11_R1", "v1_8_R3");

    public void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }

    public void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }

    public abstract ComponentBuilder componentBuilder();

    /**
     * 发送消息给玩家 - 支持PlaceholderAPI
     *
     * @param sender CommandSender
     * @param msg    Message
     */
    public static void send(CommandSender sender, String msg) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            msg = PlaceholderHelper.setPlaceholders(player, msg);
            if (msg.startsWith("[ACTIONBAR]")) {
                getInst().sendActionBar(player, msg.substring(11));
                return;
            } else if (msg.startsWith("[TITLE]")) {
                String[] args = msg.substring(7).split(":");
                int fadeIn = 5, stay = 20, fadeOut = 5;
                if (args.length > 4) {
                    fadeIn = Integer.parseInt(args[2]);
                    stay = Integer.parseInt(args[3]);
                    fadeOut = Integer.parseInt(args[4]);
                }
                getInst().sendTitle(player, args[0], args.length > 1 ? args[1] : null, fadeIn, stay, fadeOut);
                return;
            }
        }
        sender.sendMessage(msg);
    }
}
