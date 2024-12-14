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
import at.petrak.hexcasting.common.lib.hex.HexActions;
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import io.yukkuric.hexparse.HexParse;
import io.yukkuric.hexparse.parsers.IotaFactory;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CommentIotaType extends IotaType<CommentIota> {
    public static CommentIotaType INSTANCE = new CommentIotaType();

    public static final String TYPE_ID = HexParse.MOD_ID + ":comment";

    public static final HexPattern COMMENT_PATTERN = HexPattern.fromAngles("adadaqadadaaww", HexDir.SOUTH_EAST);

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
        var content = Component.literal(raw);
        if (raw.startsWith(IotaFactory.GREAT_PLACEHOLDER_PREFIX))
            content.withStyle(s -> s.withColor(PatternIota.TYPE.color()));
        else content.withStyle(ChatFormatting.DARK_GREEN);
        return content;
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
}
