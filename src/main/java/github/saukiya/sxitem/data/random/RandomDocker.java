package github.saukiya.sxitem.data.random;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.util.Tuple;
import lombok.Getter;
import lombok.experimental.PackagePrivate;
import org.apache.commons.lang.text.StrLookup;
import org.apache.commons.lang.text.StrMatcher;
import org.apache.commons.lang.text.StrSubstitutor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Random容器
 * 用于存放创建物品时调用的局部变量、lockMap。
 * <p>
 * 流程：
 * 1. new Random Docker
 * 2. docker.setRandom() -> get()
 * 3. docker.getLockMap()
 */
@PackagePrivate
public class RandomDocker extends StrLookup {

    static final StrMatcher PRE_MATCHER = StrMatcher.charMatcher('<');
    static final StrMatcher SUF_MATCHER = StrMatcher.charMatcher('>');

    static final Pattern REGEX = Pattern.compile("^.:.+?");

    final StrSubstitutor ss = new StrSubstitutor(this, PRE_MATCHER, SUF_MATCHER, StrSubstitutor.DEFAULT_ESCAPE);

    @Getter
    final Map<String, String> lockMap = new HashMap<>();

    Map<String, List<Tuple<Double, String>>> localMap = null;

    public RandomDocker(String local) {
        this();
        localMap = SXItem.getRandomStringManager().localMap.get(local);
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
    public String setRandom(String string) {
        return ss.replace(string);
    }

    /**
     * 获取局部以及全局随机数据
     *
     * @param key Key
     * @return RandomString
     */
    public String get(String key) {
        if (localMap != null) {
            String str = random(localMap.get(key));
            if (str != null) return str;
        }
        return random(SXItem.getRandomStringManager().globalMap.get(key));
    }

    protected String random(List<Tuple<Double, String>> data) {
        if (data != null) {
            if (data.size() == 1) return data.get(0).b();
            double value = SXItem.getRandom().nextDouble();
            return data.stream().filter(tuple -> value < tuple.a()).findFirst().map(Tuple::b).orElse(null);
        }
        return null;
    }

    @Override
    public String lookup(String s) {
        if (REGEX.matcher(s).matches()) {
            IRandom random = SXItem.getRandomStringManager().randoms.get(s.charAt(0));
            if (random != null) {
                return random.replace(s.substring(2), this);
            } else {
                SXItem.getInst().getLogger().warning("No Random Type: " + s.charAt(0));
            }
        }
        return "%DeleteLore%";
    }
}
