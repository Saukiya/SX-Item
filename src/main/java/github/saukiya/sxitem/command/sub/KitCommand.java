package github.saukiya.sxitem.command.sub;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.command.SubCommand;
import github.saukiya.sxitem.data.player.PlayerData;
import github.saukiya.sxitem.util.Config;
import github.saukiya.sxitem.util.Message;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.File;
import java.util.*;

/**
 * @Author 格洛
 * @Since 2020/1/1 13:11
 */
public class KitCommand extends SubCommand implements Listener {
    private final File file = new File("world" + File.separator + "playerdata");

    private final Map<String, Data> kitMap = new HashMap<>();
    private Data defaultKit;

    public KitCommand() {
        super("kit");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Message.getMsg(Message.ADMIN__NO_FORMAT));
            return;
        }
        Data data = kitMap.get(args[1]);
        if (data == null) {
            sender.sendMessage("§8[§6礼包§8] §7" + String.join(" ", kitMap.keySet()));
            return;
        }
        Player player = null;

        if (args.length > 2) {
            player = Bukkit.getPlayerExact(args[2]);
            if (player == null) {
                sender.sendMessage(Message.getMsg(Message.ADMIN__NO_ONLINE));
                return;
            }
        } else if (sender instanceof Player) {
            player = (Player) sender;
        }

        if (player != null) {
            sender.sendMessage(Message.getMsg(Message.ADMIN__GIVE_KIT, player.getName(), args[1]));
            data.give(player);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return args.length == 2 ? (kitMap.size() > 0 ? new ArrayList<>(kitMap.keySet()) : Collections.singletonList("NULL")) : null;
    }

    @EventHandler
    void on(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!new File(file, player.getUniqueId() + ".dat").exists() && defaultKit != null) {
            defaultKit.give(player);
        }
    }


    @Override
    public void onEnable() {
        Config.regConfig("Kit");
        load();
    }

    @Override
    public void onReload() {
        load();
    }

    void load() {
        YamlConfiguration config = Config.getConfig("Kit");
        kitMap.clear();
        for (String key : config.getConfigurationSection("List").getKeys(false)) {
            kitMap.put(key, new Data(key, config.getConfigurationSection("List." + key)));
        }
        String defaultKit = config.getString("Default");
        if (kitMap.containsKey(defaultKit)) {
            this.defaultKit = kitMap.get(defaultKit);
        } else {
            SXItem.getInst().getLogger().warning("null default kit: " + defaultKit);
        }
        SXItem.getInst().getLogger().info("Kit >> Load " + kitMap.size() + " Data");
    }

    @Getter
    class Data {
        private final String key;
        private final int cooldown;
        private final List<KitItem> items = new ArrayList<>();

        Data(String key, ConfigurationSection config) {
            this.key = key;
            this.cooldown = config.getInt("cooldown", -1);
            for (String str : config.getStringList("items")) {
                String[] args = str.split(" ");
                KitItem kitItem = new KitItem(args[0]);
                for (int i = 1; i < args.length; i++) {
                    String arg = args[i];
                    if (arg.startsWith("slot:")) {
                        kitItem.setSlot(Integer.parseInt(arg.substring(5)));
                    } else if (arg.startsWith("amount:")) {
                        kitItem.setAmount(Integer.parseInt(arg.substring(7)));
                    }
                }
                items.add(kitItem);
            }
        }

        public void give(Player player) {
            PlayerInventory inv = player.getInventory();
            EntityEquipment eq = player.getEquipment();
            for (KitItem kit : items) {
                for (int i = 0; i < kit.amount; i++) {
                    ItemStack item = SXItem.getItemDataManager().getItem(kit.key, player);
                    if (item != null) {
                        if (kit.slot > 0 && (inv.getItem(kit.slot) == null || inv.getItem(kit.slot).getType().isAir())) {
                            inv.setItem(kit.slot, item);
                        } else {
                            inv.addItem(item);
                        }
                    }
                }
            }
            if (cooldown > 0) {
                PlayerData playerData = SXItem.getPlayerDataManager().get(player.getUniqueId());
                playerData.set("KitReceive." + key, System.currentTimeMillis());
                playerData.save();
            }
        }
    }

    @Getter
    class KitItem {
        String key;
        @Setter
        int amount = 1;
        @Setter
        int slot = -1;

        KitItem(String key) {
            this.key = key;
        }
    }
}
