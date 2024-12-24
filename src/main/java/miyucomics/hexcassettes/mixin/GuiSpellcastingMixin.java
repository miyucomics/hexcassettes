package miyucomics.hexcassettes.mixin;

import at.petrak.hexcasting.client.gui.GuiSpellcasting;
import miyucomics.hexcassettes.client.CassetteWidget;
import miyucomics.hexcassettes.client.ClientStorage;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiSpellcasting.class)
public abstract class GuiSpellcastingMixin extends Screen {
	protected GuiSpellcastingMixin(Text title) {
		super(title);
	}

	@Inject(method = "init", at = @At("TAIL"))
	private void initCassettes(CallbackInfo ci) {
		for (int i = 0; i < ClientStorage.ownedCassettes; i++)
			addDrawableChild(new CassetteWidget(i, width - 11, height - 38 - i * 23));
	}
}