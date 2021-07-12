package github.saukiya.sxitem.command;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.util.Message;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

/**
 * @author Saukiya
 */
public abstract class SubCommand {

    static final List<SubCommand> commands = new ArrayList<>();

    String cmd, arg = "";

    boolean hide = false;

    private SenderType[] types = new SenderType[]{SenderType.ALL};

    public SubCommand(String cmd) {
        this.cmd = cmd;
    }

    /**
     * 注册指令方法
     */
    public final void registerCommand() {
        commands.add(this);
    }

    private String permission() {
        return SXItem.getInst().getName() + "." + cmd;
    }

    public abstract void onCommand(CommandSender sender, String[] args);

    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    public boolean isUse(CommandSender sender, SenderType type) {
        return sender.hasPermission(permission()) && IntStream.range(0, this.types.length).anyMatch(i -> this.types[i].equals(SenderType.ALL) || this.types[i].equals(type));
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
        return Arrays.stream(Message.values()).anyMatch(loc -> loc.name().equals("COMMAND__" + cmd.toUpperCase())) ? Message.getMsg(Message.valueOf("COMMAND__" + cmd.toUpperCase())) : "§7No Introduction";
    }

    public void sendIntroduction(CommandSender sender, String color, String label) {
        String clickCommand = MessageFormat.format("/{0} {1}", label, cmd);
        TextComponent tc = Message.getTextComponent(color + MessageFormat.format("/{0} {1}{2}§7 - §c" + getIntroduction(), label, cmd, arg), clickCommand, sender.isOp() ? "§8§oPermission: " + permission() : null);
        Message.send(sender, tc);
    }

    public void onEnable() {

    }

    public void onReload() {
    }
}
