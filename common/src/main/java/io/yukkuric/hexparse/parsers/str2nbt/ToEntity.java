package io.yukkuric.hexparse.parsers.str2nbt;

import at.petrak.hexcasting.api.casting.iota.*;
import at.petrak.hexcasting.api.casting.mishaps.MishapOthersName;
import io.yukkuric.hexparse.HexParse;
import io.yukkuric.hexparse.parsers.IPlayerBinder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ToEntity extends BaseConstParser.Prefix implements IPlayerBinder {
    ServerPlayer self;
    ServerLevel level;

    public ToEntity() {
        super("entity");
    }

    @Override
    public void BindPlayer(@NotNull ServerPlayer p) {
        self = p;
        level = p.serverLevel();
    }

    @Override
    public Iota parse(String node) {
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
            var msg = HexParse.doTranslate("hexcasting.mishap.others_name", entity.getName());
            throw new RuntimeException(msg);
        } catch (Exception e) {
            res = NullIota.INSTANCE;
        }
        return res;
    }
}
