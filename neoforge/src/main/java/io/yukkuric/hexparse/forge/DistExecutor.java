package io.yukkuric.hexparse.forge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

import java.util.function.Supplier;

public class DistExecutor {
    public static void unsafeRunWhenOn(Dist dist, Supplier<Runnable> action) {
        if (FMLEnvironment.dist == dist) action.get().run();
    }
}
