package miyucomics.hexcassettes.data

import miyucomics.hexcassettes.HexcassettesMain
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.MinecraftServer
import net.minecraft.world.PersistentState
import net.minecraft.world.World
import java.util.UUID

class StateStorage : PersistentState() {
	private val states: HashMap<UUID, PlayerState> = HashMap()

	override fun writeNbt(nbt: NbtCompound): NbtCompound {
		states.forEach { (key: UUID, element: PlayerState) -> nbt.put(key.toString(), element.serialize()) }
		return nbt
	}

	companion object {
		private fun createFromNbt(nbt: NbtCompound): StateStorage {
			val state = StateStorage()
			nbt.keys.forEach { key -> state.states[UUID.fromString(key)] =
				PlayerState.deserialize(nbt.getCompound(key))
			}
			return state
		}

		@JvmStatic
		fun getServerState(server: MinecraftServer): StateStorage {
			return server.getWorld(World.OVERWORLD)!!.persistentStateManager.getOrCreate(
				Companion::createFromNbt, ::StateStorage,
				HexcassettesMain.MOD_ID
			)
		}

		@JvmStatic
		fun getPlayerState(player: PlayerEntity): PlayerState {
			val state = getServerState(player.server!!)
			if (!state.states.containsKey(player.uuid))
				state.states[player.uuid] = PlayerState()
			state.markDirty()
			return state.states[player.uuid]!!
		}
	}
}