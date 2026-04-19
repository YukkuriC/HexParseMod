package io.yukkuric.hexparse.parsers;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public interface IPlayerBinder {
    void BindPlayer(@NotNull ServerPlayer p);
}
