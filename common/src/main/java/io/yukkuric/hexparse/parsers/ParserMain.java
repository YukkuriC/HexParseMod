package io.yukkuric.hexparse.parsers;

import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import io.yukkuric.hexparse.HexParse;
import io.yukkuric.hexparse.macro.MacroClient;
import io.yukkuric.hexparse.macro.MacroProcessor;
import io.yukkuric.hexparse.misc.CodeHelpers;
import io.yukkuric.hexparse.misc.StringProcessors;
import io.yukkuric.hexparse.parsers.nbt2str.*;
import io.yukkuric.hexparse.parsers.nbt2str.plugins.*;
import io.yukkuric.hexparse.parsers.str2nbt.*;
import io.yukkuric.hexparse.parsers.str2nbt.plugins.PluginConstParsers;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

import static io.yukkuric.hexparse.parsers.str2nbt.ConstParsers.*;

public class ParserMain {
    static boolean mutableFlag = false;
    static List<IStr2Nbt> str2nbtParsers = new ArrayList<>();
    static List<INbt2Str> nbt2strParsers = new ArrayList<>();
    static CompoundTag IGNORED = new CompoundTag();

    public static CompoundTag ParseSingleNode(String frag) {
        for (var p : str2nbtParsers) {
            if (p.match(frag)) {
                if (p.ignored()) return IGNORED;
                var res = p.parse(frag);
                CostTracker.INSTANCE.addCost(p.getCost());
                return res;
            }
        }
        return null;
    }

    public static synchronized CompoundTag ParseCode(String code, ServerPlayer caller) {
        return ParseCode(CodeCutter.splitCode(code), caller);
    }

    public static synchronized CompoundTag ParseCode(List<String> nodes, ServerPlayer caller) {
        if (caller == null) return IotaFactory.makeList(new ListTag());
        CodeHelpers.autoRefresh(caller.getServer());
        for (var p : str2nbtParsers) if (p instanceof IPlayerBinder pb) pb.BindPlayer(caller);
        try (var ignored = CostTracker.INSTANCE.beginTrack(caller)) {
            return _parseCode(nodes, caller);
        }
    }

    private static CompoundTag _parseCode(List<String> nodes, ServerPlayer caller) {
        var stack = new Stack<ListTag>();
        stack.add(new ListTag());
        try {
            for (MacroProcessor it = new MacroProcessor(nodes.iterator(), caller); it.hasNext(); ) {
                var frag = it.next();
                switch (frag) {
                    // special: nested list
                    case "[":
                        stack.push(new ListTag());
                        break;
                    case "]":
                        if (stack.size() <= 1) {
                            throw new RuntimeException(HexParse.doTranslate("hexparse.msg.error.bracket.closed"));
                        }
                        var inner = IotaFactory.makeList(stack.pop());
                        stack.peek().add(inner);
                        break;
                    default:
                        try {
                            var parsed = ParseSingleNode(frag);
                            if (parsed == null)
                                caller.sendSystemMessage(Component.translatable("hexparse.msg.error.unknown_symbol", frag).withStyle(ChatFormatting.GOLD));
                            else if (parsed != IGNORED) stack.peek().add(parsed);
                        } catch (Throwable e) {
                            caller.sendSystemMessage(Component.translatable("hexparse.msg.parse_error_node", frag, e.getLocalizedMessage()).withStyle(ChatFormatting.DARK_RED));
                        }
                        break;
                }
            }
            if (stack.size() > 1) {
                throw new RuntimeException(HexParse.doTranslate("hexparse.msg.error.bracket.open"));
            }
        } catch (Throwable e) {
            caller.sendSystemMessage(Component.translatable("hexparse.msg.parse_error", e.getLocalizedMessage()).withStyle(ChatFormatting.DARK_RED));
            // try fix data anyway
            while (stack.size() > 1) {
                var sub = IotaFactory.makeList(stack.pop());
                stack.peek().add(sub);
            }
            return IotaFactory.makeList(stack.isEmpty() ? new ListTag() : stack.pop());
        }
        return IotaFactory.makeList(stack.pop());
    }

    public static List<String> preMatchClipboardClient(String code) {
        var res = new ArrayList<String>();
        var caller = Minecraft.getInstance().player;
        for (var frag : CodeCutter.splitCode(code)) {
            var matched = false;
            if ("[]".contains(frag) || MacroClient.preMatch(frag)) {
                res.add(frag);
                matched = true;
            } else for (var p : str2nbtParsers) {
                if (p.match(frag) && !p.ignored()) {
                    res.add(frag);
                    matched = true;
                    break;
                }
            }
            if (!matched && caller != null)
                caller.sendSystemMessage(Component.translatable("hexparse.msg.error.unknown_symbol", frag).withStyle(ChatFormatting.GOLD));
        }
        return res;
    }

