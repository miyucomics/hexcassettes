package miyucomics.hexcassettes.client

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RotationAxis
import org.lwjgl.glfw.GLFW
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

class CassetteScreen : Screen(Text.literal("Cassette Screen")) {
	private var lastUpdateTime = System.currentTimeMillis()
	private var interpolatedIndex = 0f

	init {
		if (ClientStorage.ownedCassettes != 0)
			ClientStorage.selectedCassette = Math.floorMod(ClientStorage.selectedCassette, ClientStorage.ownedCassettes)
		interpolatedIndex = ClientStorage.selectedCassette - 2f
	}

	override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
		when (keyCode) {
			GLFW.GLFW_KEY_A -> ClientStorage.selectedCassette -= 1
			GLFW.GLFW_KEY_S -> ClientStorage.selectedCassette += 1
			GLFW.GLFW_KEY_H -> ClientStorage.selectedCassette -= 1
			GLFW.GLFW_KEY_L -> ClientStorage.selectedCassette += 1
			GLFW.GLFW_KEY_LEFT -> ClientStorage.selectedCassette -= 1
			GLFW.GLFW_KEY_RIGHT -> ClientStorage.selectedCassette += 1
		}
		return super.keyPressed(keyCode, scanCode, modifiers)
	}

	override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
		super.render(context, mouseX, mouseY, delta)

		if (ClientStorage.ownedCassettes == 0)
			return

		val matrices = context.matrices
		val centerX = this.width / 2
		val centerY = this.height / 2

		val currentTime = System.currentTimeMillis()
		val elapsedTime = (currentTime - lastUpdateTime) / 1000.0f
		lastUpdateTime = currentTime

		val rawDiff = (ClientStorage.selectedCassette - interpolatedIndex + ClientStorage.ownedCassettes) % ClientStorage.ownedCassettes
		val diff = if (rawDiff > ClientStorage.ownedCassettes / 2) rawDiff - ClientStorage.ownedCassettes else rawDiff
		interpolatedIndex += diff * 0.15f * elapsedTime * 60

		val trueIndex = Math.floorMod(ClientStorage.selectedCassette, ClientStorage.ownedCassettes)
		(0 until ClientStorage.ownedCassettes).sortedBy { i -> -abs(i - trueIndex) }.forEach { i ->
			val radians = ((i - interpolatedIndex) / ClientStorage.ownedCassettes) * 2 * PI
			val x = centerX + RADIUS * sin(radians)
			val y = centerY + RADIUS * cos(radians) * 0.5f + sin(currentTime.toDouble() / 1000f + i * 10f).toFloat() * 5f

			val scale = (0.6f + 0.4f * (1 + cos(radians))) * 3
			val skew = MathHelper.clamp(sin(radians) * 0.3f, -0.3f, 0.3f)

			matrices.push()
			matrices.translate(x, y, 0f)
			matrices.scale(scale, scale, 1f)
			matrices.multiply(RotationAxis.POSITIVE_Z.rotation(skew))
			context.drawTexture(Identifier("hexcassettes", "textures/cassette.png"), -16, -8, 0, 0f, 0f, 32, 16, 32, 16)
			matrices.pop()
		}

		matrices.push()
		context.drawText(MinecraftClient.getInstance().textRenderer, "Cassette #$trueIndex", centerX, centerY, 32, false)
		matrices.pop()
	}

	companion object {
		private const val PI = 3.1415927f
		private const val RADIUS = 100
	}
}