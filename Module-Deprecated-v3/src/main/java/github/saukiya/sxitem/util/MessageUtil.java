package github.saukiya.sxitem.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Delegate;
import org.bukkit.command.CommandSender;

@Deprecated
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class MessageUtil {

    @Getter
    private static final MessageUtil inst = new MessageUtil(github.saukiya.util.nms.MessageUtil.getInst());

    @Delegate
    private github.saukiya.util.nms.MessageUtil target;

    public final ComponentBuilder componentBuilder() {
        return new ComponentBuilder(builder());
    }

    public static void send(CommandSender sender, String msg) {
        github.saukiya.util.nms.MessageUtil.send(sender, msg);
    }
}
