package github.saukiya.sxitem.nms;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import java.io.DataOutput;
import java.io.IOException;


public interface TagBase<T> extends Cloneable {

    JsonParser JSON_PARSER = new JsonParser();

    Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Deprecated
    static TagBase toTag(Object object) {
        return TagType.toTag(object);
    }

    void write(DataOutput dataOutput) throws IOException;

    /**
     * 获取基础数据
     * 例如 List、Map、Array、byte、int 等
     *
     * @return
     */
    T getValue();

    TagType getTypeId();

    /**
     * 将NBT以带制符表的方式显示出来
     * 无法显示带转义(\)的Json
     *
     * @return String
     */
    default String toJson() {
        return GSON.toJson(JSON_PARSER.parse(toString()));
    }
}
