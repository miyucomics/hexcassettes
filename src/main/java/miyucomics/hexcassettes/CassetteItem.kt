package miyucomics.hexcassettes

import miyucomics.hexcassettes.data.HexcassettesAPI
import net.minecraft.entity.LivingEntity
import net.minecraft.item.FoodComponent
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Rarity
import net.minecraft.world.World

class CassetteItem : Item(Settings().maxCount(1).rarity(Rarity.EPIC).food(FoodComponent.Builder().alwaysEdible().snack().build())) {
	override fun finishUsing(stack: ItemStack, world: World, user: LivingEntity): ItemStack {
		if (world.isClient)
			return super.finishUsing(stack, world, user)
		if (user !is ServerPlayerEntity)
			return super.finishUsing(stack, world, user)
		val playerState = HexcassettesAPI.getPlayerState(user)
		if (playerState.ownedCassettes < HexcassettesMain.MAX_CASSETTES) {
			HexcassettesAPI.getPlayerState(user).ownedCassettes += 1
			HexcassettesAPI.syncToClient(user)
		}
		return super.finishUsing(stack, world, user)
	}
}