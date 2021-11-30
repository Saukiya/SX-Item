package github.saukiya.test;


import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.random.RandomDocker;
import github.saukiya.sxitem.nms.TagBase;
import github.saukiya.sxitem.nms.TagCompound;
import github.saukiya.sxitem.util.NbtUtil;
import github.saukiya.sxitem.util.NbtUtil_v1_17_R1;
import github.saukiya.sxitem.util.Tuple;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import lombok.SneakyThrows;
import net.minecraft.nbt.*;
import org.apache.commons.lang.text.StrLookup;
import org.apache.commons.lang.text.StrMatcher;
import org.apache.commons.lang.text.StrSubstitutor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

public class Test {

    static Map<String, List<Tuple<Double, String>>> dataMap = new HashMap();

    @SneakyThrows
    public static void main(String[] args) {
        //原版 ------------------------------------------------------------------

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

        nbtTagCompound.set("end", NBTTagEnd.b);
        nbtTagCompound.set("tagFloat", NBTTagFloat.a(2.5f));
        nbtTagCompound.set("tagShort", NBTTagShort.a((short) 4));
        nbtTagCompound.set("tagDouble", NBTTagDouble.a(6.6));

        //nmsNBT 转 stream
        ByteBuf buf = Unpooled.buffer();
        NBTCompressedStreamTools.a(nbtTagCompound, (DataOutput) new ByteBufOutputStream(buf));

        //stream 转 sxNBT
        TagBase tagBase = NbtUtil.getInst().readTagBase(new ByteBufInputStream(buf));
        System.out.println("stream 转 sxNBT\n" + tagBase);

        //sxNBT 转 stream
        buf = Unpooled.buffer();
        NbtUtil.getInst().writeTagBase(tagBase, new ByteBufOutputStream(buf));

        //stream 转 nmsNBT
        NBTTagCompound streamNMS = NBTCompressedStreamTools.a((DataInput) new DataInputStream(new ByteBufInputStream(buf)));
        System.out.println("stream 转 nmsNBT\n" + streamNMS);
    }

    @SneakyThrows
    public static void conversionNBT() {
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

        nbtTagCompound.set("end", NBTTagEnd.b);
        nbtTagCompound.set("tagFloat", NBTTagFloat.a(2.5f));
        nbtTagCompound.set("tagShort", NBTTagShort.a((short) 4));
        nbtTagCompound.set("tagDouble", NBTTagDouble.a(6.6));

        System.out.println("[DEFAULT] " + nbtTagCompound);

        TagCompound tagCompound = NbtUtil.getInst().asTagCopy(nbtTagCompound);
        System.out.println("[NMS->SX] " + tagCompound);
        System.out.println("[SX->VALUE] " + tagCompound.getValue());

        TagBase tagBase = new NbtUtil_v1_17_R1().asTagCopyTest(nbtTagCompound, null);
        System.out.println("[NMS->TEST] " + tagBase);

        NBTBase nbtBase = NbtUtil.getInst().asNMSCopy(tagCompound);
        System.out.println("[SX->NMS] " + nbtBase);

        //String转NBT方法
        NBTTagCompound parseTagCompound = MojangsonParser.parse(nbtTagCompound.toString());
        System.out.println("[MP->NMS] " + parseTagCompound);
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
                dataMap.put(key, Collections.singletonList(new Tuple<>(1D,obj.toString())));
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
                else {
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

