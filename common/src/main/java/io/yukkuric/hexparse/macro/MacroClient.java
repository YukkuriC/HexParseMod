package io.yukkuric.hexparse.macro;

import com.google.gson.Gson;
import io.yukkuric.hexparse.HexParse;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

import static io.yukkuric.hexparse.macro.MacroManager.MAX_MACRO_COUNT;
import static io.yukkuric.hexparse.macro.MacroManager.MAX_SINGLE_MACRO_SIZE;

public class MacroClient {
    static final Gson GSON = new Gson();
    static final Path BASE_DIR = Minecraft.getInstance().gameDirectory.toPath().resolve("hexParse/macro");
    static final String SAVENAME = "macro.json";
    static final int MAX_BACKUP = 3;
    static final File SAVE_TARGET;
    static final File[] SAVE_BAKS;

    static final Map<String, String> macros = new HashMap<>();
    static boolean dirty = false;

    static {
        SAVE_TARGET = BASE_DIR.resolve(SAVENAME).toFile();
        List<File> baks = new ArrayList<>();
        for (int i = 1; i <= MAX_BACKUP; i++) {
            baks.add(BASE_DIR.resolve("%s.bak%s".formatted(SAVENAME, i)).toFile());
        }
        SAVE_BAKS = baks.toArray(File[]::new);
    }

    // ========== SAVE ==========
    static void stepBackup(File frm, File to) {
        if (!frm.exists()) return;
        if (to.exists()) to.delete();
        frm.renameTo(to);
    }

    public static void save() {
        BASE_DIR.toFile().mkdirs();
        if (!dirty && SAVE_TARGET.exists()) return;
        for (int i = MAX_BACKUP - 1; i > 0; i--) stepBackup(SAVE_BAKS[i - 1], SAVE_BAKS[i]);
        stepBackup(SAVE_TARGET, SAVE_BAKS[0]);
        try (var fos = new FileOutputStream(SAVE_TARGET); var osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8); var bw = new BufferedWriter(osw)) {
            bw.write(GSON.toJson(macros));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    // ========== LOAD ==========
    static boolean tryLoad(File f) {
        if (!f.exists()) return false;
        try (var fs = new FileInputStream(f); var sr = new InputStreamReader(fs, StandardCharsets.UTF_8); var br = new BufferedReader(sr)) {
            var caller = Minecraft.getInstance().player;
            HashMap<String, Object> raw = GSON.fromJson(br, HashMap.class);
            int tmpSize;
            if ((tmpSize = raw.size()) > MAX_MACRO_COUNT) {
                if (caller != null)
                    caller.sendSystemMessage(Component.translatable("hexparse.msg.error.macro.too_many", tmpSize - MAX_MACRO_COUNT).withStyle(ChatFormatting.DARK_RED));
            }
            int cnt = 0;
            for (var pair : raw.entrySet()) {
                var key = pair.getKey();
                if (key.length() > MAX_SINGLE_MACRO_SIZE) {
                    if (caller != null)
                        caller.sendSystemMessage(Component.translatable("hexparse.msg.error.macro.too_long.key").withStyle(ChatFormatting.DARK_RED));
                    continue;
                }
                var val = String.valueOf(pair.getValue());
                if ((tmpSize = val.length()) > MAX_SINGLE_MACRO_SIZE) {
                    if (caller != null)
                        caller.sendSystemMessage(Component.translatable("hexparse.msg.error.macro.too_long", tmpSize - MAX_SINGLE_MACRO_SIZE).withStyle(ChatFormatting.GOLD));
                    val = val.substring(0, MAX_SINGLE_MACRO_SIZE);
                }
                macros.put(key, val);
                cnt++;
                if (cnt >= MAX_MACRO_COUNT) break;
            }
            return true;
        } catch (Throwable e) {
            HexParse.LOGGER.error(e.toString());
            return false;
        }
    }

    public static void load() {
        macros.clear();
        dirty = false;
        BASE_DIR.toFile().mkdirs();
        if (tryLoad(SAVE_TARGET)) return;
        for (var t : SAVE_BAKS) if (tryLoad(t)) return;
        initExampleMacros();
    }

    // ========== MODIFY ==========
    public static void entryOp(boolean isDefine, String key, String value) {
        dirty = true;
        if (isDefine) {
            if (value == null) value = "";
            macros.put(key, value);
        } else {
            macros.remove(key);
        }
    }

    static void initExampleMacros() {
        dirty = true;
        macros.put("#my_aim", "get_caster,entity_pos/eye,get_caster,get_entity_look");
        macros.put("#is_sneaking", "get_caster,get_entity_height,num_1.75,less");
        macros.put("#hello_world", "(print)(comment_Hello,comment_World)for_each,pop");
        macros.put("#debug", "stack_len,last_n_list,print,splat");
        macros.put("#return", "(())splat,pop,eval");
    }

    // ========== LOCAL MATCH ==========
    public static boolean preMatch(String key) {
        return macros.containsKey(key);
    }

    // ========== SYNC ==========
    public static CompoundTag serialize() {
        var pack = new CompoundTag();
        for (var pair : macros.entrySet()) {
            pack.putString(pair.getKey(), pair.getValue());
        }
        return pack;
    }

    // ========== DISPLAY ==========

}
