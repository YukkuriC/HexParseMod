package io.yukkuric.hexparse.commands;

import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.yukkuric.hexparse.misc.CodeHelpers;
import io.yukkuric.hexparse.misc.StringProcessors;
import io.yukkuric.hexparse.network.*;
import io.yukkuric.hexparse.parsers.IotaFactory;
import io.yukkuric.hexparse.parsers.ParserMain;
import io.yukkuric.hexparse.parsers.PluginIotaFactory;
import miyucomics.hexcellular.StateStorage;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

import static io.yukkuric.hexparse.hooks.HexParseCommands.registerLine;

public class CommandPropertyIO {
    public static void init() {
        var sub_property = registerLine(ctx -> propertyOp(ctx, CommandPropertyIO::readProp),
                Commands.literal("property"),
                Commands.literal("read"),
                Commands.argument("propName", StringArgumentType.string())
        );
        registerLine(ctx -> propertyOp(ctx, CommandPropertyIO::writeProp),
                sub_property,
                Commands.literal("write"),
                Commands.argument("propName", StringArgumentType.string()),
                Commands.argument("code", StringArgumentType.string())
        );
        registerLine(ctx -> propertyOp(ctx, CommandPropertyIO::pullClipboard),
                sub_property,
                Commands.literal("clipboard"),
                Commands.argument("propName", StringArgumentType.string())
        );
        registerLine(ctx -> propertyOp(ctx, CommandPropertyIO::dumpProp),
                sub_property,
                Commands.literal("dump"),
                Commands.argument("propName", StringArgumentType.string())
        );
    }

    static int propertyOp(CommandContext<CommandSourceStack> ctx, @NotNull BiConsumer<String, CommandContext<CommandSourceStack>> action) {
        var propName = StringArgumentType.getString(ctx, "propName");
        propName = StringProcessors.APPEND_UNDERLINE.apply(propName);
        action.accept(propName, ctx);
        return 1;
    }

    static void readProp(String propName, CommandContext<CommandSourceStack> ctx) {
        var data = StateStorage.Companion.getProperty(ctx.getSource().getLevel(), propName);
        var player = ctx.getSource().getPlayer();
        var code = ParserMain.ParseIotaNbt(IotaType.serialize(data), player, StringProcessors.READ_DEFAULT);
        CodeHelpers.displayCode(player, code);
    }

    static void dumpProp(String propName, CommandContext<CommandSourceStack> ctx) {
        var player = ctx.getSource().getPlayer();
        if (player == null) return;
        var level = ctx.getSource().getLevel();
        var visited = new HashSet<String>();
        dumpPropRecursive(player, level, propName, visited, 0);
    }

    static void dumpPropRecursive(ServerPlayer player, ServerLevel level, String propName, Set<String> visited, int depth) {
        if (visited.contains(propName)) {
            var indent = "  ".repeat(depth);
            var msg = Component.literal(indent + propName + ": ")
                    .withStyle(ChatFormatting.GRAY)
                    .append(Component.translatable("hexparse.cmd.property.dump.cycle")
                            .withStyle(ChatFormatting.DARK_RED));
            player.sendSystemMessage(msg);
            return;
        }
        visited.add(propName);

        var data = StateStorage.Companion.getProperty(level, propName);
        var nbt = IotaType.serialize(data);
        var code = ParserMain.ParseIotaNbt(nbt, player, StringProcessors.READ_DEFAULT);

        var indent = "  ".repeat(depth);
        var header = Component.literal(indent + propName + ": ")
                .withStyle(ChatFormatting.AQUA);
        var codeDisplay = Component.literal(code).withStyle(ChatFormatting.WHITE);
        var line = CodeHelpers.wrapClickCopy(header.append(codeDisplay), code);
        player.sendSystemMessage(line);

        collectPropertyNames(nbt, player, level, visited, depth);
    }

    static void collectPropertyNames(CompoundTag node, ServerPlayer player, ServerLevel level, Set<String> visited, int depth) {
        var type = node.getString(HexIotaTypes.KEY_TYPE);
        if (type.equals(IotaFactory.TYPE_LIST)) {
            var list = node.getList(HexIotaTypes.KEY_DATA, ListTag.TAG_COMPOUND);
            for (var child : list) {
                collectPropertyNames((CompoundTag) child, player, level, visited, depth);
            }
        } else if (type.equals(PluginIotaFactory.TYPE_PROP)) {
            var data = node.getCompound(HexIotaTypes.KEY_DATA);
            var refName = data.getString("name");
            if (!refName.isEmpty()) {
                dumpPropRecursive(player, level, refName, visited, depth + 1);
            }
        }
    }

    static void writeProp(String propName, CommandContext<CommandSourceStack> ctx) {
        var code = StringArgumentType.getString(ctx, "code");
        var nbt = ParserMain.ParseCode(code, ctx.getSource().getPlayer());
        var world = ctx.getSource().getLevel();
        StateStorage.Companion.setProperty(world, propName, IotaType.deserialize(nbt, world));
    }
    static void pullClipboard(String propName, CommandContext<CommandSourceStack> ctx) {
        MsgHandlers.SERVER.sendPacketToPlayer(ctx.getSource().getPlayer(), new MsgPullClipboard(propName, ClipboardMsgMode.WRITE_PROPERTY));
    }
}
