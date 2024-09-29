package miyucomics.hexcassettes.mixin;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import miyucomics.hexcassettes.SilentMarker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = CastingContext.class)
public class CastingContextMixin implements SilentMarker {
	@Unique
	private boolean hexical$delayCast = false;

	@Override
	public void delayCast() {
		hexical$delayCast = true;
	}

	@Override
	public boolean isDelayCast() {
		return hexical$delayCast;
	}
}