package miyucomics.hexcassettes.data

import at.petrak.hexcasting.api.utils.putCompound
import miyucomics.hexcassettes.HexcassettesUtils
import miyucomics.hexcassettes.inits.HexcassettesNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.network.ServerPlayerEntity
import java.util.*

data class QueuedHex(val hex: NbtCompound, var delay: Int, val label: String, val uuid: UUID) {
	constructor(hex: NbtCompound, delay: Int, label: String): this(hex, delay, label, UUID.randomUUID())

	fun serialize(): NbtCompound {
		val compound = NbtCompound()
		compound.putCompound("hex", hex)
		compound.putInt("delay", delay)
		compound.putString("label", label)
		return compound
	}

	fun cast(player: ServerPlayerEntity) {
		HexcassettesUtils.cast(player.getWorld(), player, hex)
		val buf = PacketByteBufs.create()
		buf.writeUuid(uuid)
		ServerPlayNetworking.send(player, HexcassettesNetworking.CASSETTE_REMOVE, buf)
	}

	companion object {
		fun deserialize(compound: NbtCompound) = QueuedHex(compound.getCompound("hex"), compound.getInt("delay"), compound.getString("label"))
	}
}