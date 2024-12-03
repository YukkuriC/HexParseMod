package io.yukkuric.hexparse_client.parsers;

import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.casting.iota.PatternIota;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import io.yukkuric.hexparse_client.parsers.nbt2str.*;
import io.yukkuric.hexparse_client.parsers.str2nbt.IStr2Nbt;
import io.yukkuric.hexparse_client.parsers.str2nbt.ToDialect;
import io.yukkuric.hexparse_client.parsers.str2nbt.ToPattern;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.yukkuric.hexparse_client.parsers.str2nbt.ConstParsers.*;

public class ParserMain {
    static boolean mutableFlag = false;
    static List<IStr2Nbt> str2nbtParsers;
    static List<INbt2Str> nbt2strParsers;
    public static CompoundTag IGNORED = new CompoundTag();

    public static CompoundTag ParseSingleNode(String frag) {
        for (var p : str2nbtParsers) {
            if (p.match(frag)) {
                if (p.ignored()) return IGNORED;
                return p.parse(frag);
            }
        }
        return null;
    }

    public static synchronized List<HexPattern> ParseCode(String code, LocalPlayer caller) {
        var pool = new ArrayList<HexPattern>();
        for (var frag : CodeCutter.splitCode(code)) {
            try {
                var parsed = ParseSingleNode(frag);
                if (parsed == null)
                    caller.sendSystemMessage(Component.literal(String.format("Unknown symbol: %s", frag)).withStyle(ChatFormatting.GOLD));
                else if (parsed != IGNORED) {
                    var iota = IotaType.deserialize(parsed, null);
                    if (!(iota instanceof PatternIota pi)) continue;
                    pool.add(pi.getPattern());
                }
            } catch (Exception e) {
                caller.sendSystemMessage(Component.literal(String.format("Error when parsing %s: %s", frag, e)).withStyle(ChatFormatting.DARK_RED));
            }
        }
        return pool;
    }

    public static synchronized String ParseIotaNbt(CompoundTag node, LocalPlayer caller) {
        return ParseIotaNbt(node, caller, true);
    }

    public static synchronized String ParseIotaNbt(CompoundTag node, LocalPlayer caller, boolean isRoot) {
        try {
            // handle nested list
            if (node.getString(HexIotaTypes.KEY_TYPE).equals(IotaFactory.TYPE_LIST)) {
                var sb = new StringBuilder();
                if (!isRoot) sb.append('[');
                var isFirst = true;
                for (var sub : node.getList(HexIotaTypes.KEY_DATA, ListTag.TAG_COMPOUND)) {
                    if (isFirst) isFirst = false;
                    else sb.append(',');
                    sb.append(ParseIotaNbt((CompoundTag) sub, caller, false));
                }
                if (!isRoot) sb.append(']');
                return sb.toString();
            }
            for (var p : nbt2strParsers) {
                if (p.match(node)) return p.parse(node);
            }
            return "UNKNOWN";
        } catch (Exception e) {
            caller.sendSystemMessage(Component.literal(String.format("Error when parsing %s: %s", node, e)).withStyle(ChatFormatting.DARK_RED));
            return "ERROR";
        }
    }


    static void makeMutableLists() {
        if (mutableFlag) return;
        mutableFlag = true;
        str2nbtParsers = new ArrayList<>(str2nbtParsers);
        nbt2strParsers = new ArrayList<>(nbt2strParsers);
    }

    public static void init() {
        str2nbtParsers = Arrays.asList(
                ToPattern.META,
                ToPattern.NORMAL,
                TO_TAB, TO_COMMENT,
                TO_NUM, TO_VEC,
                TO_MASK, TO_NUM_PATTERN,
                ToDialect.INSTANCE,
                TO_RAW_PATTERN
        );

        nbt2strParsers = Arrays.asList(
                new PatternParser(),
                new CommentParser(),
                new NumParser(), new VecParser()
        );
    }
}
