package io.yukkuric.hexparse.parsers.nbt2str;

import at.petrak.hexcasting.api.casting.iota.Vec3Iota;

import java.util.*;

public class VecParser implements INbt2Str<Vec3Iota> {
    @Override
    public String parse(Vec3Iota iota) {
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
    @Override
    public Class<Vec3Iota> getType() {
        return Vec3Iota.class;
    }
}
