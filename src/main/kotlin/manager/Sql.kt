package manager

import entity.Sql
import entity.Vector2D
import entity.*
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class Sql(
    host: String,
    port: Int,
    database: String,
    user: String,
    password: String,
    driver: String = "com.mysql.jdbc.Driver"
) {
    private val database = Database.connect(
        "jdbc:mysql://${host}:${port}/${database}",
        driver = driver,
        user = user,
        password = password
    )

    fun initTables() = transaction(database) {
        SchemaUtils.create(
            Sql.Track,
            Sql.Checkpoint,
            Sql.User,
            Sql.Responsible,
            Sql.Racer,
            Sql.Race,
            Sql.Lap,
            Sql.Split
        )
    }

    fun destroy() = transaction(database) {
        SchemaUtils.drop(
            Sql.Track,
            Sql.Checkpoint,
            Sql.User,
            Sql.Responsible,
            Sql.Racer,
            Sql.Race,
            Sql.Lap,
            Sql.Split
        )
    }

    fun ensureUser(player: Player) : Int = ensureUser(player.uniqueId, player.name)

    fun getUser(uuid: UUID) : Int? = transaction(database) {
        Sql.User.select { Sql.User.uuid eq uuid.toString() }.firstOrNull()?.get(Sql.User.id)
    }

    private fun ensureUser(uniqueId: UUID, username: String? = null) : Int = transaction(database) {
        Sql.User.insertIgnore {
            it[uuid] = uniqueId.toString()
            it[name] = username
        }
        val id = Sql.User.select { Sql.User.uuid eq uniqueId.toString() }.first()[Sql.User.id]
        username.let {
            Sql.User.update({ Sql.User.id eq id }) {
                it[name] = username!!
            }
        }
        id
    }

    fun insertRace(race: Race) = transaction(database) {
        val raceId = Sql.Race.insert {
            it[trackId] = race.trackId
            it[lapCount] = race.laps
        } get Sql.Race.id
        race.racers.forEach { racer ->
            val racerId = Sql.Racer.insert {
                it[id] = ensureUser(racer.player.uniqueId)
                it[this.raceId] = raceId
            } get Sql.Racer.id
            racer.times.forEachIndexed { index, lap ->
                val lapTime = lap.sum()
                val lapId = Sql.Lap.insert {
                    it[this.raceId] = raceId
                    it[this.lap] = index
                    it[this.racerId] = racerId
                    it[this.time] = lapTime
                } get Sql.Lap.id
                Sql.Split.batchInsert(lap.withIndex()) { (splitIndex, split) ->
                    this[Sql.Split.lapId] = lapId
                    this[Sql.Split.split] = splitIndex
                    this[Sql.Split.racerId] = racerId
                    this[Sql.Split.time] = split
                }
            }
        }
    }

    fun getTrack(id: Int) : Track? = transaction(database) {
        // Track Meta
        val trackQuery = Sql.Track.select { Sql.Track.id eq id }
        if (trackQuery.empty()) return@transaction null
        val trackMeta = trackQuery.first()
        val responsible = (Sql.Responsible innerJoin Sql.User).select { Sql.Responsible.trackId eq id }
        val owners = responsible.filter {
            it[Sql.Responsible.isOwner]
        }.map {
            UUID.fromString(it[Sql.User.uuid])
        }
        val helpers =  responsible.filter {
            it[Sql.Responsible.isHelper]
        }.map {
            UUID.fromString(it[Sql.User.uuid])
        }

        // Checkpoints
        val checkpoints = mutableListOf<Checkpoint>().apply {
            Sql.Checkpoint.select { Sql.Checkpoint.trackId eq id }.forEach {
                add(
                    Checkpoint(
                        it[Sql.Checkpoint.levelY] + trackMeta[Sql.Track.offsetY],
                        Segment(
                            Vector2D(
                                it[Sql.Checkpoint.startX] + trackMeta[Sql.Track.offsetX],
                                it[Sql.Checkpoint.startZ] + trackMeta[Sql.Track.offsetZ]
                            ),
                            Vector2D(
                                it[Sql.Checkpoint.endX] + trackMeta[Sql.Track.offsetX],
                                it[Sql.Checkpoint.endZ] + trackMeta[Sql.Track.offsetZ]
                            )
                        )
                    )
                )
            }
        }

        // Track
        Track(
            trackMeta[Sql.Track.name],
            UUID.fromString(trackMeta[Sql.Track.world]),
            trackMeta[Sql.Track.deathline],
            trackMeta[Sql.Track.isloop],
            owners,
            helpers,
            checkpoints
        )
    }

    fun insertTrack(track: Track) = transaction(database) {
        val localOffsetX = track.checkpoints[0].segment.start.x.toInt()
        val localOffsetY = track.checkpoints[0].level
        val localOffsetZ = track.checkpoints[0].segment.start.y.toInt()
        val id = Sql.Track.insert {
            it[name] = track.name
            it[checkpoints] = track.checkpoints.size
            it[offsetX] = localOffsetX
            it[offsetY] = localOffsetY
            it[offsetZ] = localOffsetZ
            it[deathline] = track.deathline
            it[isloop] = track.isloop
            it[world] = track.world.toString()
        } get Sql.Track.id
        Sql.Checkpoint.batchInsert(track.checkpoints.withIndex()) { (index, checkpoint) ->
            this[Sql.Checkpoint.trackId] = id
            this[Sql.Checkpoint.seqId] = index
            this[Sql.Checkpoint.levelY] = checkpoint.level - localOffsetY
            this[Sql.Checkpoint.startX] = checkpoint.segment.start.x - localOffsetX
            this[Sql.Checkpoint.startZ] = checkpoint.segment.start.y - localOffsetZ
            this[Sql.Checkpoint.endX] = checkpoint.segment.end.x - localOffsetX
            this[Sql.Checkpoint.endZ] = checkpoint.segment.end.y - localOffsetZ
        }
        Sql.Responsible.batchInsert(track.owners) {
            this[Sql.Responsible.trackId] = id
            this[Sql.Responsible.id] = getUser(it)!!
            this[Sql.Responsible.isOwner] = true
        }
        Sql.Responsible.batchInsert(track.helpers) {
            this[Sql.Responsible.trackId] = id
            this[Sql.Responsible.id] = getUser(it)!!
            this[Sql.Responsible.isHelper] = true
        }
    }
}