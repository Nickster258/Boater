package entity

import org.bukkit.Location
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.Table
import java.util.*

object Sql {
    object Track : Table("boat_track") {
        val id = integer("id").autoIncrement()
        val name = varchar("name", 512)
        val checkpoints = integer("checkpoints")
        val offsetX = integer("offset_x")
        val offsetY = integer("offset_y")
        val offsetZ = integer("offset_z")
        val deathline = integer("deathline")
        var isloop = bool("isloop")
        val world = varchar("world", 36)
        override val primaryKey = PrimaryKey(id)
    }

    object Checkpoint : Table("boat_checkpoint") {
        val id = integer("id").autoIncrement()
        val seqId = integer("seq_id")
        val trackId = (integer("track_id") references Track.id)
        val levelY = integer("level_y")
        val startX = double("start_x")
        val startZ = double("start_z")
        val endX = double("end_x")
        val endZ = double("end_z")
        override val primaryKey = PrimaryKey(id)
    }

    object User : Table("boat_user") {
        val id = integer("id").autoIncrement()
        val uuid = varchar("uuid", 36).index(isUnique = true)
        val name = varchar("name", 16).nullable()
        val records = integer("records").default(0)
        override val primaryKey = PrimaryKey(id)
    }

    object Responsible : Table("boat_responsible") {
        val id = (integer("id") references User.id)
        val trackId = (integer("track_id") references Track.id)
        val isOwner = bool("is_owner").default(false)
        val isHelper = bool("is_helper").default(false)
    }

    object Racer : Table("boat_racer") {
        val id = (integer("id") references User.id)
        val raceId = (integer("race_id") references Race.id)
    }

    object Race : Table("boat_race") {
        val id = integer("id").autoIncrement()
        val trackId = (integer("track_id") references Track.id)
        val lapCount = integer("laps")
        override val primaryKey = PrimaryKey(id)
    }

    object Lap : Table("boat_lap") {
        val id = integer("id").autoIncrement()
        val raceId = (integer("race_id") references Race.id)
        val lap = integer("lap")
        val racerId = (integer("racer_id") references Racer.id)
        val time = float("time")
        override val primaryKey = PrimaryKey(id)
    }

    object Split : Table("boat_split") {
        val lapId = (integer("lap_id") references Lap.id)
        val split = integer("split")
        val racerId = (integer("racer_id") references Racer.id)
        val time = float("time")
    }
}

data class Race(
    val track: Track,
    val trackId: Int,
    val laps: Int,
    val racers: MutableList<Racer>
)

data class Racer(
    val player: Player,
    val times: List<List<Float>>, // Lap<Split<Float>>
    val raceId: Int,
    val restoreState: Location? = null,
    val checkpointStatus: Int = 0
)

data class Editor(
    val editor: Player,
    val track: Track
)

data class Track(
    val name: String,
    val world: UUID,
    val deathline: Int = 0,
    val isloop: Boolean = false,
    val owners: List<UUID> = emptyList(),
    val helpers: List<UUID> = emptyList(),
    val checkpoints: MutableList<Checkpoint> = mutableListOf()
)

data class Checkpoint(
    val level: Int,
    val segment: Segment
)

data class Vector2D(
    val x: Double,
    val y: Double
)

data class Segment(
    val start: Vector2D,
    val end: Vector2D
)
