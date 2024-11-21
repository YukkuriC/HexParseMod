package io.yukkuric.hexparse.hooks;

import com.mojang.brigadier.CommandDispatcher;
import io.yukkuric.hexparse.commands.CommandClipboard;
import io.yukkuric.hexparse.commands.CommandRead;
import io.yukkuric.hexparse.commands.CommandRefresh;
import io.yukkuric.hexparse.commands.CommandWrite;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class HexParseCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var mainCmd = Commands.literal("hexParse");

        CommandClipboard.init(mainCmd);
        CommandRefresh.init(mainCmd);
        CommandRead.init(mainCmd);
        CommandWrite.init(mainCmd);

        dispatcher.register(mainCmd);
    }
}
