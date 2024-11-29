package io.yukkuric.hexparse.parsers.str2nbt;

import at.petrak.hexcasting.api.casting.iota.EntityIota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import io.yukkuric.hexparse.parsers.IPlayerBinder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class ToSelf implements IStr2Nbt, IPlayerBinder {
    Player owner;

    @Override
    public void BindPlayer(ServerPlayer p) {
        this.owner = p;
    }

    @Override
    public boolean match(String node) {
        node = node.toLowerCase();
        return node.equals("self") || node.equals("myself");
    }

    @Override
    public CompoundTag parse(String node) {
        return IotaType.serialize(new EntityIota(owner));
    }
}
