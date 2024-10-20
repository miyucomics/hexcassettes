package miyucomics.hexcassettes.inits

import miyucomics.hexcassettes.HexcassettesUtils
import net.minecraft.sound.SoundEvent
import net.minecraft.util.registry.Registry

object HexcassettesSounds {
	lateinit var CASSETTE_EJECT: SoundEvent

	fun init() {
		CASSETTE_EJECT = register("cassette_eject")
	}

	private fun register(name: String): SoundEvent {
		val id = HexcassettesUtils.id(name)
		val event = SoundEvent(id)
		Registry.register(Registry.SOUND_EVENT, id, event)
		return event
	}
}