package miyucomics.hexcassettes.mixin;

import miyucomics.hexcassettes.client.CassetteWidget;
import miyucomics.hexcassettes.client.ClientStorage;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {
	@Unique
	public List<CassetteWidget> cassettes;

	protected ChatScreenMixin(Text title) {
		super(title);
	}

	@Inject(method = "init", at = @At("TAIL"))
	private void initCassettes(CallbackInfo ci) {
		cassettes = new ArrayList<>();
		for (int i = 0; i < ClientStorage.ownedCassettes; i++) {
			var widget = new CassetteWidget(0, 10 + i * 18, i);
			cassettes.add(widget);
			addDrawableChild(widget);
		}
	}
}