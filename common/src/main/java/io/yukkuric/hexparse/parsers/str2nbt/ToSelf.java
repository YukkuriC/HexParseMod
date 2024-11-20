package io.yukkuric.hexparse.parsers.str2nbt;

import at.petrak.hexcasting.api.spell.iota.EntityIota;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import io.yukkuric.hexparse.parsers.IPlayerBinder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class ToSelf implements IStr2Nbt, IPlayerBinder {
    Player owner;

    @Override
    public void BindPlayer(Player p) {
        this.owner = p;
    }

    @Override
    public boolean match(String node) {
        node = node.toLowerCase();
        return node.equals("self") || node.equals("myself");
    }

    @Override
    public CompoundTag parse(String node) {
        return HexIotaTypes.serialize(new EntityIota(owner));
    }
}
