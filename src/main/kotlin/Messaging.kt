import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.util.formatting.text.TextComponent
import com.sk89q.worldedit.util.formatting.text.format.TextColor
import org.bukkit.entity.Player

fun Player.sendBoater(message: String) =
    BukkitAdapter.adapt(this).print(
        TextComponent.of("[").color(TextColor.DARK_GRAY)
            .append(TextComponent.of("Boater").color(TextColor.GRAY))
            .append(TextComponent.of("]").color(TextColor.DARK_GRAY))
            .append(TextComponent.of(" $message").color(TextColor.GRAY))
    )
