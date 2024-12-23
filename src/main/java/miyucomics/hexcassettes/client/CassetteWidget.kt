package miyucomics.hexcassettes.client

import com.mojang.blaze3d.systems.RenderSystem
import miyucomics.hexcassettes.HexcassettesUtils
import miyucomics.hexcassettes.inits.HexcassettesNetworking
import miyucomics.hexcassettes.inits.HexcassettesSounds
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.client.sound.SoundManager
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class CassetteWidget(x: Int, y: Int) : ButtonWidget(x, y, 11, 20, Text.empty(), { }, { supplier -> supplier.get() }) {
	private var index = 0

	constructor(index: Int, x: Int, y: Int) : this(x, y) {
		this.index = index
	}

	override fun renderButton(drawContext: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
		RenderSystem.setShader { GameRenderer.getPositionTexProgram() }
		RenderSystem.setShaderTexture(0, texture)
		RenderSystem.enableDepthTest()
		if (isActive()) {
			drawContext.drawTexture(texture, this.x, this.y, 0f, 20f, this.width, this.height, 11, 40)
			val text = ClientStorage.labels[ClientStorage.labels.keys.elementAt(index)] ?: return
			drawContext.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, text, this.x - MinecraftClient.getInstance().textRenderer.getWidth(text) - 10, this.y + 5, 0xffffff)
		} else {
			drawContext.drawTexture(texture, this.x, this.y, 0f, 0f, this.width, this.height, 11, 40)
		}
	}

	override fun playDownSound(soundManager: SoundManager) {
		soundManager.play(PositionedSoundInstance.master(if (isActive()) HexcassettesSounds.CASSETTE_EJECT else HexcassettesSounds.CASSETTE_FAIL, 1.0f, 3.0f))
	}

	override fun onPress() {
		if (isActive()) {
			val buf = PacketByteBufs.create()
			buf.writeString(ClientStorage.labels.keys.elementAt(index))
			ClientPlayNetworking.send(HexcassettesNetworking.CASSETTE_REMOVE, buf)
		}
	}

	private fun isActive(): Boolean {
		return index < ClientStorage.labels.size
	}

	companion object {
		val texture: Identifier = HexcassettesUtils.id("textures/cassette.png")
	}
}