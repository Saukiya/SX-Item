package github.saukiya.test;


import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.expression.ExpressionHandler;
import github.saukiya.tools.base.EmptyMap;
import github.saukiya.tools.base.Tuple;
import github.saukiya.tools.nbt.TagBase;
import github.saukiya.tools.nbt.TagCompound;
import github.saukiya.tools.nbt.TagType;
import github.saukiya.tools.nms.NMS;
import lombok.val;
import org.apache.commons.lang.text.StrLookup;
import org.apache.commons.lang.text.StrMatcher;
import org.apache.commons.lang.text.StrSubstitutor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Test {
    static File projectPath = new File(System.getProperty("user.dir"));
    static File mainResourcePath = new File(projectPath, "src/main/resources");
    static File testResourcePath = new File(projectPath, "src/test/resources");
    static DecimalFormat df = new DecimalFormat("#.##");
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    static Map<String, List<Tuple<Double, String>>> dataMap = new HashMap();
    static Pattern pattern = Pattern.compile("v(\\d+)_(\\d+)_R(\\d+)");
    static Pattern time_pattern = Pattern.compile("\\d+");

    public static JsonParser jsonParser = new JsonParser();
    public static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static Random random = new Random();


    @SuppressWarnings({"SystemGetProperty", "Since15"})
    public static void Environment() throws Exception {
        System.out.println("file.encoding: " + System.getProperty("file.encoding"));
        System.out.println("defaultCharset: " + Charset.defaultCharset());
        System.out.println("system.out: " + System.out.charset());
        ManagementFactory.getRuntimeMXBean().getInputArguments().forEach(arg -> System.out.println("jvm.arg: " + arg));
//        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
    }

    public static void main(String[] args) {
    }

    public static void testEmptyMap() {
        Map<String, String> map = EmptyMap.emptyMap();
        System.out.println(map.get("?"));
        System.out.println(map.putIfAbsent("?", "?"));
        System.out.println(map.computeIfAbsent("?", k -> "?"));
    }

    public static void testMap() {
        long time = 0L;
        for (int i = 0; i < 50000; i++) {
            val v1 = createMap();
            val v2 = createMap();
            long startTime = System.nanoTime();
            testSumMap(v1, v2);
            time += System.nanoTime() - startTime;
        }
        System.out.println("\t耗时: " + (time / 1000000D));
    }

    public static Map<String, Double> createMap() {
        Map<String, Double> result = new HashMap<>();
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < x + 2; y++) {
                result.put(x + "." + y, random.nextDouble() * 10);
            }
        }
        return result;
    }

    public static void testSumMap(Map<String, Double> v1, Map<String, Double> v2) {
        v2.forEach((k, v) -> v1.merge(k, v, Double::sum));
    }

    public static void testArray() {
        long time = 0L;
        for (int i = 0; i < 50000; i++) {
            val v1 = createArray();
            val v2 = createArray();
            long startTime = System.nanoTime();
            testSumArray(v1, v2);
            time += System.nanoTime() - startTime;
        }
        System.out.println("\t耗时: " + (time / 1000000D));
    }

    public static double[][] createArray() {
        val result = new double[10][];
        for (int x = 0; x < result.length; x++) {
            result[x] = new double[x + 2];
            for (int y = 0; y < x + 2; y++) {
                result[x][y] = random.nextDouble() * 10;
            }
        }
        return result;
    }

    public static void testSumArray(double[][] v1, double[][] v2) {
        for (int x = 0, lengthX = v1.length; x < lengthX; x++) {
            val vx = v1[x];
            for (int y = 0, lengthY = vx.length; y < lengthY; y++) {
                vx[y] += v2[x][y];
            }
        }
    }

    public static double getNum(String str, int index) {
        double result = 0D;
        int isDecimal = 0;
        forTag:
        for (int i = index, length = str.length(); i < length; i++) {
            val charAt = str.charAt(i);
            switch (charAt) {
                case '&':
                case '§':
                    i++;
                    continue;
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case '0':
                    if (isDecimal == 0) {
                        result = result * 10 + charAt - 48;
                        continue;
                    }
                    result += (charAt - 48) * Math.pow(10, isDecimal--);
                    continue;
                case '.':
                    isDecimal--;
                    continue;
                default:
                    if (result != 0) break forTag;
            }
        }
        return result;
    }

    public static YamlConfiguration loadYml(String path) {
        return YamlConfiguration.loadConfiguration(new File(mainResourcePath, path));
    }

    public static YamlConfiguration loadYmlTest(String path) {
        return YamlConfiguration.loadConfiguration(new File(testResourcePath, path));
    }

    public static void LoreTest() {
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("%DeleteLore%");
        list.add("%DeleteLore");
        list.add("%DeleteLore %deletelore");
        list.add("4");
        list = list.stream().flatMap(str -> Arrays.stream(str.split("\n"))).filter(s -> !s.contains("%DeleteLore") && !s.contains("%deletelore")).collect(Collectors.toList());
        System.out.println(list);
        TagCompound tagCompound = new TagCompound();
        tagCompound.set("233.233", 2333L);
        tagCompound.set("base", "qwq");
        tagCompound.set("array1", new int[]{1, 2, 3, 4, 5});
        tagCompound.set("array2", Arrays.asList(1, 2, 3, 4));
        tagCompound.set("array3", Arrays.asList(1L, 24L, 523L, 634L));
        tagCompound.set("boolean", true);
        System.out.println(tagCompound);
        System.out.println(tagCompound.getBoolean("boolean"));

        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(new File("Z:\\Dev\\Java\\Minecraft\\服务端\\Minecraft - 1.12\\plugins\\SX-Item\\Item\\Default\\Default.yml"));
        TagType.toTag(yamlConfiguration);
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
        String keyString =
                "test.test\n" +
                        "test.double\n" +
                        "test.sub.add\n" +
                        "test.sub.remove";
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
//        System.out.println("nmsNBT: \t" + NbtUtil.getInst().parseNMSCompound(tagBase.toString()));
//        System.out.println("sxNBT-json: \n" + tagBase.toJson());
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

    public static String replaceInt(String key, ExpressionHandler docker) {
        String[] strSplit = key.split("_");
        if (strSplit.length > 1) {
            int[] ints = {Integer.parseInt(strSplit[0]), Integer.parseInt(strSplit[1])};
            Arrays.sort(ints);
            return String.valueOf(ints[0] != ints[1] ? SXItem.getRandom().nextInt(1 + ints[1] - ints[0]) + ints[0] : ints[0]);
        }
        return null;
    }

    public static String replaceDouble(String key, ExpressionHandler docker) {
        String[] strSplit = key.split("_");
        if (strSplit.length > 1) {
            double[] doubles = {Double.parseDouble(strSplit[0]), Double.parseDouble(strSplit[1])};
            Arrays.sort(doubles);
            return df.format(doubles[0] != doubles[1] ? SXItem.getRandom().nextDouble() * (doubles[1] - doubles[0]) + doubles[0] : doubles[0]);
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

