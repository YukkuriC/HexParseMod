package io.yukkuric.hexparse.parsers;

import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import io.yukkuric.hexparse.HexParse;
import io.yukkuric.hexparse.parsers.nbt2str.*;
import io.yukkuric.hexparse.parsers.str2nbt.*;
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
    static List<IStr2Nbt> str2nbtParsers;
    static List<INbt2Str> nbt2strParsers;
    static CompoundTag IGNORED = new CompoundTag();

    public static CompoundTag ParseSingleNode(String frag) {
        for (var p : str2nbtParsers) {
            if (p.match(frag)) {
                if (p.ignored()) return IGNORED;
                return p.parse(frag);
            }
        }
        return null;
    }

    public static synchronized CompoundTag ParseCode(String code, ServerPlayer caller) {
        return ParseCode(CodeCutter.splitCode(code), caller);
    }

    public static synchronized CompoundTag ParseCode(List<String> nodes, ServerPlayer caller) {
        for (var p : str2nbtParsers) if (p instanceof IPlayerBinder pb) pb.BindPlayer(caller);
        return _parseCode(nodes, caller);
    }

    private static CompoundTag _parseCode(List<String> nodes, ServerPlayer caller) {
        var stack = new Stack<ListTag>();
        stack.add(new ListTag());
        try {
            for (var frag : nodes) {
                switch (frag) {
                    // special: nested list
                    case "[":
                        stack.push(new ListTag());
                        break;
                    case "]":
                        var inner = IotaFactory.makeList(stack.pop());
                        if (stack.isEmpty()) {
                            throw new RuntimeException(HexParse.doTranslate("hexparse.msg.error.bracket.closed"));
                        }
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
            caller.sendSystemMessage(Component.translatable("hexparse.msg.parse_error", e.getLocalizedMessage()));
            // try fix data anyway
            while (stack.size() > 1) {
                var sub = stack.pop();
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
            if ("[]".contains(frag)) {
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


    public static synchronized String ParseIotaNbt(CompoundTag node, ServerPlayer caller) {
        var res = _parseIotaNbt(node, caller, true);
        res = res.replaceAll("(?<=\\[|]|\\(|\\)|^|\\n|\\s),|,(?=\\[|]|\\(|\\)|$|\\n)", "");
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


    static void makeMutableLists() {
        if (mutableFlag) return;
        mutableFlag = true;
        str2nbtParsers = new ArrayList<>(str2nbtParsers);
        nbt2strParsers = new ArrayList<>(nbt2strParsers);
    }

    public static void init() {
        str2nbtParsers = Arrays.asList(
                ToPattern.NORMAL, ToPattern.GREAT,
                TO_TAB, TO_COMMENT,
                TO_NUM, TO_VEC,
                TO_MASK, TO_NUM_PATTERN,
                new ToEntity(), new ToSelf(),
                ToDialect.INSTANCE,
                TO_RAW_PATTERN
        );

        nbt2strParsers = Arrays.asList(
                new PatternParser(),
                new CommentParser(),
                new NumParser(), new VecParser(),
                new EntityParser()
        );

        if (HexParse.HELPERS.modLoaded("hexal")) {
            makeMutableLists();
            str2nbtParsers.add(PluginConstParsers.TO_ENTITY_TYPE);
            str2nbtParsers.add(PluginConstParsers.TO_IOTA_TYPE);
            str2nbtParsers.add(PluginConstParsers.TO_ITEM_TYPE);
            str2nbtParsers.add(PluginConstParsers.TO_BLOCK_TYPE);
            nbt2strParsers.add(StringParser.IOTA);
            nbt2strParsers.add(StringParser.ENTITY);
            nbt2strParsers.add(new ItemTypeParser());
        }
        if (HexParse.HELPERS.modLoaded("moreiotas")) {
            makeMutableLists();
            str2nbtParsers.add(PluginConstParsers.TO_STRING);
            nbt2strParsers.add(StringParser.STRING);
        }
    }

    public static void AddForthParser(IStr2Nbt p) {
        makeMutableLists();
        str2nbtParsers.add(p);
    }

    public static void AddBackParser(INbt2Str p) {
        makeMutableLists();
        nbt2strParsers.add(p);
    }
}
