package io.yukkuric.hexparse.parsers.str2nbt;

import io.yukkuric.hexparse.HexParse;
import io.yukkuric.hexparse.parsers.IPlayerBinder;
import io.yukkuric.hexparse.parsers.PluginIotaFactory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class ToGate extends BaseConstParser.Regex implements IPlayerBinder {
    public ToGate() {
        super("^gate(_?)");
    }

    ServerPlayer self;
    int orderId;

    @Override
    public void BindPlayer(ServerPlayer p) {
        self = p;
        orderId = -1;
    }

    @Override
    public CompoundTag parse(String node) {
        if (node.length() <= 5) return PluginIotaFactory.makeGate(orderId--, null, null);
        var frags = node.substring(5).split("_");
        double[] vecRaw = new double[]{0, 0, 0};
        byte ptr = 0;
        Entity entity = null;
        for (var f : frags) {
            try {
                var tmp = Double.parseDouble(f);
                if (ptr < 3) vecRaw[ptr++] = tmp;
                continue;
            } catch (NumberFormatException e) {
            }
            var ff = f.toLowerCase();
            if (ff.equals("self") || ff.equals("myself")) {
                entity = self;
                continue;
            }
            try {
                var uuid = UUID.fromString(f);
                entity = ((ServerLevel) self.level()).getEntity(uuid);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(HexParse.doTranslate("hexparse.msg.error.unknown_symbol", f));
            }
        }
        Vec3 pos = null;
        if (ptr > 0) pos = new Vec3(vecRaw[0], vecRaw[1], vecRaw[2]);
        return PluginIotaFactory.makeGate(orderId--, pos, entity);
    }
}
