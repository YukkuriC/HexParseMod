package io.yukkuric.hexparse.parsers;

import com.mojang.brigadier.context.CommandContext;
import io.yukkuric.hexparse.HexParse;
import io.yukkuric.hexparse.misc.IotaFactory;
import io.yukkuric.hexparse.parsers.nbt2str.INbt2Str;
import io.yukkuric.hexparse.parsers.str2nbt.IStr2Nbt;
import io.yukkuric.hexparse.parsers.str2nbt.ToNum;
import io.yukkuric.hexparse.parsers.str2nbt.ToPattern;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class ParserMain {
    static List<IStr2Nbt> str2nbtParsers;
    static List<INbt2Str> nbt2strParsers;

    public static CompoundTag ParseCode(String code, CommandSourceStack source) {
        var stack = new Stack<ListTag>();
        stack.add(new ListTag());
        for (var frag : CodeCutter.splitCode(code)) {
            switch (frag) {
                // special: nested list
                case "[":
                    stack.push(new ListTag());
                    break;
                case "]":
                    var inner = IotaFactory.makeList(stack.pop());
                    if (stack.isEmpty()) {
                        throw new RuntimeException("too many closed bracket");
                    }
                    stack.peek().add(inner);
                    break;
                default:
                    CompoundTag ret;
                    try {
                        var matched = false;
                        for (var p : str2nbtParsers) {
                            if (p.match(frag)) {
                                stack.peek().add(p.parse(frag));
                                matched = true;
                                break;
                            }
                        }
                        // unknown
                        if (!matched)
                            source.sendFailure(Component.literal(String.format("Unknown symbol: %s", frag)));
                    } catch (Exception e) {
                        source.sendFailure(Component.literal(String.format("Error when parsing %s: %s", frag, e)));
                    }
                    break;
            }
        }
        if (stack.size() > 1) {
            throw new RuntimeException("too many open bracket");
        }
        return IotaFactory.makeList(stack.pop());
    }

    public static String ParseIotaNbt(CompoundTag node, CommandContext<CommandSourceStack> ctx) {
        try {
            for (var p : nbt2strParsers) {
                if (p.match(node)) return p.parse(node);
            }
            return "UNKNOWN";
        } catch (Exception e) {
            HexParse.LOGGER.error("Error when parsing {}: {}", node, e);
            return "ERROR";
        }
    }

    public static void init() {
        str2nbtParsers = Arrays.asList(
                ToPattern.NORMAL,
                ToPattern.GREAT,
                new ToNum()
        );

        nbt2strParsers = Arrays.asList(

        );
    }
}
