package io.yukkuric.hexparse.commands

import at.petrak.hexcasting.api.spell.iota.NullIota
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import at.petrak.hexcasting.xplat.IXplatAbstractions
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import io.yukkuric.hexparse.hooks.HexParseCommands
import io.yukkuric.hexparse.misc.CodeHelpers
import io.yukkuric.hexparse.misc.StringProcessors
import io.yukkuric.hexparse.network.ClipboardMsgMode
import io.yukkuric.hexparse.network.MsgHandlers
import io.yukkuric.hexparse.network.MsgPullClipboard
import io.yukkuric.hexparse.parsers.ParserMain
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand

object CommandMindStackIO {
    fun init() {
        val subCmd = HexParseCommands.registerLine(
            ::readStack,
            Commands.literal("mind_stack"),
            Commands.literal("peek")
        )
        HexParseCommands.registerLine(
            ::writeStack,
            subCmd,
            Commands.literal("push"),
            Commands.argument("code", StringArgumentType.string())
        )
        HexParseCommands.registerLine(
            ::pullClipboard,
            subCmd,
            Commands.literal("push_clipboard"),
        )
    }

    fun readStack(ctx: CommandContext<CommandSourceStack>): Int {
        val player = ctx.source.player
        val img = IXplatAbstractions.INSTANCE.getHarness(player, InteractionHand.MAIN_HAND)
        val stack = img.stack
        val lastIota = if (stack.isEmpty()) NullIota() else stack[stack.size - 1]
        val code = ParserMain.ParseIotaNbt(HexIotaTypes.serialize(lastIota), player, StringProcessors.READ_DEFAULT)
        CodeHelpers.displayCode(player, code)
        return 1
    }

    fun writeStack(ctx: CommandContext<CommandSourceStack>): Int {
        val code = StringArgumentType.getString(ctx, "code")
        val nbt = ParserMain.ParseCode(code, ctx.source.player)
        writeStackWithIota(ctx.source.player!!, nbt)
        return 1
    }

    fun pullClipboard(ctx: CommandContext<CommandSourceStack>): Int {
        MsgHandlers.SERVER.sendPacketToPlayer(
            ctx.source.player,
            MsgPullClipboard(null, ClipboardMsgMode.PUSH_MIND)
        )
        return 1
    }

    fun writeStackWithIota(player: ServerPlayer, iotaTag: CompoundTag) {
        val newIota = HexIotaTypes.deserialize(iotaTag, player.getLevel())
        var img = IXplatAbstractions.INSTANCE.getHarness(player, InteractionHand.MAIN_HAND)
        var stack = img.stack // it is mutable
        stack.add(newIota)
        IXplatAbstractions.INSTANCE.setHarness(player, img)
    }
}