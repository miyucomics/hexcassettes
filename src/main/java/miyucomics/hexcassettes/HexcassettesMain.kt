package miyucomics.hexcassettes

import at.petrak.hexcasting.xplat.IXplatAbstractions
import miyucomics.hexcassettes.HexcassettesUtils.id
import miyucomics.hexcassettes.inits.HexcassettesAdvancements
import miyucomics.hexcassettes.inits.HexcassettesPatterns
import miyucomics.hexcassettes.inits.HexcassettesSounds
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.entity.LivingEntity
import net.minecraft.item.FoodComponent
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.util.Identifier
import net.minecraft.util.Rarity
import net.minecraft.util.registry.Registry
import net.minecraft.world.World

class HexcassettesMain : ModInitializer {
	override fun onInitialize() {
		ServerPlayNetworking.registerGlobalReceiver(CASSETTE_REMOVE) { _, player, _, packet, _ ->
			val label = packet.readString()
			val state = HexcassettesAPI.getPlayerState(player).queuedHexes
			state.remove(label)
		}

		ServerPlayNetworking.registerGlobalReceiver(SYNC_CASSETTES) { _, player, _, _, _ -> HexcassettesAPI.sendSyncPacket(player) }
		ServerPlayerEvents.AFTER_RESPAWN.register { _, player, _ -> HexcassettesAPI.dequeueAll(player) }

		Registry.register(Registry.ITEM, id("cassette"), CassetteItem())
		HexcassettesAdvancements.init()
		HexcassettesPatterns.init()
		HexcassettesSounds.init()
	}

	companion object {
		const val MOD_ID: String = "hexcassettes"
		const val MAX_CASSETTES: Int = 6
		const val MAX_LABEL_LENGTH: Int = 32

		val CASSETTE_REMOVE: Identifier = id("cassette_remove")
		val SYNC_CASSETTES: Identifier = id("sync_cassettes")
	}
}

// kinda messy, but I don't want to make a whole file for these
class CassetteItem : Item(Settings().maxCount(1).group(IXplatAbstractions.INSTANCE.tab).rarity(Rarity.UNCOMMON).food(FoodComponent.Builder().alwaysEdible().build())) {
	override fun getMaxUseTime(stack: ItemStack) = 100
	override fun getEatSound() = HexcassettesSounds.CASSETTE_LOOP

	override fun finishUsing(stack: ItemStack, world: World, user: LivingEntity): ItemStack {
		if (world.isClient) {
			world.playSound(user.x, user.y, user.z, HexcassettesSounds.CASSETTE_INSERT, SoundCategory.MASTER, 5f, 1f, false)
			return super.finishUsing(stack, world, user)
		}
		if (user !is ServerPlayerEntity)
			return super.finishUsing(stack, world, user)
		val playerState = HexcassettesAPI.getPlayerState(user)
		if (playerState.ownedCassettes < HexcassettesMain.MAX_CASSETTES) {
			HexcassettesAdvancements.TAPE_WORM.trigger(user)
			HexcassettesAPI.getPlayerState(user).ownedCassettes += 1
			if (HexcassettesAPI.getPlayerState(user).ownedCassettes == HexcassettesMain.MAX_CASSETTES)
				HexcassettesAdvancements.FULL_ARSENAL.trigger(user)
			HexcassettesAPI.sendSyncPacket(user)
		}
		return super.finishUsing(stack, world, user)
	}
}