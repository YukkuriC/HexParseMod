package io.yukkuric.hexparse.hooks;

import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.api.casting.castables.Action;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.casting.iota.PatternIota;
import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.mod.HexTags;
import at.petrak.hexcasting.api.utils.HexUtils;
import at.petrak.hexcasting.common.lib.hex.HexActions;
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import io.yukkuric.hexparse.HexParse;
import io.yukkuric.hexparse.parsers.IotaFactory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Registry;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
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

    static final Action NULL_ACTION = new Action() {
        static final List<OperatorSideEffect> NO_EFFECT = new ArrayList<>();

        @Override
        public @NotNull OperationResult operate(CastingEnvironment env, CastingImage img, SpellContinuation cont) {
            return new OperationResult(img, NO_EFFECT, cont, HexEvalSounds.NOTHING);
        }
    };

    public static final ActionRegistryEntry COMMENT_ACTION_ENTRY = new ActionRegistryEntry(COMMENT_PATTERN, NULL_ACTION);

    @Override
    public CommentIota deserialize(Tag tag, ServerLevel serverLevel) throws IllegalArgumentException {
        return new CommentIota(tag.getAsString());
    }

    @Override
    public Component display(Tag tag) {
        var raw = tag.getAsString();
        if (!IotaFactory.isGreatPatternPlaceholder(raw)) {
            if (getShiftKeyDown.get()) return Component.empty();
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
        registerIota();
        registerAction();
    }

    public static void registerAction() {
        // add action
        Registry.register(HexActions.REGISTRY, TYPE_ID, COMMENT_ACTION_ENTRY);
    }

    public static void registerIota() {
        // add type
        Registry.register(HexIotaTypes.REGISTRY, TYPE_ID, INSTANCE);
    }

    static String cachedGreatPatternKeys;

    static String pickRandomGreatPatternKey(long from, int size) {
        // get cache
        if (cachedGreatPatternKeys == null) {
            var randList = new ArrayList<>();
            // get keys
            {
                var registry = IXplatAbstractions.INSTANCE.getActionRegistry();
                for (var entry : registry.entrySet()) {
                    var key = entry.getKey();
                    if (HexUtils.isOfTag(registry, key, HexTags.Actions.PER_WORLD_PATTERN)) {
                        var regKey = key.location();
                        for (int i = 0; i < 3; i++) {
                            randList.add(regKey.getPath());
                        }
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
