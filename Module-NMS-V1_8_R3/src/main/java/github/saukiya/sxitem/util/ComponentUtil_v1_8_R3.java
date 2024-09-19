package github.saukiya.sxitem.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import github.saukiya.sxitem.nbt.TagCompound;

import java.util.Collections;

public class ComponentUtil_v1_8_R3 extends ComponentUtil {

    @Override
    public Object getDataComponentMap(Object nmsCopyItem) {
        return null;
    }

    @Override
    public Object getDataComponentPatch(Object nmsCopyItem) {
        return null;
    }

    @Override
    public void setDataComponentMap(Object nmsCopyItem, Object dataComponentMap) {

    }

    @Override
    public void setDataComponentPatch(Object nmsCopyItem, Object dataComponentPatch) {

    }

    @Override
    public JsonElement mapToJson(Object dataComponentMap) {
        return new JsonObject();
    }

    @Override
    public JsonElement patchToJson(Object dataComponentPatch) {
        return new JsonObject();
    }

    @Override
    public Object jsonToMap(JsonElement jsonElement) {
        return null;
    }

    @Override
    public Object jsonToPatch(JsonElement jsonElement) {
        return null;
    }

    @Override
    public Object mapToNBT(Object dataComponentMap) {
        return null;
    }

    @Override
    public Object patchToNBT(Object dataComponentPatch) {
        return NbtUtil.getInst().asNMSCompoundCopy(new TagCompound());
    }

    @Override
    public Object nbtToMap(Object nbtTagCompound) {
        return NbtUtil.getInst().asNMSCompoundCopy(new TagCompound());
    }

    @Override
    public Object nbtToPatch(Object nbtTagCompound) {
        return null;
    }

    @Override
    public Object mapToValue(Object dataComponentMap) {
        return Collections.emptyMap();
    }

    @Override
    public Object patchToValue(Object dataComponentPatch) {
        return Collections.emptyMap();
    }

    @Override
    public Object valueToMap(Object javaObject) {
        return null;
    }

    @Override
    public Object valueToPach(Object javaObject) {
        return null;
    }
}
