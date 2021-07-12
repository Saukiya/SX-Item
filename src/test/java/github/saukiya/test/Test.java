package github.saukiya.test;


import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Tuple;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.text.StrLookup;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.text.StrSubstitutor;

import java.io.File;
import java.text.MessageFormat;
import java.util.*;

/**
 * @Author 格洛
 * @Since 2019/12/19 11:34
 */
public class Test {
    public static void main(String[] args) {
        // 配置读取
        File file = new File("Z:\\Dev\\Java\\Minecraft\\Project\\SX-Item\\src\\main\\resources\\RandomString\\NewRandom.yml");

        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        System.out.println(yml.saveToString());
        Map<String, List<Tuple<Double, String>>> map = new HashMap();
        for (String key : yml.getKeys(false)) {
            Object obj = yml.get(key);
            System.out.println("----> " + key);
            if (obj instanceof String) {
                map.put(key, Collections.singletonList(new Tuple<>(1D,obj.toString())));
            } else if (obj instanceof List){
                List<Tuple<Double, String>> list = new ArrayList<>();
                Object unitObj = ((List<?>) obj).get(0);
                System.out.println(unitObj.getClass());
                if (unitObj instanceof Map) {
                    List<Map> listMap = (List<Map>) obj;
                    for (Map map1 : listMap) {
                        System.out.println(map1);
                    }
                    continue;
                }
                // Deprecated 过时加载方式
                else {
                    Map<String, Integer> tempMap = new HashMap<>();
                    String value;
                    for (Object objs : (List) obj) {
                        if (objs instanceof List ) {
                            value = String.join("/n", (List) objs);
                        } else {
                            value = objs.toString();
                        }
                        tempMap.put(value, tempMap.getOrDefault(value, 0) + 1);
                    }
                    for (Map.Entry<String, Integer> entry : tempMap.entrySet()) {
                        list.add(new Tuple<>(Double.valueOf(entry.getValue()), entry.getKey()));
                    }
                }

                list.sort(Comparator.comparing(Tuple::a));
                map.put(key, list);
            }
        }
        System.out.println("-------------?");
        for (Map.Entry<String, List<Tuple<Double, String>>> entry : map.entrySet()) {
            System.out.println("key: " + entry.getKey());
            for (Tuple<Double, String> tuple : entry.getValue()) {
                System.out.println("- 权重:" + tuple.a() + " -> " + tuple.b());
            }
        }
//        for (String key : yml.getKeys(false)) {
//            for (Map.Entry<String, Object> entry : yml.getConfigurationSection(key).getValues(false).entrySet()) {
//
//            }
//            for (String s : ) {
//                System.out.println();
//            }
//        }

//        lore变量识别
//        String str = "攻击力: <<111>222> <333>";
//        StrSubstitutor ss = new StrSubstitutor(new ContextLookup());
//        ss.setVariablePrefix('<');
//        ss.setVariableSuffix('>');
//        ss.setEnableSubstitutionInVariables(true);
//        String resolvedString = ss.replace(str);
//        System.out.println(resolvedString);
    }

    public List<String> loadRandomData(Object obj) {
        if (obj instanceof String) {
            return Collections.singletonList(obj.toString());
        }
        List<String> list = new ArrayList<>();
        if (obj instanceof List) {
            for (Object objs : (List) obj) {
                if (objs instanceof List ) {
                    list.add(String.join("/n", (Iterable<? extends CharSequence>) objs));
                } else {
                    list.add(objs.toString());
                }
            }
        }
        return list;
    }

    static class ContextLookup extends StrLookup<String> {

        @Override
        public String lookup(String s) {
            System.out.println(s + " -> 000");
            return "000";
        }
    }

}

