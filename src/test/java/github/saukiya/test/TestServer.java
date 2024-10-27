package github.saukiya.test;

import lombok.val;
import net.minecraft.server.v1_12_R1.DispenserRegistry;
import net.minecraft.server.v1_12_R1.EnumColor;
import net.minecraft.server.v1_12_R1.ItemBanner;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class TestServer {

    public static void Initialization() {
        DispenserRegistry.c();
    }

    // 获取 1.12.2 语言表
    public static void main(String[] args) throws IOException {
        FileUtils.writeLines(new File("eula.txt"), Collections.singletonList("eula=true"));
        org.bukkit.craftbukkit.Main.main(args);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            System.out.println("input: " + input);
            switch (input) {
                case "item":
                    materialLocalization();
                    break;
                case "stop":
                    System.out.println("stop!");
                    Bukkit.shutdown();
                    return;
                case "skip":
                    System.out.println("skip!");
                    return;
            }
        }
    }

    public static void materialLocalization() throws IOException {
        val localizationKey = loadLocalization();
        val localization = loadLocalization();
        YamlConfiguration yaml = new YamlConfiguration();
        for (Material material : Material.values()) {
            val item = new ItemStack(material);
            String materialName = material.name();
            List<String> convert = TestMaterial.materialConvert(material.name());
            if (convert != null) {
                for (int i = 0, length = convert.size(); i < length; i++) {
                    materialName = convert.get(i);
                    item.setDurability((short) i);
                    val nmsItem = CraftItemStack.asNMSCopy(item);
                    String key = nmsItem.getItem().j(nmsItem) + ".name";
                    if (nmsItem.getItem() instanceof ItemBanner) { // 就你特殊
                        key = "item.banner." + EnumColor.fromInvColorIndex(nmsItem.getData() & 15) + ".name";
                    }
                    val value = localization.getProperty(key);
                    System.out.println(materialName + ": " + key + " - " + nmsItem.getName() + " - " + nmsItem.getItem().getClass().getSimpleName());
                    yaml.set(materialName, value);
                }
            } else {
                val nmsItem = CraftItemStack.asNMSCopy(item);
                val key = nmsItem.getItem().j(nmsItem) + ".name";
                val value = localization.getProperty(key);
                System.out.println(materialName + ": " + key + " - " + nmsItem.getName());
                yaml.set(materialName, value);
            }
        }
        yaml.save(new File("../src/test/resources/material_zh_cn.yml"));
    }

    public static Properties loadLocalization() {
        Properties properties = new Properties();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get("../src/test/resources/zh_cn.lang")), StandardCharsets.UTF_8))) {
            properties.load(reader);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }
}
