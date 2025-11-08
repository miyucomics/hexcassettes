package miyucomics.hexcassettes.interop.hexdebug

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.ResolvedPatternType
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import gay.`object`.hexdebug.core.api.HexDebugCoreAPI
import gay.`object`.hexdebug.core.api.debugging.env.DebugEnvironment
import miyucomics.hexcassettes.data.QueuedHex
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

class CassetteDebugEnv(
    caster: ServerPlayerEntity,
    private val key: String,
    private val keyText: Text,
    private val queuedHex: QueuedHex,
) : DebugEnvironment(caster) {
    override fun resume(env: CastingEnvironment, image: CastingImage, resolutionType: ResolvedPatternType): Boolean {
        return false
    }

    override fun restart(threadId: Int) {
        HexDebugCoreAPI.INSTANCE.createDebugThread(this, threadId)
        queuedHex.cast(caster, key)
    }

    override fun terminate() {}

    override fun isCasterInRange() = true

    override fun getName() = keyText
}
