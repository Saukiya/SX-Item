package github.saukiya.util.nms;

import github.saukiya.util.helper.PlaceholderHelper;
import lombok.Getter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public abstract class MessageUtil implements NMS {

    @Getter
    private static final MessageUtil inst = NMS.getInst(MessageUtil.class, "v1_21_R1", "v1_16_R3", "v1_13_R2", "v1_12_R1", "v1_11_R1", "v1_8_R3");

    public void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }

    public void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }

    public abstract Builder builder();

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

    public abstract static class Builder {

        @Getter
        protected TextComponent handle;

        protected BaseComponent current;

        protected Builder() {
            handle = setCurrent(new TextComponent(""));
        }

        public Builder add(BaseComponent base) {
            handle.addExtra(setCurrent(base));
            return this;
        }

        public Builder add(String text) {
            handle.addExtra(setCurrent(new TextComponent(text)));
            return this;
        }

        public Builder add(Material material) {
            handle.addExtra(setCurrent(new TranslatableComponent((material.isBlock() ? "block" : "item") + "." + material.getKey().getNamespace() + "." + material.getKey().getKey())));
            return this;
        }

        public Builder add(ItemStack item) {
            if (item == null) item = new ItemStack(Material.AIR);
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.hasDisplayName()) {
                add(meta.getDisplayName());
            } else {
                add(item.getType());
            }
            return show(item);
        }

        public Builder show(List<String> list) {
            return show(String.join("\n", list));
        }

        public abstract Builder show(String text);

        public abstract Builder show(ItemStack item);

        public Builder openURL(String value) {
            return click(ClickEvent.Action.OPEN_URL, value);
        }

        public Builder runCommand(String value) {
            return click(ClickEvent.Action.RUN_COMMAND, value);
        }

        public Builder suggestCommand(String value) {
            return click(ClickEvent.Action.SUGGEST_COMMAND, value);
        }

        protected Builder click(ClickEvent.Action action, String value) {
            current.setClickEvent(new ClickEvent(action, value));
            return this;
        }

        public void send(CommandSender sender) {
            sender.spigot().sendMessage(handle);
        }

        protected <V extends BaseComponent> V setCurrent(V baseComponent) {
            return (V) (current = baseComponent);
        }
    }
}
