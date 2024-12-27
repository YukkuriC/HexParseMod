package io.yukkuric.hexparse.parsers.str2nbt;

import at.petrak.hexcasting.api.spell.iota.EntityIota;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.NullIota;
import at.petrak.hexcasting.api.spell.mishaps.MishapOthersName;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import io.yukkuric.hexparse.parsers.IPlayerBinder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class ToEntity extends BaseConstParser.Prefix implements IPlayerBinder {
    ServerPlayer self;
    ServerLevel level;

    public ToEntity() {
        super("entity");
    }

    @Override
    public void BindPlayer(ServerPlayer p) {
        self = p;
        level = p.getLevel();
    }

    @Override
    public CompoundTag parse(String node) {
        Iota res;
        Entity entity = null;
        try {
            var uuid = UUID.fromString(node.substring(7));
            entity = level.getEntity(uuid);
            if (entity instanceof Player p && !self.equals(p)) {
                throw new MishapOthersName(p);
            }
            res = new EntityIota(entity);
        } catch (MishapOthersName e) {
            var msg = Component.translatable("hexcasting.mishap.others_name", entity.getName()).getString();
            throw new RuntimeException(msg);
        } catch (Exception e) {
            res = new NullIota();
        }
        return HexIotaTypes.serialize(res);
    }
}
