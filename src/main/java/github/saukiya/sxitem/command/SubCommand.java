package github.saukiya.sxitem.command;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.util.Message;
import github.saukiya.sxitem.util.MessageUtil;
import org.bukkit.command.CommandSender;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

/**
 * @author Saukiya
 */
public abstract class SubCommand {

    String cmd, arg = "";

    boolean hide = false;

    private SenderType[] types = new SenderType[]{SenderType.ALL};

    public SubCommand(String cmd) {
        this.cmd = cmd;
    }

    private String permission() {
        return SXItem.getInst().getName() + "." + cmd;
    }

    public abstract void onCommand(CommandSender sender, String[] args);

    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    public boolean isUse(CommandSender sender, SenderType type) {
        return sender.hasPermission(permission()) && Arrays.stream(types).anyMatch(senderType -> senderType.equals(type) || senderType.equals(SenderType.ALL));
    }

    protected void setArg(String arg) {
        this.arg = arg;
    }

    protected void setHide() {
        this.hide = true;
    }

    protected void setType(SenderType... types) {
        this.types = types;
    }

    public String getIntroduction() {
        return Message.staticGet("COMMAND." + cmd.toUpperCase());
    }

    public void sendIntroduction(CommandSender sender, String color, String label) {
        String clickCommand = MessageFormat.format("/{0} {1}", label, cmd);
        MessageUtil.getInst().componentBuilder()
                .add(color + MessageFormat.format("/{0} {1}{2}§7 - §c" + getIntroduction(), label, cmd, arg))
                .show(sender.isOp() ? "§8§oPermission: " + permission() : null)
                .runCommand(clickCommand)
                .send(sender);
    }

    public void onEnable() {
    }

    public void onReload() {
    }
}
