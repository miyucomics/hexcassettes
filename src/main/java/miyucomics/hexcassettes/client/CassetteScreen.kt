package miyucomics.hexcassettes.client

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text

class CassetteScreen : Screen(Text.of("Cassettes")) {
	override fun init() {
		for (i in 0 until ClientStorage.ownedCassettes)
			addDrawableChild(CassetteWidget(i, 16, i * 25 + 16))
		super.init()
	}

	override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
		context.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680)
		super.render(context, mouseX, mouseY, delta)
	}

	override fun shouldPause() = false
}