package github.saukiya.test;

import github.saukiya.tools.nbt.TagCompound;
import github.saukiya.tools.nms.NbtUtil;
import lombok.SneakyThrows;
import lombok.val;

import java.util.ArrayList;
import java.util.HashMap;

public class TestNBT {

    public static void main(String[] args) throws Exception {
        System.out.println(getNBT());
        TestNBT.conversionNBT();
//        TestNBT.getAndSetPathToCompound();
        TestNBT.gsonTest();
    }

    /**
     * @see net.minecraft.nbt.NBTTagCompound
     */
    public static Object getNBT() {
        val subTagList1 = new ArrayList<String>();
        subTagList1.add("TagStringTest1");
        subTagList1.add("TagStringTest2");

        val subTagList2 = new ArrayList<Boolean>();
        subTagList2.add(true);
        subTagList2.add(false);

        val nbtByteArray = new byte[]{4, 3};
        val nbtTagIntArray = new int[]{4, 3};
        val nbtTagLongArray = new long[]{4000, 2000};
        val nbtSubCompound = new HashMap<String, Object>();
        nbtSubCompound.put("stringList", subTagList1);
        nbtSubCompound.put("booleanList", subTagList2);

        val nbtTagWrapper = NbtUtil.getInst().createTagWrapper();

        nbtTagWrapper.set("byteArray", nbtByteArray);
        nbtTagWrapper.set("tagIntArray", nbtTagIntArray);
        nbtTagWrapper.set("tagLongArray", nbtTagLongArray);
        nbtTagWrapper.set("sub", nbtSubCompound);

        nbtTagWrapper.set("tagByte", (byte) 6);
        nbtTagWrapper.set("tagInt", 23);
        nbtTagWrapper.set("tagLong", 40L);
        nbtTagWrapper.set("tagFloat", 2.5f);
        nbtTagWrapper.set("tagShort", (short) 4);
        nbtTagWrapper.set("tagDouble", 6.6D);

        return nbtTagWrapper.getHandle();
    }

    @SneakyThrows
    public static void conversionNBT() {

        val nbtTagCompound = getNBT();
        System.out.println("[DEFAULT] " + nbtTagCompound);

        TagCompound tagBase;
        Object nbtBase;
        //nmsNBT转sxNBT
        tagBase = (TagCompound) NbtUtil.getInst().toTag(nbtTagCompound);
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
        Object parseTagCompound = NbtUtil.getInst().parseNMSCompound(nbtTagCompound.toString());
        System.out.println("[NMS_STR->NMS] " + parseTagCompound);

        //sxStr转nmsNBT
        parseTagCompound = NbtUtil.getInst().parseNMSCompound(tagBase.toString());
        System.out.println("[SX_STR->NMS] " + parseTagCompound);
    }
//
//    public static void getAndSetPathToCompound() throws Exception {
//        NBTTagCompound nbtTagCompound = new NBTTagCompound();
//        NBTTagCompound subTagCompound = new NBTTagCompound();
//        NBTTagCompound qwqTagCompound = new NBTTagCompound();
//        NBTTagCompound testTagCompound = new NBTTagCompound();
//        nbtTagCompound.a("sub", subTagCompound);
//        subTagCompound.a("qwq", qwqTagCompound);
//        qwqTagCompound.a("test", testTagCompound);
//        subTagCompound.a("float", NBTTagFloat.a(4f));
//        qwqTagCompound.a("string", NBTTagString.a("测试文本"));
//        testTagCompound.a("ints", new NBTTagIntArray(new int[]{5, 23, 7, 873, 4, 46, 3, 7, 34}));
//
//        NbtUtil nbtUtil = NbtUtil.getInst();
//        TagCompound compound = nbtUtil.asTagCompoundCopy(nbtTagCompound);
//        System.out.println(nbtTagCompound);
//        System.out.println("getCompound: \t" + compound.get("sub")); //正确
//        System.out.println("getCompound: \t" + compound.get(".sub")); //null
//        System.out.println("getCompound: \t" + compound.get("sub.qwq")); //正确
//        System.out.println("getCompound: \t" + compound.get("sub.qwq1")); //null
//        System.out.println("getCompound: \t" + compound.get("sub.qwq1.test")); //null
//        System.out.println("getCompound: \t" + compound.get("sub.qwq.test")); //正确
//        System.out.println("getCompound: \t" + compound.get("sub.qwq.test1")); //null
//        System.out.println("getCompound: \t" + compound.get("sub.qwq.test.ints")); //正确
//        System.out.println(compound.getValue());
//        System.out.println(compound.set("sub.qwq.lala.test", nbtTagCompound));//正确
//        System.out.println(compound.get("sub.qwq"));
//        System.out.println(compound.set("sub.qwq.b", true));
//        System.out.println(compound.getBoolean("sub.qwq.b"));
//        System.out.println(compound.getByte("sub.qwq.b"));
//        System.out.println(compound.remove("sub.qwq.b"));
//        System.out.println(compound.get("sub.qwq.b"));
//    }

    public static void gsonTest() {
        val nbtTagCompound = getNBT();
        TagCompound tagCompound = NbtUtil.getInst().asTagCompoundCopy(nbtTagCompound);

        System.out.println(Test.gson.toJson(Test.jsonParser.parse(tagCompound.toString()).getAsJsonObject()));
        System.out.println("----------");
        System.out.println("[SX_VALUE] " + tagCompound.getValue());
        System.out.println("[SX_STRING] " + tagCompound);
    }
}