    public static synchronized String ParseIotaNbt(CompoundTag node, ServerPlayer caller, StringProcessors.F post) {
        var res = _parseIotaNbt(node, caller, true);
        res = post.apply(res);
        return res;
    }

    private static synchronized String _parseIotaNbt(CompoundTag node, ServerPlayer caller, boolean isRoot) {
        // bind caller
        if (isRoot) for (var p : nbt2strParsers) if (p instanceof IPlayerBinder pb) pb.BindPlayer(caller);

        try {
            // handle nested list
            if (node.getString(HexIotaTypes.KEY_TYPE).equals(IotaFactory.TYPE_LIST)) {
                var sb = new StringBuilder();
                if (!isRoot) sb.append('[');
                var isFirst = true;
                for (var sub : node.getList(HexIotaTypes.KEY_DATA, ListTag.TAG_COMPOUND)) {
                    if (isFirst) isFirst = false;
                    else sb.append(',');
                    sb.append(_parseIotaNbt((CompoundTag) sub, caller, false));
                }
                if (!isRoot) sb.append(']');
                return sb.toString();
            }
            for (var p : nbt2strParsers) {
                if (p.match(node)) return p.parse(node);
            }
            return "UNKNOWN";
        } catch (Throwable e) {
            caller.sendSystemMessage(Component.translatable("hexparse.msg.parse_error_node", node, e.getLocalizedMessage()).withStyle(ChatFormatting.DARK_RED));
            return "ERROR";
        }
    }

    public static <T> T loadUnsafe(Class<T> target, String subPath) {
        try {
            var clazz = Class.forName("io.yukkuric.hexparse.parsers." + subPath);
            var inst = clazz.getDeclaredField("INSTANCE").get(null);
            return target.cast(inst);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static void init() {
        str2nbtParsers.addAll(List.of(
                ToPattern.NORMAL, ToPattern.GREAT,
                TO_TAB, TO_COMMENT,
                TO_NUM, TO_VEC,
                TO_MASK, TO_NUM_PATTERN,
                new ToEntity(),
                ToMiscConst.INSTANCE,
                ToDialect.INSTANCE,
                TO_RAW_PATTERN
        ));

        nbt2strParsers.addAll(List.of(
                new PatternParser(),
                new CommentParser(),
                new NumParser(), new VecParser(),
                new EntityParser(),
                new BoolParser(),
                new NullParser(),
                new GarbageParser()
        ));

        if (HexParse.HELPERS.modLoaded("hexal")) {
            str2nbtParsers.add(PluginConstParsers.TO_ENTITY_TYPE);
            str2nbtParsers.add(PluginConstParsers.TO_IOTA_TYPE);
            str2nbtParsers.add(PluginConstParsers.TO_ITEM_TYPE);
            str2nbtParsers.add(PluginConstParsers.TO_BLOCK_TYPE);
            nbt2strParsers.add(StringParser.IOTA);
            nbt2strParsers.add(StringParser.ENTITY);
            nbt2strParsers.add(new ItemTypeParser());
            str2nbtParsers.add(loadUnsafe(IStr2Nbt.class, "str2nbt.unsafe.hexal.ToGate"));
            nbt2strParsers.add(new GateParser());
            str2nbtParsers.add(loadUnsafe(IStr2Nbt.class, "str2nbt.unsafe.hexal.ToMote"));
            nbt2strParsers.add(loadUnsafe(INbt2Str.class, "nbt2str.unsafe.hexal.MoteParser"));
        }
        if (HexParse.HELPERS.modLoaded("moreiotas")) {
            str2nbtParsers.add(PluginConstParsers.TO_STRING);
            nbt2strParsers.add(StringParser.STRING);
        }

        if (HexParse.HELPERS.modLoaded("hexcellular")) {
            str2nbtParsers.add(PluginConstParsers.TO_PROPERTY);
            nbt2strParsers.add(PropertyParser.INSTANCE);
        }
    }

    public static void AddForthParser(IStr2Nbt p) {
        str2nbtParsers.add(p);
    }

    public static void AddBackParser(INbt2Str p) {
        nbt2strParsers.add(p);
    }
}
