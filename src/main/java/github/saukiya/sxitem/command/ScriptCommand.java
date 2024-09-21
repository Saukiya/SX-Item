package github.saukiya.sxitem.command;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.util.Message;
import github.saukiya.util.command.SubCommand;
import github.saukiya.util.nms.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Collections;
import java.util.List;

/**
 * @author Saukiya
 */
public class ScriptCommand extends SubCommand implements Listener {

    public ScriptCommand() {
        super("script", 0);
        setArg("<scriptName> <functionName> [args]");
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
            MessageUtil.send(sender, Message.SCRIPT__INVOKE_RESULT.get(result, result != null ? result.getClass().getSimpleName() : "N/A"));
        } catch (Exception e) {
            MessageUtil.send(sender, Message.SCRIPT__INVOKE_FAIL.get(e.getMessage()));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) return SXItem.getScriptManager().getFileNames();
        if (args.length == 3) return Collections.singletonList("I don't know you function. :)");
        return null;
    }
}
