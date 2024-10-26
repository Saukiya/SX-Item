package github.saukiya.sxitem.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Delegate;
import org.bukkit.command.CommandSender;

/**
 * @see github.saukiya.tools.nms.MessageUtil
 * @deprecated
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class MessageUtil {

    @Deprecated
    @Getter
    private static final MessageUtil inst = new MessageUtil(github.saukiya.tools.nms.MessageUtil.getInst());

    @Delegate
    private github.saukiya.tools.nms.MessageUtil target;

    @Deprecated
    public final ComponentBuilder componentBuilder() {
        return new ComponentBuilder(builder());
    }

    @Deprecated
    public static void send(CommandSender sender, String msg) {
        github.saukiya.tools.nms.MessageUtil.send(sender, msg);
    }
}
