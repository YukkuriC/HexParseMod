package io.yukkuric.hexparse.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.yukkuric.hexparse.macro.MacroManager;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.function.BiFunction;

import static io.yukkuric.hexparse.hooks.HexParseCommands.registerLine;
import static io.yukkuric.hexparse.macro.MacroManager.MAX_SINGLE_MACRO_SIZE;

public class CommandMacro {
    public static void init() {
        initWith(Commands.literal("macro"), true);
        initWith(Commands.literal("dialect"), false);
    }

    static MutableComponent showGold(String val) {
        return Component.literal(val).withStyle(ChatFormatting.GOLD);
    }

    static void initWith(LiteralArgumentBuilder<CommandSourceStack> sub, boolean isMacro) {
        // list all
        registerLine(ctx -> {
            var self = ctx.getSource().getPlayer();
            if (self == null) return 0;
            var entries = MacroManager.listMacros(self, isMacro);
            self.sendSystemMessage(Component.translatable("hexparse.cmd.macro.list.title",
                    entries.size(),
                    Component.translatable("hexparse.cmd.macro.list.title." + (isMacro ? "macro" : "dialect"))));
            for (var pair : entries)
                self.sendSystemMessage(Component.translatable("hexparse.cmd.macro.list.kv",
                        showGold(pair.first()), showGold(pair.second())));
            return entries.size();
        }, sub, Commands.literal("list"));

        // modify
        BiFunction<CommandContext<CommandSourceStack>, Boolean, Integer> generalModify = (ctx, isDefine) -> {
            var self = ctx.getSource().getPlayer();
            if (self == null) return 0;
            var key = StringArgumentType.getString(ctx, "key");
            if (isMacro != MacroManager.isMacro(key)) {
                if (isMacro) {
                    key = '#' + key;
                } else {
                    self.sendSystemMessage(Component.translatable("hexparse.msg.error.invalid_dialect_key").withStyle(ChatFormatting.DARK_RED));
                    return 0;
                }
            }
            if (isDefine) {
                if (MacroManager.willThisExceedLimit(self, key)) {
                    self.sendSystemMessage(Component.translatable("hexparse.msg.error.macro.too_many.single").withStyle(ChatFormatting.DARK_RED));
                    return 0;
                }
                if (key.length() > MAX_SINGLE_MACRO_SIZE) {
                    self.sendSystemMessage(Component.translatable("hexparse.msg.error.macro.too_long.key").withStyle(ChatFormatting.DARK_RED));
                    return 0;
                }
                var value = StringArgumentType.getString(ctx, "value");
                if (value.length() > MAX_SINGLE_MACRO_SIZE) {
                    self.sendSystemMessage(Component.translatable("hexparse.msg.error.macro.too_long", value.length() - MAX_SINGLE_MACRO_SIZE).withStyle(ChatFormatting.GOLD));
                    value = value.substring(0, MAX_SINGLE_MACRO_SIZE);
                }
                MacroManager.modifyMacro(self, true, key, value);
                self.sendSystemMessage(Component.translatable("hexparse.cmd.macro.define",
                        showGold(key), showGold(value)));
            } else {
                MacroManager.modifyMacro(self, false, key, null);
                self.sendSystemMessage(Component.translatable("hexparse.cmd.macro.remove", showGold(key)));
            }
            return 1;
        };

        // define
        registerLine(ctx -> generalModify.apply(ctx, true), sub,
                Commands.literal("define"),
                Commands.argument("key", StringArgumentType.string()),
                Commands.argument("value", StringArgumentType.greedyString()));

        // remove
        registerLine(ctx -> generalModify.apply(ctx, false), sub,
                Commands.literal("remove"),
                Commands.argument("key", StringArgumentType.string()));
    }
}
