package io.yukkuric.hexparse_client.parsers.str2nbt;

import at.petrak.hexcasting.api.HexAPI;
import io.yukkuric.hexparse_client.parsers.ParserMain;
import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.Map;

public record ToDialect(Map<String, String> mapper) implements IStr2Nbt {
    String cutHeader(String node) {
        if (node.startsWith(HexAPI.MOD_ID)) return node.substring(HexAPI.MOD_ID.length() + 1);
        return node;
    }

    @Override
    public boolean match(String node) {
        node = cutHeader(node);
        return mapper.containsKey(node);
    }

    @Override
    public CompoundTag parse(String node) {
        node = cutHeader(node);
        var mapped = mapper.get(node);
        return ParserMain.ParseSingleNode(mapped);
    }

    public static final ToDialect INSTANCE = new ToDialect(new HashMap<>() {
        {
            // 1.19 registry
            put("list_size", "abs");
            put("concat", "add");
            put("to_set", "unique");
            put("teleport", "teleport/great");
            put("list_remove", "remove_from");
            put("modify_in_place", "replace");
            for (var old_long : new String[]{
                    "mul_dot", "div_cross", "abs_len", "pow_proj",
                    "and_bit", "or_bit", "xor_bit", "not_bit",
                    "reverse_list",
            })
                put(old_long, old_long.split("_")[0]);
        }
    });
}
