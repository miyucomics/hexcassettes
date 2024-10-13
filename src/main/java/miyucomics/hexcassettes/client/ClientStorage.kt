package miyucomics.hexcassettes.client

import java.util.*

object ClientStorage {
	@JvmField
	var ownedCassettes = 0
	val indexToUUID = mutableListOf<UUID>()
	val UUIDToLabel = mutableMapOf<UUID, String>()
}