package io.yukkuric.hexparse.parsers.str2nbt.unsafe.hexal;

import at.petrak.hexcasting.api.casting.mishaps.MishapOthersName;
import io.yukkuric.hexparse.HexParse;
import io.yukkuric.hexparse.parsers.IPlayerBinder;
import io.yukkuric.hexparse.parsers.PluginIotaFactory;
import io.yukkuric.hexparse.parsers.str2nbt.BaseConstParser;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import ram.talia.hexal.api.config.HexalConfig;

import java.util.*;

public class ToGate extends BaseConstParser.Regex implements IPlayerBinder {
    public static ToGate INSTANCE = new ToGate();
    private ToGate() {
        super("^gate(_?)");
    }

    ServerPlayer self;
    int orderId;
    Map<Entity, Map<Vec3, Integer>> usedGates = new HashMap<>();
    boolean costPardonedThisTurn = false;

    int getGateOrder(Entity target, Vec3 pos) {
        if (target != null && pos == null) pos = Vec3.ZERO;
        if (!usedGates.containsKey(target)) usedGates.put(target, new HashMap<>());
        var innerMap = usedGates.get(target);
        costPardonedThisTurn = innerMap.containsKey(pos);
        if (!costPardonedThisTurn) innerMap.put(pos, orderId--);
        return innerMap.get(pos);
    }

    @Override
    public int getCost() {
        var res = super.getCost();
        if (!costPardonedThisTurn) res += (int) HexalConfig.getServer().getMakeGateCost();
        return res;
    }

    @Override
    public void BindPlayer(ServerPlayer p) {
        self = p;
        orderId = -1;
        usedGates.clear();
    }

    @Override
    public CompoundTag parse(String node) {
        if (node.length() <= 5) return PluginIotaFactory.makeGate(getGateOrder(null, null), null, null);
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
                if (entity instanceof Player p && !self.equals(p)) {
                    throw new MishapOthersName(p);
                }
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(HexParse.doTranslate("hexparse.msg.error.unknown_symbol", f));
            } catch (MishapOthersName e) {
                var msg = HexParse.doTranslate("hexcasting.mishap.others_name", entity.getName());
                throw new RuntimeException(msg);
            }
        }
        Vec3 pos = null;
        if (ptr > 0) pos = new Vec3(vecRaw[0], vecRaw[1], vecRaw[2]);
        // check and raise here
        if (entity != null && pos != null) {
            var maxDist = HexalConfig.getServer().getMaxGateOffset();
            if (pos.length() > maxDist) {
                var expected = HexParse.doTranslate("hexcasting.mishap.invalid_value.gate.offset", maxDist);
                throw new IllegalArgumentException(HexParse.doTranslate("hexcasting.mishap.invalid_value", "", expected, 0, pos));
            }
        }
        return PluginIotaFactory.makeGate(getGateOrder(entity, pos), pos, entity);
    }
}
