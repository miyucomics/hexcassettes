package miyucomics.hexcassettes.patterns

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv
import at.petrak.hexcasting.api.casting.getList
import at.petrak.hexcasting.api.casting.getPositiveInt
import at.petrak.hexcasting.api.casting.getPositiveIntUnder
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import at.petrak.hexcasting.api.casting.mishaps.MishapOthersName
import miyucomics.hexcassettes.CassetteCastEnv
import miyucomics.hexcassettes.HexcassettesMain
import miyucomics.hexcassettes.PlayerEntityMinterface
import miyucomics.hexcassettes.data.QueuedHex
import miyucomics.hexcassettes.inits.HexcassettesAdvancements
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.DyeColor

class OpEnqueue : SpellAction {
	override val argc = 3
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		if (env !is PlayerBasedCastEnv)
			throw MishapBadCaster()

		val caster = (env.castingEntity as ServerPlayerEntity)
		val isQuining = env is CassetteCastEnv
		if (isQuining)
			HexcassettesAdvancements.QUINE.trigger(caster)

		val index = args.getPositiveIntUnder(2, HexcassettesMain.MAX_CASSETTES, argc)

		val cassetteData = (caster as PlayerEntityMinterface).getCassetteState()
		if (index >= cassetteData.ownedSlots)
			throw NotEnoughCassettes()
		if (cassetteData.queuedHexes.size >= cassetteData.ownedSlots)
			throw NotEnoughCassettes()

		args.getList(0, argc)
		val trueName = MishapOthersName.getTrueNameFromDatum(args[0], caster)
		if (trueName != null)
			throw MishapOthersName(trueName)

		return SpellAction.Result(Spell(args[0] as ListIota, args.getPositiveInt(1, argc), args.getPositiveIntUnder(2, HexcassettesMain.MAX_CASSETTES, argc)), 0, listOf())
	}

	private data class Spell(val hex: ListIota, val delay: Int, val index: Int) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			(env.castingEntity as PlayerEntityMinterface).getCassetteState().queuedHexes[index] = QueuedHex(IotaType.serialize(hex), delay)
		}
	}
}

class NotEnoughCassettes : Mishap() {
	override fun accentColor(env: CastingEnvironment, errorCtx: Context) = dyeColor(DyeColor.RED)
	override fun errorMessage(env: CastingEnvironment, errorCtx: Context) = error(HexcassettesMain.MOD_ID + ":too_many_cassettes")
	override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
		val caster = env.castingEntity
		if (caster is ServerPlayerEntity)
			(env.castingEntity as PlayerEntityMinterface).getCassetteState().queuedHexes.replaceAll { null }
	}
}