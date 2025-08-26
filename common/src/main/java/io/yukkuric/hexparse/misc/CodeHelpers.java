package io.yukkuric.hexparse.misc;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.item.IotaHolderItem;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.items.storage.*;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import io.yukkuric.hexparse.HexParse;
import io.yukkuric.hexparse.hooks.GreatPatternUnlocker;
import io.yukkuric.hexparse.hooks.PatternMapper;
import io.yukkuric.hexparse.parsers.ParserMain;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static at.petrak.hexcasting.common.items.storage.ItemSpellbook.TAG_PAGES;

public interface CodeHelpers {
    class IOMethod {
        private final BiConsumer<ItemStack, CompoundTag> writer;
        private final Function<ItemStack, CompoundTag> reader;
        private ItemStack current;

        private static Map<Class<? extends IotaHolderItem>, IOMethod> ITEM_IO_TYPES = new HashMap<>();

        public IOMethod(Class<? extends IotaHolderItem> cls, BiConsumer<ItemStack, CompoundTag> writer,
                        Function<ItemStack, CompoundTag> reader) {
            this.reader = reader;
            this.writer = writer;
            ITEM_IO_TYPES.put(cls, this);
        }
        public void write(CompoundTag nbt) {
            writer.accept(current, nbt);
        }
        public CompoundTag read() {
            if (reader == null) return ((IotaHolderItem) current.getItem()).readIotaTag(current);
            return reader.apply(current);
        }
        public void bind(ItemStack stack) {
            this.current = stack;
        }
        public void rename(String newName) {
            current.setHoverName(Component.literal(newName));
        }

        public Iota readIota(ServerLevel world) {
            return ((IotaHolderItem) current.getItem()).readIota(current, world);
        }

        static IOMethod get(ItemStack stack) {
            if (stack == null) return null;
            var ret = ITEM_IO_TYPES.get(stack.getItem().getClass());
            if (ret != null) ret.bind(stack);
            return ret;
        }

        static {
            BiConsumer<ItemStack, CompoundTag> simpleWrite = (target, nbt) -> target.getOrCreateTag().put("data", nbt);
            new IOMethod(ItemFocus.class, simpleWrite, null);
            new IOMethod(ItemThoughtKnot.class, simpleWrite, null);
            new IOMethod(ItemSpellbook.class, (stack, nbt) -> {
                int idx = ItemSpellbook.getPage(stack, 1);
                String pageKey = String.valueOf(idx);
                NBTHelper.getOrCreateCompound(stack, TAG_PAGES).put(pageKey, nbt);
            }, null);
        }
    }

    static void doExtractMedia(ServerPlayer caster, long amount) {
        var harness = IXplatAbstractions.INSTANCE.getStaffcastVM(caster, InteractionHand.MAIN_HAND);
        harness.getEnv().extractMedia(amount, false);
    }

    static IOMethod getItemIO(ServerPlayer player) {
        if (player == null) return null;
        var ret = IOMethod.get(player.getMainHandItem());
        if (ret == null) ret = IOMethod.get(player.getOffhandItem());
        return ret;
    }

    static void doParse(ServerPlayer player, String code, String rename) {
        var target = getItemIO(player);
        if (target == null) return;
        var nbt = ParserMain.ParseCode(code, player);
        target.write(nbt);
        if (rename != null) target.rename(rename);
    }

    static void doParse(ServerPlayer player, List<String> code, String rename) {
        var target = getItemIO(player);
        if (target == null) return;
        var nbt = ParserMain.ParseCode(code, player);
        target.write(nbt);
        if (rename != null) target.rename(rename);
    }

    static String readHand(ServerPlayer player) {
        return readHand(player, StringProcessors.READ_DEFAULT);
    }
    static String readHand(ServerPlayer player, StringProcessors.F post) {
        var target = getItemIO(player);
        if (target == null) return null;
        var iotaRoot = target.read();
        if (iotaRoot == null) return null;
        autoRefresh(player.getServer());
        return ParserMain.ParseIotaNbt(iotaRoot, player, post);
    }

    WeakReference<MinecraftServer> refreshedWorld = new WeakReference<>(null);
    WeakReference<Boolean> refreshedLocal = new WeakReference<>(false);

    static void autoRefresh(MinecraftServer server) {
        if (server != refreshedWorld.get()) {
            var level = server.overworld();
            HexParse.LOGGER.info("auto refresh for server: %s, level: %s".formatted(server.name(), level));
            PatternMapper.init(level);
            refreshedLocal.refersTo(true);
            refreshedWorld.refersTo(server);
        }
    }

    static void autoRefreshLocal() {
        if (refreshedLocal.get()) return;
        PatternMapper.initLocal();
        refreshedLocal.refersTo(true);
    }

    static void displayCode(ServerPlayer player, String code) {
        if (player == null || code == null) return;
        var display = Component.translatable("hexparse.cmd.read.display", Component.literal(code).withStyle(ChatFormatting.WHITE)).withStyle(ChatFormatting.GREEN);
        player.sendSystemMessage(wrapClickCopy(display, code));
    }

    static MutableComponent wrapClickCopy(MutableComponent component, String code) {
        return component.withStyle(
                Style.EMPTY
                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, code))
                        .withHoverEvent(HoverEvent.Action.SHOW_TEXT.deserializeFromLegacy(Component.translatable("chat.copy.click")))
        );
    }
    static MutableComponent wrapClickSuggest(MutableComponent component, String command) {
        return component.withStyle(
                Style.EMPTY
                        .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command))
                        .withHoverEvent(HoverEvent.Action.SHOW_TEXT.deserializeFromLegacy(Component.literal(command)))
        );
    }

    static Component getPatternDisplay(ResourceLocation id, ServerLevel level) {
        var longName = id.toString();

        // check great pattern display
        if (PatternMapper.mapPatternWorld.containsKey(longName)) {
            if (!GreatPatternUnlocker.get(level).isUnlocked(longName)) return Component.literal("???");
        }

        CompoundTag rawIota = null;
        for (var map : PatternMapper.ShortNameTracker.modifyTargets) {
            if (map.containsKey(longName)) {
                rawIota = map.get(longName);
                break;
            }
        }
        if (rawIota == null) return Component.literal("NULL");
        return IotaType.getDisplay(rawIota);
    }
}
