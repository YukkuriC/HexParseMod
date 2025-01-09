package io.yukkuric.hexparse.mixin.patchouli;

import at.petrak.hexcasting.interop.patchouli.AbstractPatternComponent;
import at.petrak.hexcasting.interop.patchouli.LookupPatternComponent;
import io.yukkuric.hexparse.mixin_interface.IGetOpName;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;

@Mixin(AbstractPatternComponent.class)
public abstract class MixinPatternRenderer implements ICustomComponent {
    @Inject(method = "render", at = @At("RETURN"))
    public void render(GuiGraphics graphics, IComponentRenderContext context, float pticks, int mouseX, int mouseY, CallbackInfo ci) {
        if (!Screen.hasShiftDown()) return;
        if (!(this instanceof IGetOpName getter)) return;
        var opName = getter.getOpName();
        Component toRender = Component.literal(opName.toString())
                .setStyle(context.getFont().withColor(ChatFormatting.BLACK));
        graphics.drawString(Minecraft.getInstance().font, toRender, 5, 10, -1, false);
    }

    @Mixin(LookupPatternComponent.class)
    public static abstract class AccessorLookup implements ICustomComponent, IGetOpName {
        @Shadow
        protected ResourceLocation opName;

        public ResourceLocation getOpName() {
            return opName;
        }
    }
}
