package io.yukkuric.hexparse_client.parsers.nbt2str;

import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.casting.iota.Vec3Iota;
import io.yukkuric.hexparse_client.parsers.IotaFactory;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VecParser implements INbt2Str {
    @Override
    public boolean match(CompoundTag node) {
        return isType(node, IotaFactory.TYPE_VECTOR);
    }

    @Override
    public String parse(CompoundTag node) {
        // use builtin
        var iota = (Vec3Iota) IotaType.deserialize(node, null);
        var vec = iota.getVec3();
        var frags = new ArrayList<>(List.of("vec"));
        var vecAxes = new ArrayList<>(Arrays.asList(vec.x, vec.y, vec.z));
        while (true) {
            var l = vecAxes.size();
            if (l == 0 || vecAxes.get(l - 1) != 0) break;
            vecAxes.remove(l - 1);
        }
        for (var a : vecAxes) frags.add(displayMinimal(a));
        return String.join("_", frags);
    }
}
