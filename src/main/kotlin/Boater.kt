import co.aikar.commands.PaperCommandManager
import com.comphenix.protocol.ProtocolLibrary
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.yaml
import com.uchuhimo.konf.source.yaml.toYaml
import commands.BoaterCommand
import entity.*
import listener.BoatListener
import listener.EditListener
import listener.MetaListener
import manager.Racing
import manager.Sql
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.logging.Level

class Boater : JavaPlugin() {
    var confirmStates = mutableMapOf<Player, ConfirmationState>()
    val boaterStates = mutableMapOf<Player, BoaterState>()
    var segment: Segment? = null
    var config = loadConfig()
    var racing: Racing = Racing(this)
    var dbManager: Sql = Sql(
        config[BoaterSpec.BoaterDatabase.host],
        config[BoaterSpec.BoaterDatabase.port],
        config[BoaterSpec.BoaterDatabase.database],
        config[BoaterSpec.BoaterDatabase.username],
        config[BoaterSpec.BoaterDatabase.password]
    )

    override fun onEnable() {
        dbManager.initTables()
        val protocolManager = ProtocolLibrary.getProtocolManager()
        server.pluginManager.apply {
            registerEvents(BoatListener(this@Boater), this@Boater)
            registerEvents(EditListener(this@Boater), this@Boater)
            registerEvents(MetaListener(this@Boater), this@Boater)
        }
        PaperCommandManager(this).apply {
            registerCommand(BoaterCommand(this@Boater))
            enableUnstableAPI("brigadier")
        }
    }

    override fun onDisable() {
        super.onDisable()
    }

    fun reload() {
        config = loadConfig(reloaded = true)
    }

    private fun loadConfig(reloaded: Boolean = false): Config {
        if (!dataFolder.exists()) {
            logger.log(Level.INFO, "No resource directory found, creating directory")
            dataFolder.mkdir()
        }
        val configFile = File(dataFolder, "config.yml")
        val loadedConfig = if (!configFile.exists()) {
            logger.log(Level.INFO, "No config file found, generating from default config.yml")
            configFile.createNewFile()
            Config { addSpec(BoaterSpec) }
        } else {
            Config { addSpec(BoaterSpec) }.from.yaml.watchFile(configFile)
        }
        loadedConfig.toYaml.toFile(configFile)
        logger.log(Level.INFO, "${if (reloaded) "Rel" else "L"}oaded config.yml")
        return loadedConfig
    }
}