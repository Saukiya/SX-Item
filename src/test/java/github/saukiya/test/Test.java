package github.saukiya.test;


import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.random.RandomDocker;
import github.saukiya.sxitem.nbt.TagBase;
import github.saukiya.sxitem.nbt.TagCompound;
import github.saukiya.sxitem.nbt.TagType;
import github.saukiya.sxitem.util.NMS;
import github.saukiya.sxitem.util.NbtUtil;
import github.saukiya.sxitem.util.Tuple;
import lombok.SneakyThrows;
import net.minecraft.nbt.*;
import org.apache.commons.lang.text.StrLookup;
import org.apache.commons.lang.text.StrMatcher;
import org.apache.commons.lang.text.StrSubstitutor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Test {
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    static Map<String, List<Tuple<Double, String>>> dataMap = new HashMap();
    static Pattern pattern = Pattern.compile("v(\\d+)_(\\d+)_R(\\d+)");
    static Pattern time_pattern = Pattern.compile("\\d+");

    static JsonParser JSON_PARSER = new JsonParser();
    static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    static String keyString =
            "test.test\n" +
                    "test.double\n" +
                    "test.sub.add\n" +
                    "test.sub.remove";

    public static void main(String[] args) throws Exception {
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("%DeleteLore%");
        list.add("%DeleteLore");
        list.add("%DeleteLore %deletelore");
        list.add("4");
        list = list.stream().flatMap(str -> Arrays.stream(str.split("\n"))).filter(s -> !s.contains("%DeleteLore") && !s.contains("%deletelore")).collect(Collectors.toList());
        System.out.println(list);
//        TagCompound tagCompound = new TagCompound();
//        tagCompound.set("233.233", 2333L);
//        tagCompound.set("base", "qwq");
//        tagCompound.set("array1", new int[]{1, 2, 3, 4, 5});
//        tagCompound.set("array2", Arrays.asList(1, 2, 3, 4));
//        tagCompound.set("array3", Arrays.asList(1L, 24L, 523L, 634L));
//        tagCompound.set("boolean", true);
//        System.out.println(tagCompound);
//        System.out.println(tagCompound.getBoolean("boolean"));

//        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(new File("Z:\\Dev\\Java\\Minecraft\\服务端\\Minecraft - 1.12\\plugins\\SX-Item\\Item\\Default\\Default.yml"));
//        TagCompound tagCompound = (TagCompound) TagType.toTag(yamlConfiguration);

//        getAndSetPathToCompound();
//        yamlToTagTest();
//        gsonTest();
//        conversionNBT();
        checkUpdate();
    }

    public static JsonElement getJsonFromUrl(String url) {
        try (
            InputStream inputStream = new URL(url).openStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            JsonReader jsonReader = new JsonReader(inputStreamReader)
        ) {
            return new Gson().fromJson(jsonReader, JsonElement.class);
        } catch (IOException e) {
            return null;
        }
    }

    public static void checkUpdate() {
        // 从Url中读取Json
        JsonObject json = getJsonFromUrl("https://api.github.com/repos/Saukiya/SX-Item/releases/latest").getAsJsonObject();
        // 版本号(tag号)
        String version = json.get("tag_name").getAsString();
        // 网页链接
        String html = json.get("html_url").getAsString();
        // 版本详情
        String[] desc = json.get("body").getAsString().split("\\r\\n");
        // 资源列表
        JsonArray assets = json.get("assets").getAsJsonArray();
        // 插件下载地址
        String downLoadUrl = IntStream.range(0, assets.size()).mapToObj(i -> assets.get(i).getAsJsonObject()).filter(asset -> asset.get("name").getAsString().endsWith("-all.jar")).findFirst().get().get("browser_download_url").getAsString();

        System.out.println("\n\n\n");
        System.out.println("Version: " + version);
        System.out.println("Html: " + html);
        System.out.println("DownloadUrl: " + downLoadUrl);
        System.out.println("Info: \n\t" + String.join("\n\t", desc));
    }

    public static String c(String... versions) {
        return Arrays.stream(versions).filter(version -> NMS.compareTo(version) >= 0).findFirst().orElse(null);
    }

    public static void print(Class target) {
        System.out.println(target.getPackage().getName() + "." + target.getSimpleName() + "_");
        System.out.println(target.getName() + "_");
    }

    public static Set<String> keySet(String path) {
        if (path == null) path = "";
        Set<String> keys = new HashSet<>();
        if (keyString == null) return keys;
        int length = path.length();
        int temp;
        int first;
        for (String key : keyString.split("\n")) {
            if (key.startsWith(path)) {
                first = length + (key.charAt(length) == '.' ? 1 : 0);
                temp = key.indexOf('.', first);
                keys.add(key.substring(first, temp != -1 ? temp : key.length()));
            } else if (keys.size() != 0) break;
        }
        return keys;
    }

    public static String replace2(String key) {
        if (time_pattern.matcher(key).matches()) {
            return sdf.format(System.currentTimeMillis() + Long.parseLong(key) * 1000);
        } else {
            Calendar calendar = Calendar.getInstance();
            int num = 0;
            for (int i = 0, length = key.length(); i < length; i++) {
                switch (key.charAt(i)) {
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                        num = num * 10 + key.charAt(i) - 48;
                        break;
                    case 'Y':
                    case 'y':
                        calendar.add(Calendar.YEAR, num);
                        num = 0;
                        break;
                    case 'M':
                        calendar.add(Calendar.MONTH, num);
                        num = 0;
                        break;
                    case 'D':
                    case 'd':
                        calendar.add(Calendar.DATE, num);
                        num = 0;
                        break;
                    case 'H':
                    case 'h':
                        calendar.add(Calendar.HOUR_OF_DAY, num);
                        num = 0;
                        break;
                    case 'm':
                        calendar.add(Calendar.MINUTE, num);
                        num = 0;
                        break;
                    case 'S':
                    case 's':
                        calendar.add(Calendar.SECOND, num);
                        num = 0;
                        break;
                }
            }
            return sdf.format(calendar.getTimeInMillis());
        }
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

        NbtUtil nbtUtil = NbtUtil.getInst();
        TagCompound compound = nbtUtil.asTagCompoundCopy(nbtTagCompound);
        System.out.println(nbtTagCompound);
        System.out.println("getCompound: \t" + compound.get("sub")); //正确
        System.out.println("getCompound: \t" + compound.get(".sub")); //null
        System.out.println("getCompound: \t" + compound.get("sub.qwq")); //正确
        System.out.println("getCompound: \t" + compound.get("sub.qwq1")); //null
        System.out.println("getCompound: \t" + compound.get("sub.qwq1.test")); //null
        System.out.println("getCompound: \t" + compound.get("sub.qwq.test")); //正确
        System.out.println("getCompound: \t" + compound.get("sub.qwq.test1")); //null
        System.out.println("getCompound: \t" + compound.get("sub.qwq.test.ints")); //正确
        System.out.println(compound.getValue());
        System.out.println(compound.set("sub.qwq.lala.test", nbtTagCompound));//正确
        System.out.println(compound.get("sub.qwq"));
        System.out.println(compound.set("sub.qwq.b", true));
        System.out.println(compound.getBoolean("sub.qwq.b"));
        System.out.println(compound.getByte("sub.qwq.b"));
        System.out.println(compound.remove("sub.qwq.b"));
        System.out.println(compound.get("sub.qwq.b"));
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

        TagBase tagBase = TagType.toTag(loadYaml);
        System.out.println("sxNBT: \t\t" + tagBase);
        System.out.println("nmsNBT: \t" + NbtUtil.getInst().parseNMSCompound(tagBase.toString()));
        System.out.println("sxNBT-json: \n" + tagBase.toJson());

        NBTTagList tagList = new NBTTagList();
        tagList.add(NBTTagByte.a(true));
        tagList.add(NBTTagByte.a(true));
        tagList.add(NBTTagByte.a(false));
        tagList.add(NBTTagByte.a(true));
        NBTTagByteArray bytes = new NBTTagByteArray(new byte[]{1, 3, 5, 6});
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        nbtTagCompound.set("tagList", tagList);
        nbtTagCompound.set("bytes", bytes);
        nbtTagCompound.set("byte", NBTTagByte.a(false));
        System.out.println(nbtTagCompound);
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
     * @param thisVersion 所需版本
     * @param version2    当前版本
     * @return 1 和 0 代表兼容 -1 不兼容
     */
    public static int compareTo(String thisVersion, String version2) {
        Matcher VERSION_MATCHER = pattern.matcher(thisVersion);
        VERSION_MATCHER.matches();
        int[] thisVersionSplit = IntStream.range(0, VERSION_MATCHER.groupCount()).map(i -> Integer.parseInt(VERSION_MATCHER.group(i + 1))).toArray();
        System.out.println(Arrays.toString(thisVersionSplit));
        Matcher matcher = pattern.matcher(version2);
        if (!matcher.matches()) return 0;
        return IntStream.range(0, 3).map(i -> Integer.compare(thisVersionSplit[i], Integer.parseInt(matcher.group(i + 1)))).filter(ct -> ct != 0).findFirst().orElse(0);
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
//        File file = new File("./src/main/resources/RandomString/Test.yml");
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
        if (obj == null) return "";
        if (obj instanceof List) obj = String.join("\n", (List) obj);
        return obj.toString().replace("/n", "\n").replace("\\n", "\n");
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

