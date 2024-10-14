//package github.saukiya.test;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.JsonParser;
//import github.saukiya.util.nbt.TagCompound;
//import github.saukiya.util.nbt.TagList;
//import github.saukiya.util.nbt.TagLong;
//import github.saukiya.util.nms.NbtUtil;
//import lombok.SneakyThrows;
//import net.minecraft.nbt.*;
//
//import java.util.Arrays;
//
//public class TestNBT {
//
//    public static NBTTagCompound getNBT() {
//        NBTTagList subTagList1 = new NBTTagList();
//        subTagList1.add(NBTTagString.a("TagStringTest1"));
//        subTagList1.add(NBTTagString.a("TagStringTest2"));
//
//        NBTTagList subTagList2 = new NBTTagList();
//        subTagList2.add(NBTTagString.a("TagStringTest3"));
//        subTagList2.add(NBTTagString.a("TagStringTest4"));
//
//        NBTTagByteArray nbtByteArray = new NBTTagByteArray(new byte[0]);
//        nbtByteArray.add(NBTTagByte.a((byte) 4));
//        nbtByteArray.add(NBTTagByte.a((byte) 3));
//        NBTTagIntArray nbtTagIntArray = new NBTTagIntArray(new int[0]);
//        nbtTagIntArray.add(NBTTagInt.a(4));
//        nbtTagIntArray.add(NBTTagInt.a(3));
//        NBTTagLongArray nbtTagLongArray = new NBTTagLongArray(new long[0]);
//        nbtTagLongArray.add(NBTTagLong.a(4000L));
//        nbtTagLongArray.add(NBTTagLong.a(2000L));
//        NBTTagCompound nbtSubCompound = new NBTTagCompound();
//        nbtSubCompound.a("test1", subTagList1);
//        nbtSubCompound.a("test2", subTagList2);
//
//        NBTTagCompound nbtTagCompound = new NBTTagCompound();
//
//        nbtTagCompound.a("byteArray", nbtByteArray);
//        nbtTagCompound.a("tagIntArray", nbtTagIntArray);
//        nbtTagCompound.a("tagLongArray", nbtTagLongArray);
//        nbtTagCompound.a("sub", nbtSubCompound);
//
//        nbtTagCompound.a("tagByte", NBTTagByte.a((byte) 6));
//        nbtTagCompound.a("tagInt", NBTTagInt.a(23));
//        nbtTagCompound.a("tagLong", NBTTagLong.a(40));
//        nbtTagCompound.a("tagFloat", NBTTagFloat.a(2.5f));
//        nbtTagCompound.a("tagShort", NBTTagShort.a((short) 4));
//        nbtTagCompound.a("tagDouble", NBTTagDouble.a(6.6));
//
//        return nbtTagCompound;
//    }
//
//    @SneakyThrows
//    public static void conversionNBT() {
//
//        NBTTagCompound nbtTagCompound = getNBT();
//        System.out.println("[DEFAULT] " + nbtTagCompound);
//
//        TagCompound tagBase;
//        NBTTagCompound nbtBase;
//        //nmsNBT转sxNBT
//        tagBase = (TagCompound) NbtUtil.getInst().toTag(nbtTagCompound);
//        System.out.println("[NMS->SX] " + tagBase);
//
//        //sxNBT转nmsNBT
//        nbtBase = NbtUtil.getInst().toNMS(tagBase);
//        System.out.println("[SX->NMS] " + nbtBase);
//
//        //sxNBT 转 stream 转 nmsNBT
//        nbtBase = NbtUtil.getInst().asNMSCompoundCopy(tagBase);
//        System.out.println("[STREAM->NMS] " + nbtBase);
//
//        //nmsNBT 转 stream 转 sxNBT
//        tagBase = NbtUtil.getInst().asTagCompoundCopy(nbtTagCompound);
//        System.out.println("[STREAM->SX] " + tagBase);
//
//        //nmsStr转nmsNBT
//        NBTTagCompound parseTagCompound = MojangsonParser.a(nbtTagCompound.toString());
//        System.out.println("[NMS_STR->NMS] " + parseTagCompound);
//
//        //sxStr转nmsNBT
//        parseTagCompound = MojangsonParser.a(tagBase.toString());
//        System.out.println("[SX_STR->NMS] " + parseTagCompound);
//    }
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
//
//    public static void yamlTagTest() throws Exception {
//        NBTTagList tagList = new NBTTagList();
//        tagList.add(NBTTagByte.a(true));
//        tagList.add(NBTTagByte.a(true));
//        tagList.add(NBTTagByte.a(false));
//        tagList.add(NBTTagByte.a(true));
//        NBTTagByteArray bytes = new NBTTagByteArray(new byte[]{1, 3, 5, 6});
//        NBTTagCompound nbtTagCompound = new NBTTagCompound();
//        nbtTagCompound.a("tagList", tagList);
//        nbtTagCompound.a("bytes", bytes);
//        nbtTagCompound.a("byte", NBTTagByte.a(false));
//        System.out.println(nbtTagCompound);
//        NBTTagCompound compound = new NBTTagCompound();
//        compound.a("tag.sub", NBTTagString.a("Test"));
//        System.out.println("compound: " + compound);
//        System.out.println("compound-tag.sub: " + compound.c("tag.sub"));
//
//        TagCompound tagCompound = new TagCompound();
//        tagCompound.set("tag.List", new TagList(Arrays.asList(
//                new TagLong(1),
//                new TagLong(1),
//                new TagLong(1),
//                new TagLong(1)
//        )));
//
//        System.out.println("sxToString: " + tagCompound);
//        System.out.println("sxStr -> nms: " + NbtUtil.getInst().parseNMSCompound(tagCompound.toString()).toString());
//        System.out.println("sx -> nms: " + NbtUtil.getInst().asNMSCompoundCopy(tagCompound).toString());
//    }
//
//    public static void gsonTest() {
//        NBTTagCompound nbtTagCompound = getNBT();
//        TagCompound tagCompound = NbtUtil.getInst().asTagCompoundCopy(nbtTagCompound);
//
//        JsonParser jsonParser = new JsonParser();
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        System.out.println(gson.toJson(jsonParser.parse(tagCompound.toString()).getAsJsonObject()));
//        System.out.println("----------");
//        System.out.println("[SX_VALUE] " + tagCompound.getValue());
//        System.out.println("[SX_STRING] " + tagCompound);
//    }
//}
