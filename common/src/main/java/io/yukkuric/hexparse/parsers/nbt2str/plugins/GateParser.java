package io.yukkuric.hexparse.parsers.nbt2str.plugins;

import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import io.yukkuric.hexparse.parsers.IPlayerBinder;
import io.yukkuric.hexparse.parsers.PluginIotaFactory;
import io.yukkuric.hexparse.parsers.nbt2str.INbt2Str;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public class GateParser implements IPlayerBinder, INbt2Str {
    ServerPlayer self;

    @Override
    public void BindPlayer(ServerPlayer p) {
        self = p;
    }

    @Override
    public boolean match(CompoundTag node) {
        return isType(node, PluginIotaFactory.TYPE_GATE);
    }

    @Override
    public String parse(CompoundTag node) {
        node = node.getCompound(HexIotaTypes.KEY_DATA);
        var type = node.getByte("target_type");
        if (type <= 0 || type > 2) return "gate";
        var res = "gate_%s_%s_%s".formatted(
                displayMinimal(node.getDouble("target_x")),
                displayMinimal(node.getDouble("target_y")),
                displayMinimal(node.getDouble("target_z"))
        );
        if (type == 2) {
            var uuid = node.getUUID("target_uuid");
            if (self != null && self.getUUID().equals(uuid)) res += "_self";
            else res += "_" + uuid;
        }
        return res;
    }
}
