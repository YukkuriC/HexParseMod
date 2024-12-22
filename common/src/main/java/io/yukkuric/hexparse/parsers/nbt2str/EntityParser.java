package io.yukkuric.hexparse.parsers.nbt2str;

import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import io.yukkuric.hexparse.parsers.IPlayerBinder;
import io.yukkuric.hexparse.parsers.IotaFactory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public class EntityParser implements INbt2Str, IPlayerBinder {
    @Override
    public boolean match(CompoundTag node) {
        return isType(node, IotaFactory.TYPE_ENTITY);
    }

    @Override
    public String parse(CompoundTag node) {
        var uuid = node.getCompound(HexIotaTypes.KEY_DATA).getUUID("uuid");
        if (self.getUUID().equals(uuid)) return "self";
        return "entity_" + uuid;
    }

    ServerPlayer self;

    @Override
    public void BindPlayer(ServerPlayer p) {
        self = p;
    }
}
