package github.saukiya.sxitem.data.expression;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.random.INode;
import github.saukiya.sxitem.data.random.RandomManager;
import github.saukiya.tools.base.EmptyMap;
import github.saukiya.tools.helper.PlaceholderHelper;
import github.saukiya.tools.nbt.TagBase;
import github.saukiya.tools.nbt.TagCompound;
import github.saukiya.tools.nbt.TagList;
import github.saukiya.tools.nbt.TagString;
import lombok.Getter;
import lombok.val;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrLookup;
import org.apache.commons.lang.text.StrMatcher;
import org.apache.commons.lang.text.StrSubstitutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 表达式处理器 - 用于存放变量, 解析表达式
 * <pre>
 * 特性: 支持PlaceholderAPI, 支持嵌套
 * {@code
 *  val handler = new ExpressionHandler(player);
 *  handle.replace("<i:1_10>");
 *  handle.replace(Arrays.asList("<i:0_%player_level%>", "<c:%player_exp% * <d:0.1_0.5>>"));
 *  handle.replace(object);
 * }</pre>
 * 
 */
public class ExpressionHandler extends StrLookup {

    static final StrMatcher PRE_MATCHER = StrMatcher.charMatcher('<');
    static final StrMatcher SUF_MATCHER = StrMatcher.charMatcher('>');

    @Getter
    static final ExpressionHandler inst = new ExpressionHandler(null, null, EmptyMap.emptyMap());

    final StrSubstitutor ss = new StrSubstitutor(this, PRE_MATCHER, SUF_MATCHER, StrSubstitutor.DEFAULT_ESCAPE);

    /**
     * 玩家 用来调用placeholderAPI
     */
    @Getter
    @Nullable
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

    public ExpressionHandler() {
        this(null, null, new HashMap<>());
    }

    public ExpressionHandler(Player player) {
        this(player, null, new HashMap<>());
    }

    public ExpressionHandler(Player player, Map<String, INode> localMap) {
        this(player, localMap, new HashMap<>());
    }

    protected ExpressionHandler(Player player, Map<String, INode> localMap, Map<String, String> lockMap) {
        this.player = player;
        this.localMap = localMap;
        this.lockMap = lockMap;
        this.ss.setEnableSubstitutionInVariables(true);
    }

    /**
     * 替换文本内的表达式
     *
     * @param string Text
     */
    public String replace(String string) {
        return PlaceholderHelper.setPlaceholders(getPlayer(), ss.replace(string));
    }

    /**
     * 替换列表内的表达式
     *
     * @param list TextList
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
     * 替换sxNBT内的表达式
     *
     * @param tagBase TagBase
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
     * 替换基础类型的表达式
     *
     * @param object ConfigurationSection/List/Map/String/base
     */
    @SuppressWarnings({"unchecked"})
    public Object replace(Object object) {
        if (object == null) return null;
        if (object instanceof ConfigurationSection) {
            return replace(((ConfigurationSection) object).getValues(false));
        } else if (object instanceof Map) {
            val value = (Map<Object, Object>) object;
            val map = new HashMap<>();
            for (Map.Entry<?, ? super Object> entry : value.entrySet()) {
                map.put(entry.getKey(), replace(entry.getValue()));
            }
            return map;
        } else if (object instanceof List) {
            val value = (List<Object>) object;
            val list = new ArrayList<>();
            for (Object o : value) {
                list.add(replace(o));
            }
            return list;
        } else if (object instanceof String) {
            val value = (String) object;
            val result = replace(value);
            if (StringUtils.isEmpty(result) || result.charAt(0) != '[') return result;

            int last = result.indexOf(']');
            if (last == -1) return result;
            val type = result.substring(1, last);
            val caseResult = result.substring(last + 1);
            switch (type) {
                case "int":
                    return Integer.valueOf(caseResult);
                case "byte":
                    return Byte.valueOf(caseResult);
                case "short":
                    return Short.valueOf(caseResult);
                case "long":
                    return Long.valueOf(caseResult);
                case "float":
                    return Float.valueOf(caseResult);
                case "double":
                    return Double.valueOf(caseResult);
            }
            return result;
        }
        return object;
    }

    /**
     * 获取局部以及全局随机数据 (这不是replace)
     *
     * @param key RandomKey
     * @return RandomValue
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
        return null;
    }
}
