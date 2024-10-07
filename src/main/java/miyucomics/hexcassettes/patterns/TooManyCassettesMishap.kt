package miyucomics.hexcassettes.patterns

import at.petrak.hexcasting.api.misc.FrozenColorizer
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.Mishap
import miyucomics.hexcassettes.HexcassettesMain
import miyucomics.hexcassettes.data.HexcassettesAPI
import net.minecraft.text.Text
import net.minecraft.util.DyeColor

class TooManyCassettesMishap : Mishap() {
	override fun accentColor(ctx: CastingContext, errorCtx: Context): FrozenColorizer = dyeColor(DyeColor.RED)
	override fun errorMessage(ctx: CastingContext, errorCtx: Context): Text  = error(HexcassettesMain.MOD_ID + ":too_many_cassettes")
	override fun execute(ctx: CastingContext, errorCtx: Context, stack: MutableList<Iota>) {
		HexcassettesAPI.removeAllQueued(ctx.caster)
	}
}