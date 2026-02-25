package io.yukkuric.hexparse.compat.hexdebug;

import at.petrak.hexcasting.api.casting.iota.IotaType;
import gay.object.hexdebug.api.client.splicing.SplicingTableIotaRenderer;
import gay.object.hexdebug.api.splicing.SplicingTableIotaClientView;
import io.yukkuric.hexparse.hooks.CommentIotaType;
import net.minecraft.client.gui.components.Tooltip;
import org.jetbrains.annotations.NotNull;

public abstract class CommentRendererButIgnoresOverride extends SplicingTableIotaRenderer {
    public CommentRendererButIgnoresOverride(@NotNull IotaType<?> type, @NotNull SplicingTableIotaClientView iota, int x, int y) {
        super(type, iota, x, y);
    }

    public Tooltip getTooltip() {
        return Tooltip.create(CommentIotaType.INSTANCE.display(getIota().getData()));
    }
}
