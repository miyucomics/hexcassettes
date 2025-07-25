package miyucomics.hexcassettes.client

import miyucomics.hexcassettes.HexcassettesMain
import miyucomics.hexcassettes.inits.HexcassettesNetworking
import miyucomics.hexcassettes.inits.HexcassettesSounds
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.client.sound.SoundManager
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class CassetteWidget(val index: Int, x: Int, y: Int) : ButtonWidget(x, y, 16, 20, Text.empty(), { }, { supplier -> supplier.get() }) {
	override fun renderButton(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
		if (isActive()) {
			context.drawTexture(texture, this.x, this.y, 0f, 20f, this.width, this.height, 16, 48)
			val renderer = MinecraftClient.getInstance().textRenderer
			val text = ClientStorage.activeCassettes[index]
			renderer.getWidth(text)
			context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, ClientStorage.activeCassettes[index], this.x - renderer.getWidth(text), this.y + renderer.fontHeight / 2, 0xff_ffffff.toInt())
		} else {
			context.drawTexture(texture, this.x, this.y, 0f, 0f, this.width, this.height, 16, 48)
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