package io.yukkuric.hexparse.parsers;

import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import com.mojang.brigadier.context.CommandContext;
import io.yukkuric.hexparse.misc.IotaFactory;
import io.yukkuric.hexparse.parsers.nbt2str.INbt2Str;
import io.yukkuric.hexparse.parsers.str2nbt.*;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class ParserMain {
    static List<IStr2Nbt> str2nbtParsers;
    static List<INbt2Str> nbt2strParsers;

    public static CompoundTag ParseCode(String code, CommandContext<CommandSourceStack> ctx) {
        return ParseCode(code, ctx.getSource().getPlayer());
    }

    public static CompoundTag ParseCode(String code, Player caller) {
        // bind caller
        for (var p : nbt2strParsers) if (p instanceof IPlayerBinder pb) pb.BindPlayer(caller);

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
                            caller.sendSystemMessage(Component.literal(String.format("Unknown symbol: %s", frag)).withStyle(ChatFormatting.GOLD));
                    } catch (Exception e) {
                        caller.sendSystemMessage(Component.literal(String.format("Error when parsing %s: %s", frag, e)).withStyle(ChatFormatting.DARK_RED));
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
        return ParseIotaNbt(node, ctx.getSource().getPlayer(), true);
    }

    public static String ParseIotaNbt(CompoundTag node, Player caller, boolean isRoot) {
        // bind caller
        if (isRoot) for (var p : nbt2strParsers) if (p instanceof IPlayerBinder pb) pb.BindPlayer(caller);

        try {
            // handle nested list
            if (node.getString(HexIotaTypes.KEY_TYPE).equals(IotaFactory.TYPE_PATTERN)) {
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

    public static void init() {
        str2nbtParsers = Arrays.asList(
                ToPattern.NORMAL, ToPattern.GREAT,
                ConstParsers.TO_TAB, ConstParsers.TO_COMMENT,
                ConstParsers.TO_NUM,
                new ToSelf()
        );

        nbt2strParsers = Arrays.asList(

        );
    }
}
