package github.saukiya.util.command;

import github.saukiya.util.nms.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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

    MainCommand mainCommand;

    public abstract void onCommand(CommandSender sender, String[] args);

    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    public boolean isUse(CommandSender sender, SenderType type) {
        return sender.hasPermission(permission()) && Arrays.stream(types).anyMatch(senderType -> senderType.equals(type) || senderType.equals(SenderType.ALL));
    }

    protected String permission() {
        return mainCommand.plugin.getName().toLowerCase(Locale.ROOT) + "." + cmd;
    }

    protected String getIntroduction() {
        return mainCommand.message.apply("COMMAND." + cmd.toUpperCase());
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

    public final void sendIntroduction(CommandSender sender, String color, String label) {
        String clickCommand = MessageFormat.format("/{0} {1}", label, cmd);
        MessageUtil.getInst().builder()
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
