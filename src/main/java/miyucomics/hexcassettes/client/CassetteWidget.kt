package miyucomics.hexcassettes.client

import at.petrak.hexcasting.client.render.PatternColors
import at.petrak.hexcasting.client.render.PatternRenderer
import at.petrak.hexcasting.client.render.WorldlyPatternRenderHelpers
import com.mojang.blaze3d.systems.RenderSystem
import miyucomics.hexcassettes.HexcassettesMain
import miyucomics.hexcassettes.inits.HexcassettesNetworking
import miyucomics.hexcassettes.inits.HexcassettesSounds
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.client.sound.SoundManager
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class CassetteWidget(val index: Int, x: Int, y: Int) : ButtonWidget(x, y, 11, 20, Text.empty(), { }, { supplier -> supplier.get() }) {
	override fun renderButton(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
		RenderSystem.setShader { GameRenderer.getPositionTexProgram() }
		RenderSystem.setShaderTexture(0, texture)
		RenderSystem.enableDepthTest()
		if (isActive()) {
			context.drawTexture(texture, this.x, this.y, 0f, 20f, this.width, this.height, 11, 40)

			val matrices = context.matrices
			matrices.push()
			matrices.translate(this.x.toDouble() - 16, this.y.toDouble(), 0.0)
			matrices.scale(20f, 20f, 20f)
			PatternRenderer.renderPattern(ClientStorage.activeCassettes[index], context.matrices, null, WorldlyPatternRenderHelpers.WORLDLY_SETTINGS_WOBBLY, PatternColors.SLATE_WOBBLY_PURPLE_COLOR, 0.0, 10)
			matrices.pop()
		} else {
			context.drawTexture(texture, this.x, this.y, 0f, 0f, this.width, this.height, 11, 40)
		}
	}

	override fun playDownSound(soundManager: SoundManager) {
		soundManager.play(PositionedSoundInstance.master(if (isActive()) HexcassettesSounds.CASSETTE_EJECT else HexcassettesSounds.CASSETTE_FAIL, 1.0f, 3.0f))
	}

	override fun onPress() {
		if (isActive()) {
			val buf = PacketByteBufs.create()
			buf.writeString(HexcassettesMain.serializeKey(ClientStorage.activeCassettes[index]))
			ClientPlayNetworking.send(HexcassettesNetworking.CASSETTE_REMOVE, buf)
		}
	}

	private fun isActive(): Boolean {
		return index < ClientStorage.activeCassettes.size
	}

	companion object {
		val texture: Identifier = HexcassettesMain.id("textures/cassette.png")
	}
}