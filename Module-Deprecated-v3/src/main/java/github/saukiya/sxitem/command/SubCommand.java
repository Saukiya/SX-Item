package github.saukiya.sxitem.command;

import java.util.Arrays;

@Deprecated
public abstract class SubCommand extends github.saukiya.util.command.SubCommand {

    public SubCommand(String cmd, int priority) {
        super(cmd, priority);
    }

    protected final void setType(SenderType... types) {
        setType(Arrays.stream(types).map(senderType -> github.saukiya.util.command.SenderType.valueOf(senderType.name())).toArray(github.saukiya.util.command.SenderType[]::new));
    }
}
