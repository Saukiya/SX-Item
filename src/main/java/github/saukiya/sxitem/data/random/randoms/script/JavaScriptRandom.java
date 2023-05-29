package github.saukiya.sxitem.data.random.randoms.script;

import github.saukiya.sxitem.data.random.IRandom;
import github.saukiya.sxitem.data.random.RandomDocker;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class JavaScriptRandom implements IRandom {

    //<j: script#<l:A> <l:B> <l:C>>

    public static void test(Player player) {
        //用法
        try {
            //这个data不一定是String
            Object data = JavaScriptEngine.getInstance().callFunction(player, "SN", "FN", new HashMap<>());

            //不太懂java 瞎写了
            if (data instanceof List) {
                List<String> list = (List<String>) data;
            }
            if (data instanceof String) {

            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String replace(String key, RandomDocker docker) {
        return null;
    }


}
