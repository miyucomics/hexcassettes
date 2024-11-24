package miyucomics.hexcassettes.client

import com.mojang.blaze3d.systems.RenderSystem
import miyucomics.hexcassettes.HexcassettesMain
import miyucomics.hexcassettes.HexcassettesUtils
import miyucomics.hexcassettes.inits.HexcassettesSounds
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.client.sound.SoundManager
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class CassetteWidget(x: Int, y: Int) : ButtonWidget(x, y, 11, 20, Text.empty(), { }) {
	private var index = 0

	constructor(index: Int) : this(0, index * 23 + 5) {
		this.index = index
	}

	override fun renderButton(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
		RenderSystem.setShader { GameRenderer.getPositionTexShader() }
		RenderSystem.setShaderTexture(0, texture)
		RenderSystem.enableDepthTest()
		if (isActive()) {
			drawTexture(matrices, this.x, this.y, 0f, 20f, this.width, this.height, 11, 40)
			drawTextWithShadow(matrices, MinecraftClient.getInstance().textRenderer, ClientStorage.labels[ClientStorage.labels.keys.elementAt(index)], this.x + 15, this.y + 5, 0xffffff)
		} else {
			drawTexture(matrices, this.x, this.y, 0f, 0f, this.width, this.height, 11, 40)
		}
	}

	override fun playDownSound(soundManager: SoundManager) {
		soundManager.play(PositionedSoundInstance.master(if (isActive()) HexcassettesSounds.CASSETTE_EJECT else HexcassettesSounds.CASSETTE_FAIL, 1.0f, 3.0f))
	}

	override fun onPress() {
		if (isActive()) {
			val buf = PacketByteBufs.create()
			buf.writeString(ClientStorage.labels.keys.elementAt(index))
			ClientPlayNetworking.send(HexcassettesMain.CASSETTE_REMOVE, buf)
		}
	}

	private fun isActive(): Boolean {
		return index < ClientStorage.labels.size
	}

	companion object {
		val texture: Identifier = HexcassettesUtils.id("textures/cassette.png")
	}
}