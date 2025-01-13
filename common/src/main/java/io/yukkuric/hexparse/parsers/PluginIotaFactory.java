package io.yukkuric.hexparse.parsers;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class PluginIotaFactory extends IotaFactory {
    // hexal iotas
    public static final String TYPE_IOTA_TYPE = "moreiotas:iota_type";
    public static final String TYPE_ENTITY_TYPE = "moreiotas:entity_type";
    public static final String TYPE_ITEM_TYPE = "moreiotas:item_type";
    public static final String TYPE_STRING = "moreiotas:string";
    public static final String TYPE_GATE = "hexal:gate";

    public static CompoundTag makeIotaType(String type) {
        return makeType(TYPE_IOTA_TYPE, StringTag.valueOf(type));
    }

    public static CompoundTag makeEntityType(String type) {
        return makeType(TYPE_ENTITY_TYPE, StringTag.valueOf(type));
    }

    public static CompoundTag makeItemType(String type, boolean isBlock) {
        var body = new CompoundTag();
        body.putString(isBlock ? "block" : "item", type);
        return makeType(TYPE_ITEM_TYPE, body);
    }

    public static CompoundTag makeItemType(String type) {
        return makeItemType(type, false);
    }

    public static CompoundTag makeBlockType(String type) {
        return makeItemType(type, true);
    }

    public static CompoundTag makeString(String data) {
        return makeType(TYPE_STRING, StringTag.valueOf(data));
    }

    public static CompoundTag makeGate(int id, Vec3 pos, Entity binder) {
        byte type = 0;
        if (binder != null) {
            type = 2;
            if (pos == null) pos = Vec3.ZERO;
        } else if (pos != null) {
            type = 1;
        }
        var payload = new CompoundTag();
        payload.putInt("index", id);
        payload.putByte("target_type", type);
        if (pos != null) {
            payload.putDouble("target_x", pos.x);
            payload.putDouble("target_y", pos.y);
            payload.putDouble("target_z", pos.z);
        }
        if (binder != null) {
            payload.putUUID("target_uuid", binder.getUUID());
            payload.putString("target_name", binder.getName().getString());
        }
        return makeType(TYPE_GATE, payload);
    }
}
