package io.yukkuric.hexparse.parsers.nbt2str;

import at.petrak.hexcasting.api.casting.iota.EntityIota;
import io.yukkuric.hexparse.parsers.IPlayerBinder;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class EntityParser implements INbt2Str<EntityIota>, IPlayerBinder {
    @Override
    public String parse(EntityIota iota) {
        var uuid = iota.getEntityId();
        if (self.getUUID().equals(uuid)) return "self";
        return "entity_" + uuid;
    }
    @Override
    public Class<EntityIota> getType() {
        return EntityIota.class;
    }

    ServerPlayer self;

    @Override
    public void BindPlayer(@NotNull ServerPlayer p) {
        self = p;
    }
}
