package github.saukiya.test;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.random.RandomDocker;
import github.saukiya.sxitem.nms.*;
import github.saukiya.sxitem.util.NbtUtil;
import github.saukiya.sxitem.util.NbtUtil_v1_17_R1;
import github.saukiya.sxitem.util.Tuple;
import lombok.SneakyThrows;
import net.minecraft.nbt.*;
import org.apache.commons.lang.text.StrLookup;
import org.apache.commons.lang.text.StrMatcher;
import org.apache.commons.lang.text.StrSubstitutor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Test {

    static Map<String, List<Tuple<Double, String>>> dataMap = new HashMap();
    static Pattern pattern = Pattern.compile("v(\\d+)_(\\d+)_R(\\d+)");

    /**
     * 属于NBTTagWrapper的private方法
     * get需求: ssss.sss.ss
     * ssss 和 sss 为 NBTTagCompound 不存在直接返回 null
     * ss 为 NBTBase 不存在则直接返回null
     */
    public static NBTBase get(NBTTagCompound compound, String path) {
        int index = path.indexOf('.');
        if (index == -1) return compound.get(path);
        NBTBase nbtBase = compound.get(path.substring(0, index));
        if (nbtBase instanceof NBTTagCompound) {
            return get((NBTTagCompound) nbtBase, path.substring(index + 1));
        }
        return null;
    }

    /**
     * set需求: ssss.sss.ss
     * ssss 和 sss 为NBTTagCompound 不存在则创建
     * 不为 NBTTagCompound 则返回throw Ex
     * ss 直接设置并返回原参数
     */
    public static Object set(NBTTagCompound compound, String path, Object object) throws Exception {
        int index = path.indexOf('.');
        if (index == -1) {//不存在子节点时
            return getValue(compound.set(path, NbtUtil.getInst().toNMS(object)));
        } else {//存在子节点时
            String subPath = path.substring(0, index);
            NBTBase nbtBase = compound.get(subPath);
            if (nbtBase == null) {
                nbtBase = new NBTTagCompound();
                compound.set(subPath, nbtBase);
            }
            if (nbtBase instanceof NBTTagCompound) {
                return set((NBTTagCompound) nbtBase, path.substring(index + 1), object);
            }
            throw new Exception("SX-Item: 当前路径并不是NBTTagCompound -> " + subPath);
        }
    }

    public static Object getValue(Object nbtBase) {
        if (nbtBase instanceof NBTBase) {
            if (nbtBase instanceof NBTTagCompound) {
                NBTTagCompound nbtTagCompound = (NBTTagCompound) nbtBase;
                return nbtTagCompound.getKeys().stream().collect(Collectors.toMap(key -> key, key -> getValue(nbtTagCompound.get(key)), (a, b) -> b));
            } else if (nbtBase instanceof NBTTagList) {
                return ((NBTTagList) nbtBase).stream().map(Test::getValue).collect(Collectors.toList());
            } else if (nbtBase instanceof NBTTagByteArray) {
                return ((NBTTagByteArray) nbtBase).getBytes();
            } else if (nbtBase instanceof NBTTagIntArray) {
                return ((NBTTagIntArray) nbtBase).getInts();
            } else if (nbtBase instanceof NBTTagLongArray) {
                return ((NBTTagLongArray) nbtBase).getLongs();
            } else if (nbtBase instanceof NBTTagByte) {
                return ((NBTTagByte) nbtBase).asByte();
            } else if (nbtBase instanceof NBTTagShort) {
                return ((NBTTagShort) nbtBase).asShort();
            } else if (nbtBase instanceof NBTTagInt) {
                return ((NBTTagInt) nbtBase).asInt();
            } else if (nbtBase instanceof NBTTagFloat) {
                return ((NBTTagFloat) nbtBase).asFloat();
            } else if (nbtBase instanceof NBTTagDouble) {
                return ((NBTTagDouble) nbtBase).asDouble();
            } else if (nbtBase instanceof NBTTagLong) {
                return ((NBTTagLong) nbtBase).asLong();
            } else if (nbtBase instanceof NBTTagString) {
                return ((NBTTagString) nbtBase).asString();
            } else if (nbtBase instanceof NBTTagEnd) {
                return null;
            }
        }
        return null;
    }

    @SneakyThrows
    public static void main(String[] args) {
        NbtUtil_v1_17_R1 nbtUtil = new NbtUtil_v1_17_R1();
        TagCompound tagCompound = nbtUtil.asTagCompoundCopy(getNBT());
        NBTTagWrapper nbtTagWrapper = nbtUtil.newNBTTagWrapper(tagCompound);
        System.out.println(tagCompound);
        Integer integer = nbtTagWrapper.getInt("asgfag");
//        YamlConfiguration yamlConfiguration = null;
//        yamlConfiguration.getString("");
//        getAndSetPathToCompound();
//        yamlToTagTest();
//        gsonTest();
//        conversionNBT();
    }

    public static void getAndSetPathToCompound() throws Exception {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        NBTTagCompound subTagCompound = new NBTTagCompound();
        NBTTagCompound qwqTagCompound = new NBTTagCompound();
        NBTTagCompound testTagCompound = new NBTTagCompound();
        nbtTagCompound.set("sub", subTagCompound);
        subTagCompound.set("qwq", qwqTagCompound);
        qwqTagCompound.set("test", testTagCompound);
        subTagCompound.set("float", NBTTagFloat.a(4f));
        qwqTagCompound.set("string", NBTTagString.a("测试文本"));
        testTagCompound.set("ints", new NBTTagIntArray(new int[]{5, 23, 7, 873, 4, 46, 3, 7, 34}));

        System.out.println(nbtTagCompound);
        System.out.println("getCompound: \t" + get(nbtTagCompound, "sub")); //正确
        System.out.println("getCompound: \t" + get(nbtTagCompound, ".sub")); //null
        System.out.println("getCompound: \t" + get(nbtTagCompound, "sub.qwq")); //正确
        System.out.println("getCompound: \t" + get(nbtTagCompound, "sub.qwq1")); //null
        System.out.println("getCompound: \t" + get(nbtTagCompound, "sub.qwq1.test")); //null
        System.out.println("getCompound: \t" + get(nbtTagCompound, "sub.qwq.test")); //正确
        System.out.println("getCompound: \t" + get(nbtTagCompound, "sub.qwq.test1")); //null
        System.out.println("getCompound: \t" + get(nbtTagCompound, "sub.qwq.test.ints")); //正确
        System.out.println(set(nbtTagCompound, "sub.qwq.lala.test", nbtTagCompound));//正确
        System.out.println(nbtTagCompound);//正确
        System.out.println(set(nbtTagCompound, "sub.qwq.lala.test", nbtTagCompound).getClass().getSimpleName());//正确 - 返回了原来的值
        try {
            System.out.println(set(nbtTagCompound, "sub.qwq.lala.test.error", nbtTagCompound));//error
        } catch (Exception ignored) {
            System.out.println("error -> set(nbtTagCompound, \"sub.qwq.lala.test.error\", nbtTagCompound)");
        }
        System.out.println(set(nbtTagCompound, "sub.qwq", nbtTagCompound));//正确
    }

    public static void yamlToTagTest() throws Exception {
        // 如何解决：数组被yaml强制转换List
        // 1.int[]被强转成List<Integer>
        // 1.long[]被强转成List<Long>
        //
        // 已解决: yaml读到List内元素为Integer、Byte、Long(Long版本检测)后，转为相应数组
        YamlConfiguration yaml = new YamlConfiguration();
        long[] nums = new long[]{1, 5, 7, 8, 2, 6};
        List<Long> longList = Arrays.asList(1L, 5L, 7L, 8L, 2L, Long.MAX_VALUE);
        Map<String, Object> subMap = new HashMap<>();
        subMap.put("string", "23333");
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("int", Integer.MAX_VALUE);
        objectMap.put("long", Long.MAX_VALUE);
        objectMap.put("byte", Byte.MAX_VALUE);
        objectMap.put("float", Float.MAX_VALUE);
        objectMap.put("map", subMap);
        yaml.set("boolean", true);
        yaml.set("map", objectMap);
        yaml.set("nums", nums);
        yaml.set("long.List", longList);//为 num -> List
        System.out.println("yaml-json: \n" + yaml.saveToString());
        System.out.println("boolean: \t" + yaml.get("boolean").getClass().getSimpleName());
        System.out.println("nums: \t\t" + yaml.get("nums").getClass().getSimpleName());
        System.out.println("long.List: \t" + yaml.get("long.List").getClass().getSimpleName());
        System.out.println("map: \t\t" + yaml.get("map").getClass().getSimpleName());
        System.out.println("===================");

        YamlConfiguration loadYaml = new YamlConfiguration();
        loadYaml.loadFromString(yaml.saveToString());
        System.out.println("boolean:  \t" + loadYaml.get("boolean").getClass().getSimpleName());
        System.out.println("nums:  \t\t" + loadYaml.get("nums").getClass().getSimpleName());
        System.out.println("long.List: \t" + loadYaml.get("long.List").getClass().getSimpleName());
        System.out.println("map:  \t\t" + loadYaml.get("map").getClass().getSimpleName());

        TagBase tagBase = TagBase.toTag(loadYaml);
        System.out.println("sxNBT: \t\t" + tagBase);
        System.out.println("nmsNBT: \t" + NbtUtil.getInst().parseNMSCompound(tagBase.toString()));
        System.out.println("sxNBT-json: \n" + tagBase.toJson());

        NBTTagList tagList = new NBTTagList();
        tagList.add(NBTTagByte.a(true));
        tagList.add(NBTTagByte.a(true));
        tagList.add(NBTTagByte.a(false));
        tagList.add(NBTTagByte.a(true));
        NBTTagByteArray bytes = new NBTTagByteArray(new byte[] {1,3,5,6});
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        nbtTagCompound.set("tagList", tagList);
        nbtTagCompound.set("bytes", bytes);
        nbtTagCompound.set("byte", NBTTagByte.a(false));
        System.out.println(nbtTagCompound);
        //TODO
        // 如何解决:nbtTag无法像yaml那么灵活可以get("sub1.sub2.sub3")
        // 方案一:
        // 允许在当前节点中存在"sub1.sub2.sub3"
        // get(path) 时会优先匹配sub1.sub2.sub3 再匹配 sub1.sub2 以此类推 (set? setPath?)
        // 同时存在 sub1.sub2.sub3 和 sub1.sub2 请出门左转
        // set(path) 可以分为set() 和setPath()
        // 方案二: 取消灵活性，只允许匹配当前子节点
//        NBTTagCompound compound = new NBTTagCompound();
//        compound.set("tag.sub", NBTTagString.a("Test"));
//        System.out.println("compound: " + compound);
//        System.out.println("compound-tag.sub: " + compound.get("tag.sub"));
//
//        TagCompound tagCompound = new TagCompound();
//        tagCompound.put("tag.List", new TagList(Arrays.asList(
//                new TagLong(1),
//                new TagLong(1),
//                new TagLong(1),
//                new TagLong(1)
//        )));
//
//        System.out.println("sxToString: " + tagCompound);
//        System.out.println("sxStr -> nms: " + NbtUtil.getInst().parseNMSCompound(tagCompound.toString()).toString());
//        System.out.println("sx -> nms: " + NbtUtil.getInst().asNMSCompoundCopy(tagCompound).toString());
    }

    public static void testVersion(String version) {
        for (String thisVersion : Arrays.asList(
                "v1_8_R3",
                "v1_11_R1",
                "v1_12_R1",
                "v1_13_R2",
                "v1_14_R1",
                "v1_15_R1",
                "v1_16_R3",
                "v1_17_R1"
        )) {
            System.out.println(thisVersion + " -> " + compareTo(thisVersion, version));
        }
    }

    /**
     *
     * @param thisVersion 所需版本
     * @param version2 当前版本
     * @return 1 和 0 代表兼容 -1 不兼容
     */
    public static int compareTo(String thisVersion, String version2) {
        Matcher VERSION_MATCHER = pattern.matcher(thisVersion);
        VERSION_MATCHER.matches();
        int[] thisVersionSplit = IntStream.range(0, VERSION_MATCHER.groupCount()).map(i -> Integer.parseInt(VERSION_MATCHER.group(i+1))).toArray();
        System.out.println(Arrays.toString(thisVersionSplit));
        Matcher matcher = pattern.matcher(version2);
        if (!matcher.matches()) return 0;
        return IntStream.range(0, 3).map(i -> Integer.compare(thisVersionSplit[i], Integer.parseInt(matcher.group(i+1)))).filter(ct -> ct != 0).findFirst().orElse(0);
    }

    public static void instanceofClass(Object object, Class target) {
        System.out.println(object.getClass() + "\t instanceof:" + target.isAssignableFrom(object.getClass()));
    }

    public static void gsonTest() {
        NBTTagCompound nbtTagCompound = getNBT();
        TagCompound tagCompound = NbtUtil.getInst().asTagCompoundCopy(nbtTagCompound);

        JsonParser jsonParser = new JsonParser();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(jsonParser.parse(tagCompound.toString()).getAsJsonObject()));
        System.out.println("----------");
        System.out.println("[SX_VALUE] " + tagCompound.getValue());
        System.out.println("[SX_STRING] " + tagCompound);
    }

    public static void regexTest() {
        List<String> list = Arrays.asList(
                "[DELAY]",
                "[DELAY] 1",
                "[CONSOLE] /sxi nbt",
                "[CHAT]233l233"
        );

        Pattern pattern = Pattern.compile("^\\[(.*?)] *(.+)");

        for (String str : list) {
            System.out.println();
            System.out.println("-----> " + str);
            Matcher matcher = pattern.matcher(str);
            StringBuilder print = new StringBuilder("  ");
            if (matcher.find()) {
                print.append("find-").append(matcher.groupCount()).append(": ");
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    if (i != 1) print.append(" + ");
                    print.append('"').append(matcher.group(i)).append('"');
                }
            } else {
                print.append("no-find: ").append(str);
            }
            System.out.println(print);
        }
    }

    public static NBTTagCompound getNBT() {
        NBTTagList subTagList1 = new NBTTagList();
        subTagList1.add(NBTTagString.a("TagStringTest1"));
        subTagList1.add(NBTTagString.a("TagStringTest2"));

        NBTTagList subTagList2 = new NBTTagList();
        subTagList2.add(NBTTagString.a("TagStringTest3"));
        subTagList2.add(NBTTagString.a("TagStringTest4"));

        NBTTagByteArray nbtByteArray = new NBTTagByteArray(new byte[0]);
        nbtByteArray.add(NBTTagByte.a((byte) 4));
        nbtByteArray.add(NBTTagByte.a((byte) 3));
        NBTTagIntArray nbtTagIntArray = new NBTTagIntArray(new int[0]);
        nbtTagIntArray.add(NBTTagInt.a(4));
        nbtTagIntArray.add(NBTTagInt.a(3));
        NBTTagLongArray nbtTagLongArray = new NBTTagLongArray(new long[0]);
        nbtTagLongArray.add(NBTTagLong.a(4000L));
        nbtTagLongArray.add(NBTTagLong.a(2000L));
        NBTTagCompound nbtSubCompound = new NBTTagCompound();
        nbtSubCompound.set("test1", subTagList1);
        nbtSubCompound.set("test2", subTagList2);

        NBTTagCompound nbtTagCompound = new NBTTagCompound();

        nbtTagCompound.set("byteArray", nbtByteArray);
        nbtTagCompound.set("tagIntArray", nbtTagIntArray);
        nbtTagCompound.set("tagLongArray", nbtTagLongArray);
        nbtTagCompound.set("sub", nbtSubCompound);

        nbtTagCompound.set("tagByte", NBTTagByte.a((byte) 6));
        nbtTagCompound.set("tagInt", NBTTagInt.a(23));
        nbtTagCompound.set("tagLong", NBTTagLong.a(40));
        nbtTagCompound.set("tagFloat", NBTTagFloat.a(2.5f));
        nbtTagCompound.set("tagShort", NBTTagShort.a((short) 4));
        nbtTagCompound.set("tagDouble", NBTTagDouble.a(6.6));

        return nbtTagCompound;
    }

    @SneakyThrows
    public static void conversionNBT() {

        NBTTagCompound nbtTagCompound = getNBT();
        System.out.println("[DEFAULT] " + nbtTagCompound);

        TagCompound tagBase;
        NBTTagCompound nbtBase;
        //nmsNBT转sxNBT
        tagBase = NbtUtil.getInst().toTag(nbtTagCompound);
        System.out.println("[NMS->SX] " + tagBase);

        //sxNBT转nmsNBT
        nbtBase = NbtUtil.getInst().toNMS(tagBase);
        System.out.println("[SX->NMS] " + nbtBase);

        //sxNBT 转 stream 转 nmsNBT
        nbtBase = NbtUtil.getInst().asNMSCompoundCopy(tagBase);
        System.out.println("[STREAM->NMS] " + nbtBase);

        //nmsNBT 转 stream 转 sxNBT
        tagBase = NbtUtil.getInst().asTagCompoundCopy(nbtTagCompound);
        System.out.println("[STREAM->SX] " + tagBase);

        //nmsStr转nmsNBT
        NBTTagCompound parseTagCompound = MojangsonParser.parse(nbtTagCompound.toString());
        System.out.println("[NMS_STR->NMS] " + parseTagCompound);

        //sxStr转nmsNBT
        parseTagCompound = MojangsonParser.parse(tagBase.toString());
        System.out.println("[SX_STR->NMS] " + parseTagCompound);
    }

    public static String replaceInt(String key, RandomDocker docker) {
        String[] strSplit = key.split("_");
        if (strSplit.length > 1) {
            int[] ints = {Integer.parseInt(strSplit[0]), Integer.parseInt(strSplit[1])};
            Arrays.sort(ints);
            return String.valueOf(ints[0] != ints[1] ? SXItem.getRandom().nextInt(1 + ints[1] - ints[0]) + ints[0] : ints[0]);
        }
        return null;
    }

    public static String replaceDouble(String key, RandomDocker docker) {
        String[] strSplit = key.split("_");
        if (strSplit.length > 1) {
            double[] doubles = {Double.parseDouble(strSplit[0]), Double.parseDouble(strSplit[1])};
            Arrays.sort(doubles);
            return SXItem.getDf().format(doubles[0] != doubles[1] ? SXItem.getRandom().nextDouble() * (doubles[1] - doubles[0]) + doubles[0] : doubles[0]);
        }
        return null;
    }

    /**
     * lore变量识别
     */
    public static void distinguish() {
        String str = "<s><><s:DefaultPrefix>&c炎之洗礼<s:DefaultSuffix> <s:<l:品质>Color><l:品质>";
        StrSubstitutor ss = new StrSubstitutor(new CustomLookup());
        ss.setVariablePrefix("<");
        ss.setVariableSuffix('>');
        ss.setEnableSubstitutionInVariables(true);
        String resolvedString = ss.replace(str);
        System.out.println("resolvedString: " + resolvedString);
    }

    /**
     * 加载Random数据
     */
    public static void loadData() {

        // 配置读取的方式
//        File file = new File("./src/main/resources/RandomString/NewRandom.yml");
        File file = new File("./src/main/resources/RandomString/DefaultRandom.yml");

        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        for (String key : yml.getKeys(false)) {
            Object obj = yml.get(key);
            System.out.println("----> " + key + " - " + obj.getClass().getSimpleName());
            //单行 key - v
            if (obj instanceof String) {
                dataMap.put(key, Collections.singletonList(new Tuple<>(1D, obj.toString())));
            }
            // 多行 key - vList
            else if (obj instanceof List) {
                List<Tuple<Double, String>> list = new ArrayList<>();
                Object unitObj = ((List<?>) obj).get(0);
                System.out.println(unitObj.getClass());
                if (unitObj instanceof Map) {
                    List<Map> listMap = (List<Map>) obj;
                    for (Map map : listMap) {
                        System.out.println("?> " + map);
                        list.add(new Tuple<>(Double.valueOf(map.get("rate").toString()), loadDataString(map.get("string"))));
                    }
                } else {
                    // 标记可转化文本并备份
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

                double value = 1;
                double temp;
                double sum = list.stream().mapToDouble(Tuple::a).sum();
                for (int i = list.size() - 1; i >= 0; i--) {
                    Tuple<Double, String> tuple = list.get(i);
                    temp = tuple.a() / sum;
                    tuple.a(value);
                    value -= temp;
                }
                dataMap.put(key, list);
            }
        }
        System.out.println("-------------?");
        for (Map.Entry<String, List<Tuple<Double, String>>> entry : dataMap.entrySet()) {
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


    protected static String get(String key) {
        List<Tuple<Double, String>> data = dataMap.get(key);
        if (data != null) {
            double r = SXItem.getRandom().nextDouble();
            for (Tuple<Double, String> tuple : data) {
                if (r < tuple.a()) {
                    return tuple.b();
                }
            }
        }
        return null;
    }

    static class CustomLookup extends StrLookup {

        static Pattern regex = Pattern.compile("^.:.+?");

        @Override
        public String lookup(String s) {
            if (regex.matcher(s).matches()) {
                if (s.charAt(0) == 's' || s.charAt(0) == 'l') {
                    return get(s.substring(2));
                }
                System.out.println(s.substring(2) + " -> [" + s.charAt(0) + "]");
                return "[" + s.charAt(0) + "]";
            } else {
                System.out.println("异类: " + s);
            }
            return null;
        }
    }

    static class CustomStrMatcher extends StrMatcher {

        @Override
        public int isMatch(char[] chars, int i, int i1, int i2) {
            return 0;
        }
    }

}

