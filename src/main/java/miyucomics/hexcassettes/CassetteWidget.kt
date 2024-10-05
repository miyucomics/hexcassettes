package miyucomics.hexcassettes

import net.minecraft.client.gui.widget.TexturedButtonWidget
import net.minecraft.util.Identifier

class CassetteWidget(x: Int, y: Int, width: Int, height: Int) : TexturedButtonWidget(x, y, width, height, 0, 0, 0, Identifier("textures/mob_effect/blindness.png"), 16, 16, { }) {
	private var index: Int = 0

	constructor(x: Int, y: Int, width: Int, height: Int, index: Int) : this(x, y, width, height) {
		this.index = index
	}

	override fun onPress() {
		println(this.index)
	}
}