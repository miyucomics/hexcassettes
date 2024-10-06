package miyucomics.hexcassettes.mixin;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import miyucomics.hexcassettes.data.SilentMarker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(value = CastingContext.class)
public class CastingContextMixin implements SilentMarker {
	@Unique
	private boolean hexcassettes$delayCast = false;

	@Override
	public void delayCast() {
		hexcassettes$delayCast = true;
	}

	@Override
	public boolean isDelayCast() {
		return hexcassettes$delayCast;
	}
}