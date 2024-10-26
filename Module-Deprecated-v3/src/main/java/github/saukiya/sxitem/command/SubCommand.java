package github.saukiya.sxitem.command;

import org.bukkit.command.CommandSender;

import java.util.Arrays;

/**
 * @see github.saukiya.tools.command.SubCommand
 * @deprecated
 */
public abstract class SubCommand extends github.saukiya.tools.command.SubCommand {

    @Deprecated
    public SubCommand(String cmd, int priority) {
        super(cmd, priority);
    }

    @Deprecated
    protected final void setType(SenderType... types) {
        setType(Arrays.stream(types).map(senderType -> github.saukiya.tools.command.SenderType.valueOf(senderType.name())).toArray(github.saukiya.tools.command.SenderType[]::new));
    }

    @Deprecated
    public boolean isUse(CommandSender sender, SenderType type) {
        return super.isUse(sender, github.saukiya.tools.command.SenderType.valueOf(type.name()));
    }
}
