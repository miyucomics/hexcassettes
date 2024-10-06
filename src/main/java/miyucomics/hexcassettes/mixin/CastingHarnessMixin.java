package miyucomics.hexcassettes.mixin;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import at.petrak.hexcasting.api.spell.casting.sideeffects.OperatorSideEffect;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import miyucomics.hexcassettes.CastingUtils;
import miyucomics.hexcassettes.data.SilentMarker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = CastingHarness.class, priority = 900)
public class CastingHarnessMixin {
	@Unique
	private final CastingHarness hexcassettes$harness = (CastingHarness) (Object) this;

	@WrapOperation(method = "updateWithPattern", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
	private boolean silenceCasting(List<OperatorSideEffect> instance, Object effect, Operation<Boolean> original) {
		if (((SilentMarker) (Object) hexcassettes$harness.getCtx()).isDelayCast())
			return true;
		return original.call(instance, effect);
	}

	@WrapWithCondition(method = "executeIotas", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"))
	private boolean silenceCasting(ServerWorld world, PlayerEntity player, double x, double y, double z, SoundEvent event, SoundCategory type, float volume, float pitch) {
		return !((SilentMarker) (Object) hexcassettes$harness.getCtx()).isDelayCast();
	}

	@SuppressWarnings("DataFlowIssue")
	@Inject(method = "withdrawMedia", at = @At("HEAD"), cancellable = true, remap = false)
	private void takeMediaFromArchLamp(int mediaCost, boolean allowOvercast, CallbackInfoReturnable<Integer> cir) {
		CastingContext ctx = hexcassettes$harness.getCtx();
		if (ctx.getCaster().isCreative()) {
			cir.setReturnValue(0);
			return;
		}
		if (((SilentMarker) (Object) hexcassettes$harness.getCtx()).isDelayCast())
			cir.setReturnValue(CastingUtils.takeMediaFromInventory((CastingHarness) (Object) this, mediaCost));
	}
}