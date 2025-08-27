package io.yukkuric.hexparse.commands

import at.petrak.hexcasting.api.utils.aqua
import at.petrak.hexcasting.api.utils.darkRed
import at.petrak.hexcasting.api.utils.gold
import com.mojang.brigadier.context.CommandContext
import io.yukkuric.hexparse.hooks.HexParseCommands
import io.yukkuric.hexparse.hooks.PatternMapper
import io.yukkuric.hexparse.misc.CodeHelpers
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.ResourceLocationArgument
import net.minecraft.network.chat.Component

object CommandConflictResolver {
    fun init() {
        val subCmd = HexParseCommands.registerLine(
            ::listAll,
            Commands.literal("conflict")
        )
        HexParseCommands.registerLine(
            ::listAll,
            subCmd,
            Commands.literal("list"),
        )
        HexParseCommands.registerLine(
            ::listName,
            subCmd,
            Commands.literal("list"),
            Commands.argument("name", ResourceLocationArgument.id())
        )
        HexParseCommands.registerLine(
            ::redirectName,
            subCmd,
            Commands.literal("set"),
            Commands.argument("name", ResourceLocationArgument.id()),
            Commands.argument("id", ResourceLocationArgument.id())
        )
    }

    private fun listAll(ctx: CommandContext<CommandSourceStack>): Int {
        CodeHelpers.autoRefresh(ctx.source.server)

        val targets = PatternMapper.ShortNameTracker.shortNameWithConflicts.sorted().toList()
        ctx.source.sendSystemMessage(Component.translatable("hexparse.cmd.conflict.list_all.title"))
        for (name in targets) {
            val msg = Component.translatable(
                "hexparse.cmd.conflict.list_all.entry",
                Component.literal(name).aqua,
                PatternMapper.ShortNameTracker.allPointed[name]?.size ?: 0
            )
            ctx.source.sendSystemMessage(CodeHelpers.wrapClickSuggest(msg, "/hexParse conflict list $name"))
        }
        return targets.size
    }

    private fun listName(ctx: CommandContext<CommandSourceStack>): Int {
        CodeHelpers.autoRefresh(ctx.source.server)

        val name = ResourceLocationArgument.getId(ctx, "name").path
        val curTarget = PatternMapper.ShortNameTracker.mapActiveShortName[name]
        if (curTarget == null) {
            ctx.source.sendFailure(Component.translatable("hexparse.cmd.conflict.error.name").darkRed)
            return -1
        }
        ctx.source.sendSystemMessage(
            Component.translatable(
                "hexparse.cmd.conflict.list.title",
                Component.literal(name).aqua,
                Component.literal(curTarget.toString()).gold
            )
        )

        val allEntries = PatternMapper.ShortNameTracker.allPointed[name]?.sorted()?.toList() ?: listOf()
        for (id in allEntries) {
            val msg = Component.translatable(
                "hexparse.cmd.conflict.list.entry",
                Component.literal(id.toString()).gold,
                CodeHelpers.getPatternDisplay(id, ctx.source.level),
            )
            ctx.source.sendSystemMessage(CodeHelpers.wrapClickSuggest(msg, "/hexParse conflict set $name $id"))
        }

        return allEntries.size
    }

    private fun redirectName(ctx: CommandContext<CommandSourceStack>): Int {
        CodeHelpers.autoRefresh(ctx.source.server)

        val name = ResourceLocationArgument.getId(ctx, "name").path
        val id = ResourceLocationArgument.getId(ctx, "id")
        PatternMapper.ShortNameTracker.redirectShortName(name, id)
        ctx.source.sendSystemMessage(
            Component.translatable(
                "hexparse.cmd.conflict.edited",
                Component.literal(name).aqua,
                Component.literal(id.toString()).gold,
                CodeHelpers.getPatternDisplay(id, ctx.source.level),
            )
        )
        return 1
    }
}