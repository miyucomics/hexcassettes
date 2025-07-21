package miyucomics.hexcassettes.client

import miyucomics.hexcassettes.HexcassettesClient
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import kotlin.math.max
import kotlin.math.min

class CassetteScreen : Screen(Text.of("Cassettes")) {
	private val panelWidth = 20
	private var translateOffset = 0.0
	private var isClosingTriggered = false
	private val animationDuration = 1.0
	private var animationProgress = 0.0

	override fun init() {
		super.init()
		translateOffset = panelWidth.toDouble()
		animationProgress = 0.0
		for (i in 0 until ClientStorage.ownedCassettes)
			addDrawableChild(CassetteWidget(i, width - 16, 16 + i * 25))
	}

	override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
		val minecraftClient = MinecraftClient.getInstance()

		if (!isClosingTriggered) {
			animationProgress = min(1.0, animationProgress + (delta / animationDuration))
		} else {
			animationProgress = max(0.0, animationProgress - (delta / animationDuration))
			if (animationProgress <= 0.001 && isClosingTriggered) {
				minecraftClient.setScreen(null)
				return
			}
		}

		translateOffset = panelWidth * (1.0 - animationProgress)

		val matrices = context.matrices
		matrices.push()
		matrices.translate(translateOffset, 0.0, 0.0)
		super.render(context, mouseX, mouseY, delta)
		matrices.pop()
	}

	override fun shouldPause() = false
	override fun shouldCloseOnEsc() = false

	override fun keyReleased(keycode: Int, scancode: Int, modifiers: Int): Boolean {
		if (HexcassettesClient.CASSETTE_KEYBIND.matchesKey(keycode, scancode)) {
			isClosingTriggered = true
			animationProgress = 1.0 - (translateOffset / panelWidth.toDouble())
		}
		return super.keyReleased(keycode, scancode, modifiers)
	}
}