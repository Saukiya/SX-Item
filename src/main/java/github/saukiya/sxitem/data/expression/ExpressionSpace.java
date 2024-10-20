package github.saukiya.sxitem.data.expression;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.random.INode;
import github.saukiya.sxitem.data.random.RandomManager;
import github.saukiya.util.base.EmptyMap;
import github.saukiya.util.helper.PlaceholderHelper;
import github.saukiya.util.nbt.TagBase;
import github.saukiya.util.nbt.TagCompound;
import github.saukiya.util.nbt.TagList;
import github.saukiya.util.nbt.TagString;
import lombok.Getter;
import lombok.val;
import org.apache.commons.lang.text.StrLookup;
import org.apache.commons.lang.text.StrMatcher;
import org.apache.commons.lang.text.StrSubstitutor;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Random容器
 * 用于存放创建物品时调用的局部变量、lockMap。
 * <p>
 * 流程：
 * 1. new RandomDocker(map, player).replace("");
 */
public class ExpressionSpace extends StrLookup {

    static final StrMatcher PRE_MATCHER = StrMatcher.charMatcher('<');
    static final StrMatcher SUF_MATCHER = StrMatcher.charMatcher('>');

    @Getter
    static final ExpressionSpace inst = new ExpressionSpace(null, null, EmptyMap.emptyMap());

    final StrSubstitutor ss = new StrSubstitutor(this, PRE_MATCHER, SUF_MATCHER, StrSubstitutor.DEFAULT_ESCAPE);

    /**
     * 玩家 用来调用placeholderAPI
     */
    @Getter
    final Player player;

    /**
     * 局部变量 物品配置表内的
     */
    @Getter
    @Nullable
    final Map<String, INode> localMap;

    /**
     * 永久变量
     */
    @Getter
    @Nonnull
    final Map<String, String> lockMap;

    /**
     * 其他变量
     */
    @Getter
    final Map<String, String> otherMap = new HashMap<>();

    public ExpressionSpace() {
        this(null, null, new HashMap<>());
    }

    public ExpressionSpace(Player player, Map<String, INode> localMap) {
        this(player, localMap, new HashMap<>());
    }

    protected ExpressionSpace(Player player, Map<String, INode> localMap, Map<String, String> lockMap) {
        this.player = player;
        this.localMap = localMap;
        this.lockMap = lockMap;
        this.ss.setEnableSubstitutionInVariables(true);
    }

    /**
     * 替换文本内的表达式
     *
     * @param string Text
     * @return RandomText
     */
    public String replace(String string) {
        return PlaceholderHelper.setPlaceholders(getPlayer(), ss.replace(string));
    }

    /**
     * 替换文本内的表达式
     *
     * @param list Text
     * @return RandomText
     */
    public List<String> replace(List<String> list) {
        val result = new ArrayList<String>();
        for (String str : list) {
            str = PlaceholderHelper.setPlaceholders(getPlayer(), ss.replace(str));
            if (str.indexOf('\n') != -1) {
                String[] split = str.split("\n");
                for (String s : split) {
                    if (!s.contains("%DeleteLore%")) {
                        result.add(s);
                    }
                }
            } else if (!str.contains("%DeleteLore%")) {
                result.add(str);
            }
        }
        return result;
    }

    /**
     * 替换NBT内的表达式，深拷贝TagCompound、TagList、TagString
     *
     * @param tagBase TagBase
     * @return RandomTag
     */
    public TagBase replace(TagBase tagBase) {
        if (tagBase == null) return null;
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
        String str;
        str = otherMap.get(key);
        if (str != null) return str;
        str = RandomManager.random(key, localMap);
        if (str != null) return str;
        return SXItem.getRandomManager().random(key);
    }

    @Override
    public String lookup(String str) {
        int index = str.indexOf(':');
        if (index != -1) {
            IExpression random = ExpressionManager.get(str.substring(0, index));
            if (random != null) {
                int placeholderIndex = str.indexOf('%');
                if (placeholderIndex != -1 && placeholderIndex != str.lastIndexOf('%')) {
                    str = PlaceholderHelper.setPlaceholders(player, str);
                }
                str = random.replace(str.substring(index + 1), this);
                return str != null ? str : "%DeleteLore%";
            }
            SXItem.getInst().getLogger().warning("No Random Type: " + str.substring(0, index));
        }
        return str;
    }
}
