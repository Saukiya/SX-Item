package github.saukiya.sxitem.data.player;

import github.saukiya.sxitem.SXItem;
import lombok.Getter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @author Saukiya
 */
@Getter
public class PlayerData extends YamlConfiguration {

    private final String key;

    private final File file;

    /**
     * 建立玩家数据并自行读取
     *
     * @param uuid UUID
     */
    PlayerData(UUID uuid) {
        this.key = uuid.toString();
        this.file = new File(SXItem.getInst().getDataFolder(), "PlayerData" + File.separator + key + ".yml");
        if (file.exists()) {
            try {
                load(file);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 保存数据并检查
     *
     * @throws IOException IOException
     */
    public void save() {
        if (getKeys(false).size() == 0) {
            file.delete();
            return;
        }
        try {
            save(file);
        } catch (IOException e) {
            SXItem.getInst().getLogger().warning("PlayerFile: " + file.getName() + " save Error!");
        }
    }
}
