package miyucomics.hexcassettes.mixin;

import miyucomics.hexcassettes.client.CassetteWidget;
import miyucomics.hexcassettes.client.ClientStorage;
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
	@Unique
	public List<CassetteWidget> cassettes;

	public InventoryScreenMixin(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
		super(screenHandler, playerInventory, text);
	}

	@Inject(method = "init", at = @At("TAIL"))
	private void initCassettes(CallbackInfo ci) {
		cassettes = new ArrayList<>();
		for (int i = 0; i < ClientStorage.ownedCassettes; i++) {
			var widget = new CassetteWidget(x + 176, y + 4 + i * 18, i);
			cassettes.add(widget);
			addDrawableChild(widget);
		}
	}
}