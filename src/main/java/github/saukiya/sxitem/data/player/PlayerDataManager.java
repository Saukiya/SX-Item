package github.saukiya.sxitem.data.player;

import github.saukiya.sxitem.SXItem;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Saukiya
 */

public class PlayerDataManager implements Listener {

    @Getter
    private final Map<UUID, PlayerData> playerDataMap = new HashMap<>();

    public PlayerDataManager() {
        autoSave();
        Bukkit.getPluginManager().registerEvents(this, SXItem.getInst());
    }

    /**
     * 定时保存玩家数据
     */
    private void autoSave() {
        new BukkitRunnable() {
            @Override
            public void run() {
                saveAll();
            }
        }.runTaskTimerAsynchronously(SXItem.getInst(), 12000, 12000);
    }

    /**
     * 获取玩家数据 - 如果玩家在线，则保存到缓存区
     *
     * @param uuid UUID
     * @return PlayerData
     */
    public PlayerData get(UUID uuid) {
        PlayerData playerData = playerDataMap.get(uuid);
        if (playerData == null) {
            playerData = new PlayerData(uuid);
        }
        if (Bukkit.getPlayer(uuid) != null) {
            playerDataMap.put(uuid, playerData);
        }
        return playerData;
    }

    /**
     * 卸载玩家数据并保存
     *
     * @param uuid UUID
     */
    public void remove(UUID uuid) {
        PlayerData playerData = playerDataMap.remove(uuid);
        if (playerData != null) {
            playerData.save();
        }
    }

    /**
     * 保存全部数据
     */
    public void saveAll() {
        if (playerDataMap.size() == 0) return;
        for (PlayerData playerData : playerDataMap.values()) {
            playerData.save();
        }
        SXItem.getInst().getLogger().info("Save PlayerData!");
    }

    @EventHandler
    void onPlayerJoinEvent(PlayerLoginEvent event) {
        get(event.getPlayer().getUniqueId());
    }

    @EventHandler
    void onPlayerQuitEvent(PlayerQuitEvent event) {
        remove(event.getPlayer().getUniqueId());
    }
}
