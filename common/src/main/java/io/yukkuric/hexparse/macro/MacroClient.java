package io.yukkuric.hexparse.macro;

import com.google.gson.Gson;
import io.yukkuric.hexparse.HexParse;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

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
            HashMap<String, Object> raw = GSON.fromJson(br, HashMap.class);
            for (var pair : raw.entrySet())
                macros.put(pair.getKey(), String.valueOf(pair.getValue()));
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
}
