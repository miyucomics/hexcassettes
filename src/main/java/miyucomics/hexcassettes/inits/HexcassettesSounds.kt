package miyucomics.hexcassettes.inits

import miyucomics.hexcassettes.HexcassettesMain
import net.minecraft.sound.SoundEvent
import net.minecraft.util.registry.Registry

object HexcassettesSounds {
	lateinit var CASSETTE_EJECT: SoundEvent

	@JvmStatic
	fun init() {
		CASSETTE_EJECT = register("cassette_eject")
	}

	private fun register(name: String): SoundEvent {
		val id = HexcassettesMain.id(name)
		val event = SoundEvent(id)
		Registry.register(Registry.SOUND_EVENT, id, event)
		return event
	}
}