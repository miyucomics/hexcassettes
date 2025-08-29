package miyucomics.hexcassettes

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.fabricmc.loader.api.FabricLoader
import java.io.IOException
import java.nio.file.Files

data class HexcassettesConfiguration(var maxCassettes: Int = 6) {
    companion object {
        private val CONFIG_PATH = FabricLoader.getInstance().configDir.resolve("hexcassettes.json")
        private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
        val instance: HexcassettesConfiguration by lazy { load() }

        private fun load(): HexcassettesConfiguration = try {
            if (Files.exists(CONFIG_PATH)) {
                Files.readString(CONFIG_PATH).let { gson.fromJson(it, HexcassettesConfiguration::class.java) }
            } else {
                HexcassettesConfiguration().also { save(it) }
            }
        } catch (e: IOException) {
            println("Failed to load config: ${e.message}")
            HexcassettesConfiguration().also { save(it) }
        }

        internal fun save(config: HexcassettesConfiguration = instance) = try {
            Files.writeString(CONFIG_PATH, gson.toJson(config))
        } catch (e: IOException) {
            println("Failed to save config: ${e.message}")
        }
    }
}