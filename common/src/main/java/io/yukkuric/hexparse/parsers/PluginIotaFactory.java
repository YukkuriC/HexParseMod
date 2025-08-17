package io.yukkuric.hexparse.parsers;

import io.yukkuric.hexparse.HexParse;
import net.minecraft.nbt.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class PluginIotaFactory extends IotaFactory {
    public static final String TYPE_IOTA_TYPE = "hexal:iota_type";
    public static final String TYPE_ENTITY_TYPE = "hexal:entity_type";
    public static final String TYPE_ITEM_TYPE = "hexal:item_type";
    public static final String TYPE_STRING = "moreiotas:string";
    public static final String TYPE_MATRIX = "moreiotas:matrix";
    public static final String TYPE_GATE = "hexal:gate";
    public static final String TYPE_MOTE = "hexal:item";
    public static final String TYPE_PROP = "hexcellular:property";

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

    public static CompoundTag makeMatrix(String[] raw) {
        var body = new CompoundTag();
        int ptr = 1, // (mat_)...
                targetSize = 3;

        // pre-check size 1
        if (raw.length < targetSize)
            throw new IllegalArgumentException(HexParse.doTranslate("hexparse.msg.error.matrix.data_amount", raw.length, targetSize));

        // row & col
        int nrow, ncol;
        try {
            nrow = Integer.parseInt(raw[ptr]);
            if (nrow <= 0)
                throw new IllegalArgumentException(HexParse.doTranslate("hexparse.msg.error.matrix.size", nrow));
            ptr++;
            body.putInt("rows", nrow);
            ncol = Integer.parseInt(raw[ptr]);
            if (ncol <= 0)
                throw new IllegalArgumentException(HexParse.doTranslate("hexparse.msg.error.matrix.size", ncol));
            ptr++;
            body.putInt("cols", ncol);
        } catch (Throwable e) {
            throw new IllegalArgumentException(HexParse.doTranslate("hexparse.msg.error.matrix.size", raw[ptr]));
        }

        // pre-check data length 2
        targetSize = nrow * ncol;
        if (raw.length - 3 < targetSize)
            throw new IllegalArgumentException(HexParse.doTranslate("hexparse.msg.error.matrix.data_amount", raw.length - 3, targetSize));

        // data
        var data = new ListTag();
        try {
            for (var i = 0; i < nrow; i++) {
                var row = new ListTag();
                for (var j = 0; j < ncol; j++) {
                    row.add(DoubleTag.valueOf(Double.parseDouble(raw[ptr])));
                    ptr++;
                }
                data.add(row);
            }
        } catch (Throwable e) {
            throw new IllegalArgumentException(HexParse.doTranslate("hexparse.msg.error.matrix.value", raw[ptr]));
        }
        body.put("mat", data);

        return makeType(TYPE_MATRIX, body);
    }

    public static CompoundTag makeProperty(CompoundTag packed) {
        return makeType(TYPE_PROP, packed);
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
