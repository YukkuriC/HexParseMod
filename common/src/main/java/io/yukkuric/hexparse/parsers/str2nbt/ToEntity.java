package io.yukkuric.hexparse.parsers.str2nbt;

import at.petrak.hexcasting.api.spell.iota.EntityIota;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.NullIota;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import io.yukkuric.hexparse.parsers.IPlayerBinder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class ToEntity extends BaseConstParser.Prefix implements IPlayerBinder {
    ServerLevel level;

    public ToEntity() {
        super("entity");
    }

    @Override
    public void BindPlayer(ServerPlayer p) {
        level = p.getLevel();
    }

    @Override
    public CompoundTag parse(String node) {
        Iota res;
        try {
            var uuid = UUID.fromString(node.substring(7));
            var entity = level.getEntity(uuid);
            res = new EntityIota(entity);
        } catch (Exception e) {
            res = new NullIota();
        }
        return HexIotaTypes.serialize(res);
    }

}
