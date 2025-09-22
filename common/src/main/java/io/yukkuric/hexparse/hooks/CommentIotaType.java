package io.yukkuric.hexparse.hooks;

import at.petrak.hexcasting.api.PatternRegistry;
import at.petrak.hexcasting.api.spell.Action;
import at.petrak.hexcasting.api.spell.OperationResult;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.eval.SpellContinuation;
import at.petrak.hexcasting.api.spell.casting.sideeffects.OperatorSideEffect;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.IotaType;
import at.petrak.hexcasting.api.spell.iota.PatternIota;
import at.petrak.hexcasting.api.spell.math.HexDir;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import io.yukkuric.hexparse.HexParse;
import io.yukkuric.hexparse.parsers.IotaFactory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Registry;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class CommentIotaType extends IotaType<CommentIota> {
    public static CommentIotaType INSTANCE = new CommentIotaType();
    public static final String TYPE_ID = HexParse.MOD_ID + ":comment";
    public static final HexPattern COMMENT_PATTERN = HexPattern.fromAngles("adadaqadadaaww", HexDir.SOUTH_EAST);
    static final Supplier<Boolean> getShiftKeyDown;

    static {
        Supplier<Boolean> getter;
        try {
            getter = Screen::hasShiftDown;
        } catch (Throwable e) {
            getter = () -> false;
        }
        getShiftKeyDown = getter;
    }

    public static final Action NULL_ACTION = new Action() {
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
        var raw = tag.getAsString();
        if (!IotaFactory.isGreatPatternPlaceholder(raw)) {
            if (getShiftKeyDown.get()) return Component.empty();
            if (!raw.isEmpty() && raw.charAt(0) == '\"') return  Component.literal(raw.substring(1, raw.length() - 1)).withStyle(ChatFormatting.DARK_GREEN);
            return Component.literal(raw).withStyle(ChatFormatting.DARK_GREEN);
        }
        var len = raw.length();
        var loopSize = (int) Math.floor(len * Math.PI * 2);
        var ticker = (System.currentTimeMillis() / 20);
        var looper = (int) (ticker % loopSize);
        if (looper >= len * 2) return Component.literal(raw).withStyle(s -> s.withColor(PatternIota.TYPE.color()));
        ticker -= looper;
        var filledStr = pickRandomGreatPatternKey(ticker, len - IotaFactory.GREAT_PLACEHOLDER_PREFIX.length() - IotaFactory.GREAT_PLACEHOLDER_POSTFIX.length());
        filledStr = IotaFactory.makeUnknownGreatPatternText(filledStr);
        if (looper <= len) {
            return Component.literal(filledStr.substring(0, looper)).withStyle(ChatFormatting.LIGHT_PURPLE).append(
                    Component.literal(raw.substring(looper)).withStyle(s -> s.withColor(PatternIota.TYPE.color()))
            );
        } else {
            looper -= len;
            return Component.literal(raw.substring(0, looper)).withStyle(s -> s.withColor(PatternIota.TYPE.color())).append(
                    Component.literal(filledStr.substring(looper)).withStyle(ChatFormatting.LIGHT_PURPLE)
            );
        }
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

    static String cachedGreatPatternKeys;

    static String pickRandomGreatPatternKey(long from, int size) {
        // get cache
        if (cachedGreatPatternKeys == null) {
            var randList = new ArrayList<>();
            // get keys
            {
                for (var regKey : PatternMapper.greatMapper.keySet()) {
                    for (int i = 0; i < 3; i++) {
                        randList.add(regKey.getPath());
                    }
                }
            }
            Collections.shuffle(randList);
            var sb = new StringBuilder();
            for (var k : randList) {
                sb.append(k);
                sb.append("---");
            }
            cachedGreatPatternKeys = sb.toString();
        }

        var startIdx = (int) (from % cachedGreatPatternKeys.length());
        var endIdx = startIdx + size;
        if (endIdx <= cachedGreatPatternKeys.length()) return cachedGreatPatternKeys.substring(startIdx, endIdx);
        else {
            return cachedGreatPatternKeys.substring(startIdx) + cachedGreatPatternKeys.substring(0, endIdx - cachedGreatPatternKeys.length());
        }
    }
}
