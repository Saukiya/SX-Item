package github.saukiya.sxitem.command;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.util.Message;
import github.saukiya.sxitem.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

/**
 * @author Saukiya
 */
@RequiredArgsConstructor
public abstract class SubCommand implements Comparable<SubCommand> {

    public final String cmd;

    final int priority;

    String arg = "";

    boolean hide = false;

    SenderType[] types = new SenderType[]{SenderType.ALL};

    public abstract void onCommand(CommandSender sender, String[] args);

    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    protected String permission() {
        return SXItem.getInst().getName() + "." + cmd;
    }

    protected String getIntroduction() {
        return Message.getStatic("COMMAND." + cmd.toUpperCase());
    }

    public void onEnable() {
    }

    public void onReload() {
    }

    public void onDisable() {
    }

    protected final void setArg(String arg) {
        this.arg = " " + arg;
    }

    protected final void setHide() {
        this.hide = true;
    }

    protected final void setType(SenderType... types) {
        this.types = types;
    }

    public final boolean isUse(CommandSender sender, SenderType type) {
        return sender.hasPermission(permission()) && Arrays.stream(types).anyMatch(senderType -> senderType.equals(type) || senderType.equals(SenderType.ALL));
    }

    public final void sendIntroduction(CommandSender sender, String color, String label) {
        String clickCommand = MessageFormat.format("/{0} {1}", label, cmd);
        MessageUtil.getInst().componentBuilder()
                .add(color + MessageFormat.format("/{0} {1}{2}§7 - §c" + getIntroduction(), label, cmd, arg))
                .show(sender.isOp() ? "§8§oPermission: " + permission() : null)
                .runCommand(clickCommand)
                .send(sender);
    }

    @Override
    public final int compareTo(SubCommand cmd) {
        return Integer.compare(priority, cmd.priority);
    }
}
