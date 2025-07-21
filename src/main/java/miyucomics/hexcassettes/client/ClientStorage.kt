package miyucomics.hexcassettes.client

import net.minecraft.text.Text

object ClientStorage {
	var ownedCassettes: Int = 0
	var activeCassettes: MutableList<Text> = mutableListOf()
}