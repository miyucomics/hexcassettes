package miyucomics.hexcassettes;

import miyucomics.hexcassettes.advancements.QuineCriterion;
import miyucomics.hexcassettes.inits.HexcassettesNetworking;
import miyucomics.hexcassettes.inits.HexcassettesPatterns;
import miyucomics.hexcassettes.inits.HexcassettesSounds;
import net.fabricmc.api.ModInitializer;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.util.registry.Registry;

public class HexcassettesMain implements ModInitializer {
	public static final String MOD_ID = "hexcassettes";
	public static final int MAX_CASSETTES = 6;
	public static final int MAX_LABEL_LENGTH = 20;

	public static QuineCriterion QUINE = null;

	@Override
	public void onInitialize() {
		QUINE = Criteria.register(new QuineCriterion());
		Registry.register(Registry.ITEM, HexcassettesUtils.id("cassette"), new CassetteItem());

		HexcassettesNetworking.init();
		HexcassettesPatterns.init();
		HexcassettesSounds.init();
	}
}