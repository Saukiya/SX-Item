package github.saukiya.sxitem.data.random;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.nbt.TagBase;
import github.saukiya.sxitem.nbt.TagCompound;
import github.saukiya.sxitem.nbt.TagList;
import github.saukiya.sxitem.nbt.TagString;
import lombok.Getter;
import org.apache.commons.lang.text.StrLookup;
import org.apache.commons.lang.text.StrMatcher;
import org.apache.commons.lang.text.StrSubstitutor;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Random容器
 * 用于存放创建物品时调用的局部变量、lockMap。
 * <p>
 * 流程：
 * 1. new Random Docker
 * 2. docker.setRandom() -> get()
 * 3. docker.getLockMap()
 */
public class RandomDocker extends StrLookup {


    static final StrMatcher PRE_MATCHER = StrMatcher.charMatcher('<');
    static final StrMatcher SUF_MATCHER = StrMatcher.charMatcher('>');

    static final Pattern REGEX = Pattern.compile("^.:.+?");

    @Getter
    static final RandomDocker inst = new RandomDocker();

    static {
        inst.lockMap = null;
    }

    final StrSubstitutor ss = new StrSubstitutor(this, PRE_MATCHER, SUF_MATCHER, StrSubstitutor.DEFAULT_ESCAPE);

    @Getter
    Map<String, String> lockMap = new HashMap<>();
    @Getter
    HashSet<String> lockLog = new HashSet<>();

    Map<String, INode> localMap = null;

    public RandomDocker(Map<String, INode> map) {
        this();
        localMap = map;
    }

    public RandomDocker() {
        ss.setEnableSubstitutionInVariables(true);
    }

    /**
     * 替换文本内的随机占位符
     *
     * @param string Text
     * @return RandomText
     */
    public String replace(String string) {
        return ss.replace(string);
    }

    /**
     * 替换文本内的随机占位符
     *
     * @param list Text
     * @return RandomText
     */
    public List<String> replace(List<String> list) {
        return list.stream().flatMap(str -> Arrays.stream(ss.replace(str).split("\n"))).filter(s -> !s.contains("%DeleteLore%")).collect(Collectors.toList());
    }

    /**
     * 替换NBT内的随机占位符，深拷贝TagCompound、TagList、TagString
     *
     * @param tagBase TagBase
     * @return RandomTag
     */
    public TagBase replace(TagBase tagBase) {
        if (tagBase == null) return tagBase;
        switch (tagBase.getTypeId()) {
            case COMPOUND:
                TagCompound tagCompound = new TagCompound();
                ((TagCompound) tagBase).entrySet().forEach(entry -> tagCompound.setTagBase(entry.getKey(), replace(entry.getValue())));
                return tagCompound;
            case LIST:
                TagList tagList = new TagList();
                ((TagList) tagBase).forEach((v) -> tagList.add(replace(v)));
                return tagList;
            case STRING:
                return new TagString(replace(((TagString) tagBase).getValue()));
            default:
                return tagBase;
        }
    }

    /**
     * 获取局部以及全局随机数据
     *
     * @param key Key
     * @return RandomString
     */
    public String random(String key) {
        String str = RandomManager.random(key, localMap);
        if (str != null) return str;
        return SXItem.getRandomManager().random(key);
    }

    @Override
    public String lookup(String s) {
        if (REGEX.matcher(s).matches()) {
            IRandom random = SXItem.getRandomManager().getRandoms().get(s.charAt(0));
            if (random != null) {
                String str = random.replace(s.substring(2), this);
                return str != null ? str : "%DeleteLore%";
            }
            SXItem.getInst().getLogger().warning("No Random Type: " + s.charAt(0));
        }
        return null;
    }
}
