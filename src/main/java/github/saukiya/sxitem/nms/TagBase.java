package github.saukiya.sxitem.nms;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import java.io.DataOutput;
import java.io.IOException;

public interface TagBase<T> {

    JsonParser JSON_PARSER = new JsonParser();

    Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    void write(DataOutput dataOutput) throws IOException;

    T getValue();

    TagType getTypeId();

    default String toJson() {
        return GSON.toJson(JSON_PARSER.parse(String.valueOf(getValue())));
    }
}
