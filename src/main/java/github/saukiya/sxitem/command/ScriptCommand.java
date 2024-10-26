package github.saukiya.sxitem.command;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.util.Message;
import github.saukiya.tools.command.SubCommand;
import github.saukiya.tools.nms.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import javax.script.Bindings;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 调用脚本指令 (脚本启用时生效)
 * <pre>
 *  <code>/si script Default testPlayer player Hello</code> - 调用 Default 的 testPlayer 方法
 * </pre>
 */
public class ScriptCommand extends SubCommand implements Listener {

    public ScriptCommand() {
        super("script", 80);
        setArg("[file] [func] <args>");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length < 3) {
            MessageUtil.send(sender, Message.ADMIN__NO_FORMAT.get());
            return;
        }
        if (!SXItem.getScriptManager().containsFile(args[1])) {
            MessageUtil.send(sender, Message.SCRIPT__NULL_FILE.get(args[1]));
            return;
        }
        Object[] scriptArgs = new Object[args.length - 3];
        for (int i = 3, index = 0; i < args.length; i++, index++) {
            Player player = Bukkit.getPlayerExact(args[i]);
            if (player != null) scriptArgs[index] = player;
            scriptArgs[index] = player != null ? player : args[i];
        }
        try {
            Object result = SXItem.getScriptManager().callFunction(args[1], args[2], scriptArgs.length != 0 ? scriptArgs : null);
            String[] resultPrint = new String[2];
            if (result instanceof Bindings) {
                resultPrint[0] = Arrays.toString(((Bindings) result).entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue()).toArray(String[]::new));
                resultPrint[1] = "Bindings";
            } else {
                resultPrint[0] = String.valueOf(result);
                resultPrint[1] = result != null ? result.getClass().getSimpleName() : "N/A";
            }
            MessageUtil.send(sender, Message.SCRIPT__INVOKE_RESULT.get(resultPrint[0], resultPrint[1]));
        } catch (Exception e) {
            MessageUtil.send(sender, Message.SCRIPT__INVOKE_FAIL.get(e.getMessage()));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        switch (args.length) {
            case 2:
                return SXItem.getScriptManager().getScriptNames().stream().filter(name -> name.contains(args[1])).collect(Collectors.toList());
            case 3:
                return SXItem.getScriptManager().getScriptFunc(args[1]).stream().filter(name -> name.contains(args[2])).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public void onEnable() {
        onReload();
    }

    @Override
    public void onReload() {
        setHide(!SXItem.getScriptManager().isEnabled());
    }
}
