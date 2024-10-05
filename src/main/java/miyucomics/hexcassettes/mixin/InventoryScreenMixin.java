package miyucomics.hexcassettes.mixin;

import miyucomics.hexcassettes.CassetteWidget;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> {
	public InventoryScreenMixin(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
		super(screenHandler, playerInventory, text);
	}

	@Unique
	public List<CassetteWidget> cassettes;

	@Inject(method = "init", at = @At("TAIL"))
	private void initCassettes(CallbackInfo ci) {
		cassettes = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			var widget = new CassetteWidget(x + 156 + 19, y + i * 18, 14, 14, i);
			cassettes.add(widget);
			addDrawableChild(widget);
		}
	}

	@Inject(method = "handledScreenTick", at = @At("TAIL"))
	private void tickCassettes(CallbackInfo ci) {

	}
}