package miyucomics.hexcassettes

import net.minecraft.util.Identifier

object HexcassettesUtils {
	fun id(string: String) = Identifier(HexcassettesMain.MOD_ID, string)
	fun shortenLabel(label: String) = label.substring(0, HexcassettesMain.MAX_LABEL_LENGTH.coerceAtMost(label.length))
}