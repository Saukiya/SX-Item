package github.saukiya.test;


import net.minecraft.util.Tuple;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.text.StrLookup;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.text.StrSubstitutor;

import java.io.File;
import java.util.*;

/**
 * @Author 格洛
 * @Since 2019/12/19 11:34
 */
public class Test {
    public static void main(String[] args) {
        distinguish();
        loadData();
    }

    /**
     * lore变量识别
     */
    public static void distinguish() {
        String str = "<s:DefaultPrefix>&c炎之洗礼<s:DefaultSuffix> <s:<l:品质>Color><l:品质>";

        StrSubstitutor ss = new StrSubstitutor(new ContextLookup());
        ss.setVariablePrefix('<');
        ss.setVariableSuffix('>');
        ss.setEnableSubstitutionInVariables(true);
        String resolvedString = ss.replace(str);
        System.out.println(resolvedString);
    }

    /**
     * 加载Random数据
     */
    public static void loadData() {

        // 配置读取的方式
        File file = new File("./src/main/resources/RandomString/NewRandom.yml");

        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        Map<String, List<Tuple<Double, String>>> mainMap = new HashMap();
        for (String key : yml.getKeys(false)) {
            Object obj = yml.get(key);
            System.out.println("----> " + key + " - " + obj.getClass().getSimpleName());
            //单行 key - v
            if (obj instanceof String) {
                mainMap.put(key, Collections.singletonList(new Tuple<>(1D,obj.toString())));
            }
            // 多行 key - vList
            else if (obj instanceof List){
                List<Tuple<Double, String>> list = new ArrayList<>();
                Object unitObj = ((List<?>) obj).get(0);
                System.out.println(unitObj.getClass());
                if (unitObj instanceof Map) {
                    List<Map> listMap = (List<Map>) obj;
                    for (Map map : listMap) {
                        System.out.println("?> " + map);
                        list.add(new Tuple<>(Double.valueOf(map.get("rate").toString()), loadDataString(map.get("string"))));
                    }
                }
                else {// Deprecated
                    // TODO 标记可转化文本并备份
                    Map<String, Integer> tempMap = new HashMap<>();
                    String value;
                    for (Object objs : (List) obj) {
                        value = loadDataString(objs);
                        tempMap.put(value, tempMap.getOrDefault(value, 0) + 1);
                    }
                    for (Map.Entry<String, Integer> entry : tempMap.entrySet()) {
                        list.add(new Tuple<>(Double.valueOf(entry.getValue()), entry.getKey()));
                    }
                }

                list.sort(Comparator.comparing(Tuple::a));
                mainMap.put(key, list);
            }
        }
        System.out.println("-------------?");
        for (Map.Entry<String, List<Tuple<Double, String>>> entry : mainMap.entrySet()) {
            System.out.println("key: " + entry.getKey());
            for (Tuple<Double, String> tuple : entry.getValue()) {
                System.out.println("- 权重:" + tuple.a() + " -> " + tuple.b());
            }
        }
    }

    public static String loadDataString(Object obj) {
//        if (obj == null) return "";
        if (obj instanceof String) {
            return obj.toString().replace("\n", "/n");
        }
        if (obj instanceof List) {
            return String.join("/n", (List) obj);
        }
        return "无法识别";
    }

    static class ContextLookup extends StrLookup<String> {

        @Override
        public String lookup(String s) {
            System.out.println(s + " -> [" + s.charAt(0) + "]");
            return "[" + s.substring(0, 1) + "]";
        }
    }

}

