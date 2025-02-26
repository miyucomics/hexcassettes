package miyucomics.hexcassettes.client

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
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
		ClientStorage.selectedCassette = Math.floorMod(ClientStorage.selectedCassette, NUMBER_OF_CASSETTES)
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

		val matrices = context.matrices
		val centerX = this.width / 2
		val centerY = this.height / 2

		val currentTime = System.currentTimeMillis()
		val elapsedTime = (currentTime - lastUpdateTime) / 1000.0f
		lastUpdateTime = currentTime

		val rawDiff = (ClientStorage.selectedCassette - interpolatedIndex + NUMBER_OF_CASSETTES) % NUMBER_OF_CASSETTES
		val diff = if (rawDiff > NUMBER_OF_CASSETTES / 2) rawDiff - NUMBER_OF_CASSETTES else rawDiff
		interpolatedIndex += diff * 0.15f * elapsedTime * 60

		val client = MinecraftClient.getInstance()

		val trueIndex = Math.floorMod(ClientStorage.selectedCassette, NUMBER_OF_CASSETTES)

		(0 until NUMBER_OF_CASSETTES).sortedBy { i -> -abs(i - trueIndex) }.forEach { i ->
			val radians = ((i - interpolatedIndex) / NUMBER_OF_CASSETTES) * 2 * PI
			val x = centerX + RADIUS * sin(radians) - CASSETTE_WIDTH / 2
			val y = centerY + RADIUS * cos(radians) * 0.5f - CASSETTE_HEIGHT / 2

			val scale = 0.6f + 0.4f * (1 + cos(radians))
			val skew = MathHelper.clamp(sin(radians) * 0.3f, -0.3f, 0.3f)

			matrices.push()
			matrices.translate(x + CASSETTE_WIDTH / 2, y + CASSETTE_HEIGHT / 2, 0f)
			matrices.scale(scale, scale, 1f)
			matrices.multiply(RotationAxis.POSITIVE_Z.rotation(skew))
			context.drawCenteredTextWithShadow(client.textRenderer, i.toString(), 0, 0, 0)
			matrices.translate(-CASSETTE_WIDTH / 2f, -CASSETTE_HEIGHT / 2f, 0f)
			context.fill(0, 0, CASSETTE_WIDTH, CASSETTE_HEIGHT, if (i == trueIndex) -0x5600 else -0x55010000)
			matrices.pop()
		}
	}

	companion object {
		private const val PI = 3.1415927f
		private const val NUMBER_OF_CASSETTES = 6
		private const val CASSETTE_WIDTH = 100
		private const val CASSETTE_HEIGHT = 80
		private const val RADIUS = 100
	}
}