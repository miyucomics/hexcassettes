package miyucomics.hexcassettes.client

import com.mojang.blaze3d.systems.RenderSystem
import miyucomics.hexcassettes.HexcassettesMain
import miyucomics.hexcassettes.inits.HexcassettesNetworking
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

class CassetteWidget(x: Int, y: Int) : ButtonWidget(x, y, 16, 16, Text.empty(), { }) {
	private var index = 0
	constructor(x: Int, y: Int, index: Int) : this(x, y) {
		this.index = index
	}

	override fun renderButton(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
		RenderSystem.setShader { GameRenderer.getPositionTexShader() }
		RenderSystem.setShaderTexture(0, texture)
		RenderSystem.enableDepthTest()
		if (isActive()) {
			drawTexture(matrices, this.x, this.y, 0f, 16f, this.width, this.height, 16, 32)
			drawTextWithShadow(matrices, MinecraftClient.getInstance().textRenderer, Text.literal(ClientStorage.UUIDToLabel[ClientStorage.indexToUUID[index]]), this.x + 15, this.y + 4, 0xffffff)
		} else {
			drawTexture(matrices, this.x, this.y, 0f, 0f, this.width, this.height, 16, 32)
		}
	}

	override fun playDownSound(soundManager: SoundManager) {
		if (isActive())
			soundManager.play(PositionedSoundInstance.master(HexcassettesSounds.CASSETTE_EJECT, 1.0f))
	}

	override fun onPress() {
		if (isActive()) {
			val buf = PacketByteBufs.create()
			buf.writeUuid(ClientStorage.indexToUUID[index])
			ClientPlayNetworking.send(HexcassettesNetworking.CASSETTE_REMOVE, buf)
			val removed = ClientStorage.indexToUUID.removeAt(index)
			ClientStorage.UUIDToLabel.remove(removed)
		}
	}

	private fun isActive(): Boolean {
		return index < ClientStorage.indexToUUID.size
	}

	companion object {
		val texture: Identifier = HexcassettesMain.id("textures/cassette.png")
	}
}