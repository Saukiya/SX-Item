package github.saukiya.sxitem.command.sub;

import github.saukiya.sxitem.command.SenderType;
import github.saukiya.sxitem.command.SubCommand;
import github.saukiya.sxitem.nms.NBTItemWrapper;
import github.saukiya.sxitem.util.NbtUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class TestCommand extends SubCommand {
    public TestCommand() {
        super("test");
        setType(SenderType.PLAYER);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length < 3) return;
        String key = args[2];
        String value = args.length == 4 ? args[3] : null;
        Player player = (Player) sender;
        ItemStack itemStack = player.getEquipment().getItemInMainHand();
        if (itemStack.getType().isItem()) {
            NBTItemWrapper tagWrapper = NbtUtil.getInst().getItemTagWrapper(itemStack);
            if (args[1].equals("set") && args.length == 4) {
                tagWrapper.set(key, value);
                tagWrapper.save();
            } else if (args[1].equals("remove")) {
                if (args.length == 4) {
                    List<String> list = tagWrapper.getStringList(key);
                    list.remove(value);
                    tagWrapper.set(key, list);
                } else {
                    tagWrapper.remove(key);
                }
                tagWrapper.save();
            } else if (args[1].equals("add") && args.length == 4) {
                List<String> list = tagWrapper.getStringList(key);
                list.add(value);
                tagWrapper.set(key, list);
                tagWrapper.save();
            }
        }
    }
}
