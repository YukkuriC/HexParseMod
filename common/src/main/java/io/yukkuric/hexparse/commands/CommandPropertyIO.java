package io.yukkuric.hexparse.commands;

import at.petrak.hexcasting.api.casting.iota.IotaType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.yukkuric.hexparse.misc.CodeHelpers;
import io.yukkuric.hexparse.misc.StringProcessors;
import io.yukkuric.hexparse.parsers.ParserMain;
import miyucomics.hexcellular.StateStorage;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

import static io.yukkuric.hexparse.hooks.HexParseCommands.registerLine;

public class CommandPropertyIO {
    public static void init() {
        var entry = registerLine(ctx -> propertyOp(ctx, CommandPropertyIO::readProp),
                Commands.literal("property"),
                Commands.literal("read"),
                Commands.argument("propName", StringArgumentType.string())
        );
        registerLine(ctx -> propertyOp(ctx, CommandPropertyIO::writeProp),
                entry,
                Commands.literal("write"),
                Commands.argument("propName", StringArgumentType.string())
        );
    }

    static int propertyOp(CommandContext<CommandSourceStack> ctx, @NotNull BiConsumer<String, CommandContext<CommandSourceStack>> action) {
        var propName = StringArgumentType.getString(ctx, "propName");
        propName = StringProcessors.APPEND_UNDERLINE.apply(propName);
        if (!propName.startsWith("_")) propName = "_" + propName;
        action.accept(propName, ctx);
        return 1;
    }

    static void readProp(String propName, CommandContext<CommandSourceStack> ctx) {
        var data = StateStorage.Companion.getProperty(ctx.getSource().getLevel(), propName);
        var player = ctx.getSource().getPlayer();
        var code = ParserMain.ParseIotaNbt(IotaType.serialize(data), player, StringProcessors.READ_DEFAULT);
        CodeHelpers.displayCode(player, code);
    }
    static void writeProp(String propName, CommandContext<CommandSourceStack> ctx) {
        var code = StringArgumentType.getString(ctx, "code");
        var nbt = ParserMain.ParseCode(code, ctx.getSource().getPlayer());
        var world = ctx.getSource().getLevel();
        StateStorage.Companion.setProperty(world, propName, IotaType.deserialize(nbt, world));
    }
}
