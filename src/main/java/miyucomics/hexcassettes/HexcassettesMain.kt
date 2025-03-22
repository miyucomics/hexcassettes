package miyucomics.hexcassettes

import at.petrak.hexcasting.api.HexAPI
import miyucomics.hexcassettes.inits.HexcassettesAdvancements
import miyucomics.hexcassettes.inits.HexcassettesNetworking
import miyucomics.hexcassettes.inits.HexcassettesPatterns
import miyucomics.hexcassettes.inits.HexcassettesSounds
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageType
import net.minecraft.item.FoodComponent
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.util.Identifier
import net.minecraft.util.Rarity
import net.minecraft.world.World

class HexcassettesMain : ModInitializer {
	override fun onInitialize() {
		val cassette = CassetteItem()
		Registry.register(Registries.ITEM, id("cassette"), cassette)
		ItemGroupEvents.modifyEntriesEvent(RegistryKey.of(Registries.ITEM_GROUP.key, HexAPI.modLoc("hexcasting"))).register { group -> group.add(cassette) }

		HexcassettesAdvancements.init()
		HexcassettesNetworking.init()
		HexcassettesPatterns.init()
		HexcassettesSounds.init()
	}

	companion object {
		const val MOD_ID: String = "hexcassettes"
		const val MAX_CASSETTES: Int = 6
		fun id(string: String) = Identifier(MOD_ID, string)

		val BAD_QUINE: RegistryKey<DamageType> = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, id("bad_quine"))
	}
}

// kinda messy, but I don't want to make a whole file for these
class CassetteItem : Item(Settings().maxCount(1).rarity(Rarity.UNCOMMON).food(FoodComponent.Builder().alwaysEdible().build())) {
	override fun getMaxUseTime(stack: ItemStack) = 100
	override fun getEatSound() = HexcassettesSounds.CASSETTE_LOOP

	override fun finishUsing(stack: ItemStack, world: World, user: LivingEntity): ItemStack {
		if (world.isClient) {
			world.playSound(user.x, user.y, user.z, HexcassettesSounds.CASSETTE_INSERT, SoundCategory.MASTER, 5f, 1f, false)
			return super.finishUsing(stack, world, user)
		}

		if (user !is ServerPlayerEntity)
			return super.finishUsing(stack, world, user)

		val cassetteData = (user as PlayerEntityMinterface).getCassetteState()
		if (cassetteData.ownedSlots < HexcassettesMain.MAX_CASSETTES) {
			HexcassettesAdvancements.TAPE_WORM.trigger(user)
			cassetteData.ownedSlots += 1
			if (cassetteData.ownedSlots == HexcassettesMain.MAX_CASSETTES)
				HexcassettesAdvancements.FULL_ARSENAL.trigger(user)
			cassetteData.sync(user)
		}
		return super.finishUsing(stack, world, user)
	}
}