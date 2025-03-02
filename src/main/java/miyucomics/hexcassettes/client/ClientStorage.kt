package miyucomics.hexcassettes.client

import miyucomics.hexcassettes.HexcassettesMain

object ClientStorage {
	@JvmField
	var ownedCassettes: Int = 0
	@JvmField
	val mask: MutableList<Boolean> = MutableList(HexcassettesMain.MAX_CASSETTES) { false }

	var selectedCassette = 0
}