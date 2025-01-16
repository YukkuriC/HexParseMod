package io.yukkuric.hexparse.parsers.str2nbt;

import at.petrak.hexcasting.api.HexAPI;
import io.yukkuric.hexparse.parsers.ParserMain;
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
            // pop one
            put("pop", "mask_v");
            // special pattern dialect
            put("open_paren", "(");
            put("close_paren", ")");
            put("escape", "\\");
            // meta names
            put("hermes", "eval");
            put("iris", "eval/cc");
            put("thoth", "for_each");
            // 1.19 registry reverse (without operation overload)
            /*put("abs", "list_size");
            put("add", "concat");*/
            put("unique", "to_set");
            put("teleport/great", "teleport");
            put("remove_from", "list_remove");
            put("replace", "modify_in_place");
            for (var old_long : new String[]{
                    "mul_dot", "div_cross", "abs_len", "pow_proj",
                    "and_bit", "or_bit", "xor_bit", "not_bit",
                    "reverse_list",
            })
                put(old_long.split("_")[0], old_long);
        }
    });
}
