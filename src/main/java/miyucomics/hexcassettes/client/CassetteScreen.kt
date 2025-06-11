package miyucomics.hexcassettes.client

import at.petrak.hexcasting.client.render.PatternColors
import at.petrak.hexcasting.client.render.PatternRenderer
import at.petrak.hexcasting.client.render.WorldlyPatternRenderHelpers
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
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

class CassetteScreen : Screen(Text.translatable("screen.hexical.cassette")) {
	private var lastUpdateTime = System.currentTimeMillis()
	private var interpolatedIndex = 0f

	init {
		if (ClientStorage.ownedCassettes != 0)
			ClientStorage.selectedCassette = Math.floorMod(ClientStorage.selectedCassette, ClientStorage.ownedCassettes)
		interpolatedIndex = ClientStorage.selectedCassette.toFloat()
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

		context.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680)

		if (ClientStorage.ownedCassettes == 0)
			return

		val matrices = context.matrices
		val centerX = this.width / 2f
		val centerY = this.height / 2f

		val currentTime = System.currentTimeMillis()
		val elapsedTime = (currentTime - lastUpdateTime) / 1000.0f
		lastUpdateTime = currentTime

		val diff = circularDiff(ClientStorage.selectedCassette.toFloat(), interpolatedIndex, ClientStorage.ownedCassettes)
		interpolatedIndex += diff * 0.15f * elapsedTime * 60

		val trueIndex = Math.floorMod(ClientStorage.selectedCassette, ClientStorage.ownedCassettes)
		(0 until ClientStorage.ownedCassettes).sortedBy { i -> -abs(i - trueIndex) }.forEach { i ->
			val radians = ((i - interpolatedIndex) / ClientStorage.ownedCassettes) * 2 * PI
			val x = centerX + getRadius() * sin(radians)
			val y = centerY + getRadius() * cos(radians) * SQUASH

			val scale = 1f + 2.5f * (1 + cos(radians)) / 2
			val skew = MathHelper.clamp(sin(radians) * 0.3f, -0.3f, 0.3f)

			matrices.push()
			matrices.translate(x, y + sin(currentTime.toDouble() / 1000f + i * 10f).toFloat() * 5f, 0f)
			matrices.scale(scale, scale, 1f)
			matrices.multiply(RotationAxis.POSITIVE_Z.rotation(skew))
			context.drawTexture(Identifier("hexcassettes", "textures/cassette.png"), -16, -8, 0, 0f, 0f, 32, 16, 32, 16)
			matrices.pop()
		}

		matrices.push()
		matrices.translate(centerX, centerY, 0f)
		matrices.scale(75f, 75f, 75f)
		matrices.translate(-0.5f, -0.5f, 0f)
		if (trueIndex < ClientStorage.activeCassettes.size) {
			PatternRenderer.renderPattern(ClientStorage.activeCassettes[trueIndex], matrices, null, WorldlyPatternRenderHelpers.WORLDLY_SETTINGS_WOBBLY, PatternColors.SLATE_WOBBLY_PURPLE_COLOR, 0.0, 10)
		}
		matrices.pop()
	}

	private fun getRadius(): Float {
		val horizontalRadius = (width - 40f) / 2f
		val verticalRadius = (height - 40f) / (2f * SQUASH)
		return min(min(horizontalRadius, verticalRadius), BASE_RADIUS.toFloat())
	}

	private fun circularDiff(a: Float, b: Float, size: Int): Float {
		val diff = (a - b) % size
		return when {
			diff < -size / 2f -> diff + size
			diff > size / 2f -> diff - size
			else -> diff
		}
	}

	companion object {
		private const val PI = 3.1415927f
		private const val BASE_RADIUS = 175
		private const val SQUASH = 0.5f
	}
}