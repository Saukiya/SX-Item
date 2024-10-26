package github.saukiya.tools.command;

import github.saukiya.tools.nms.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * 次级指令
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

    /**
     * 监听插件开启方法
     */
    public void onEnable() {
    }

    /**
     * 监听重载方法
     */
    public void onReload() {
    }

    /**
     * 监听关闭方法
     */
    public void onDisable() {
    }

    /**
     * 设置额外参数格式
     */
    protected final void setArg(String arg) {
        this.arg = " " + arg;
    }

    /**
     * 隐藏/显示指令
     *
     * @param hide 是否隐藏
     */
    protected final void setHide(boolean hide) {
        this.hide = hide;
    }

    /**
     * 设置指令发送者类型
     *
     * @param types 发送者类型
     */
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
