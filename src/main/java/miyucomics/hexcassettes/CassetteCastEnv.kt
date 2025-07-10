package miyucomics.hexcassettes

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Hand

class CassetteCastEnv(caster: ServerPlayerEntity, castingHand: Hand, val pattern: HexPattern, val depth: Int = 0) : PlayerBasedCastEnv(caster, castingHand) {
	init {
		if (depth >= 9)
			HexcassettesMain.QUINIO.trigger(caster)
	}

	override fun getCastingHand(): Hand = this.castingHand
	override fun produceParticles(particles: ParticleSpray, pigment: FrozenPigment) {}
	override fun getPigment(): FrozenPigment = IXplatAbstractions.INSTANCE.getPigment(this.caster)

	public override fun extractMediaEnvironment(costLeft: Long, simulate: Boolean): Long {
		if (caster.isCreative)
			return 0
		return this.extractMediaFromInventory(costLeft, true, simulate)
	}
}