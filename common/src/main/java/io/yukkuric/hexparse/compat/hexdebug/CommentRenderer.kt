package io.yukkuric.hexparse.compat.hexdebug

import at.petrak.hexcasting.api.utils.darkGreen
import gay.`object`.hexdebug.api.client.splicing.SplicingTableIotaRenderer
import gay.`object`.hexdebug.api.client.splicing.SplicingTableIotaRendererParser
import gay.`object`.hexdebug.api.client.splicing.SplicingTableIotaRenderers
import gay.`object`.hexdebug.api.splicing.SplicingTableIotaClientView
import gay.`object`.hexdebug.utils.letPushPose
import gay.`object`.hexdebug.utils.scale
import io.yukkuric.hexparse.hooks.CommentIotaType
import io.yukkuric.hexparse.parsers.IotaFactory
import io.yukkuric.hexparse.parsers.nbt2str.CommentParser
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.nbt.StringTag
import net.minecraft.network.chat.Component

class CommentRenderer(iota: SplicingTableIotaClientView, x: Int, y: Int) :
    SplicingTableIotaRenderer(CommentIotaType.INSTANCE, iota, x, y) {
    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        guiGraphics.pose().letPushPose { pose ->
            val content = (iota.data as StringTag).asString
            var tabSize = 0
            val displayString = if (CommentParser.INDENT.matcher(content).find()) {
                tabSize = content.length - 1
                "\\n"
            } else if (content.startsWith(IotaFactory.GREAT_PLACEHOLDER_PREFIX)) "?"
            else if (content.length <= 3) content
            else content.substring(0, 2) + "."

            val font = Minecraft.getInstance().font
            val halfWidth = font.width(displayString) / 2
            pose.translate(x + 9.0, y + 7.0, 0.0)
            guiGraphics.drawString(
                font, Component.literal(displayString).darkGreen, -halfWidth, 0, (0xffffffff).toInt(), false
            )
            if (tabSize > 0) {
                pose.translate(4.0, -2.5, 0.0)
                pose.scale(0.5f)
                val sizeText = "+${tabSize}"
                val halfWidth2 = font.width(sizeText) / 2
                guiGraphics.drawString(
                    font, Component.literal(sizeText).darkGreen, -halfWidth2, 0, (0xffffffff).toInt(), false
                )
            }
        }
    }

    companion object {
        @JvmStatic
        fun registerSelf() {
            SplicingTableIotaRenderers.register(
                net.minecraft.resources.ResourceLocation(CommentIotaType.TYPE_ID),
                SplicingTableIotaRendererParser.simple { _, iota, x, y -> CommentRenderer(iota, x, y) }
            )
        }
    }
}