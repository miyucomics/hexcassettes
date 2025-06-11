package miyucomics.hexcassettes.mixin;

import miyucomics.hexcassettes.PlayerEntityMinterface;
import miyucomics.hexcassettes.data.CassetteState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements PlayerEntityMinterface {
	@Unique
	private CassetteState cassetteState = new CassetteState();

	@Inject(method = "tick", at = @At("HEAD"))
	private void runCassettes(CallbackInfo ci) {
		PlayerEntity player = (PlayerEntity) (Object) this;
		if (player.getWorld().isClient)
			return;
		cassetteState.tick((ServerPlayerEntity) player);
	}

	@Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
	private void readCassetteState(NbtCompound compound, CallbackInfo ci) {
		if (compound.contains("cassettes"))
			cassetteState = CassetteState.deserialize(compound.getCompound("cassettes"));
	}

	@Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
	private void writeCassetteState(NbtCompound compound, CallbackInfo ci) {
		compound.put("cassettes", cassetteState.serialize());
	}

	@Override
	public @NotNull CassetteState getCassetteState() {
		return cassetteState;
	}
}