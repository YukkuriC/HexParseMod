package io.yukkuric.hexparse.hooks;

import at.petrak.hexcasting.api.PatternRegistry;
import at.petrak.hexcasting.api.spell.Action;
import at.petrak.hexcasting.api.spell.OperationResult;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.eval.SpellContinuation;
import at.petrak.hexcasting.api.spell.casting.sideeffects.OperatorSideEffect;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.IotaType;
import at.petrak.hexcasting.api.spell.math.HexDir;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import io.yukkuric.hexparse.HexParse;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CommentIotaType extends IotaType<CommentIota> {
    public static CommentIotaType INSTANCE = new CommentIotaType();

    public static final String TYPE_ID = HexParse.MOD_ID + ":comment";

    static final HexPattern COMMENT_PATTERN = HexPattern.fromAngles("adadaqadadaaww", HexDir.SOUTH_EAST);
    static final Action NULL_ACTION = new Action() {
        static List<OperatorSideEffect> NO_EFFECT = new ArrayList<>();
        static Component DISPLAY = Component.literal("comment");

        @Override
        public @NotNull Component getDisplayName() {
            return DISPLAY;
        }

        @Override
        public OperationResult operate(SpellContinuation continuation, List<Iota> stack, Iota raven, CastingContext ctx) {
            return new OperationResult(continuation, stack, raven, NO_EFFECT);
        }

        @Override
        public boolean isGreat() {
            return false;
        }

        @Override
        public boolean getAlwaysProcessGreatSpell() {
            return false;
        }

        @Override
        public boolean getCausesBlindDiversion() {
            return false;
        }
    };

    @Override
    public CommentIota deserialize(Tag tag, ServerLevel serverLevel) throws IllegalArgumentException {
        return new CommentIota(tag.getAsString());
    }

    @Override
    public Component display(Tag tag) {
        return Component.literal(tag.getAsString()).withStyle(ChatFormatting.DARK_GREEN);
    }

    @Override
    public int color() {
        return 0xff_00aa00;
    }

    public static void registerSelf() {
        try {
            // add action
            PatternRegistry.mapPattern(COMMENT_PATTERN, new ResourceLocation(TYPE_ID), NULL_ACTION);
            // add type
            Registry.register(HexIotaTypes.REGISTRY, TYPE_ID, INSTANCE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
