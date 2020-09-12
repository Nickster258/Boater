package manager

import Boater
import entity.BoaterState
import entity.Race
import entity.Racer
import entity.Segment
import intKey
import org.bukkit.entity.Player
import org.bukkit.util.Vector

class Racing(private val boater: Boater) {
    private val lastPositions = mutableMapOf<Player, Vector>()
    private val races = mutableMapOf<Int, Race>()

    fun isRacing(player: Player) : Racer? {
        if (boater.boaterStates[player] != BoaterState.RACING) return null
        races.forEach { (_, race) ->
            race.racers.forEach {
                if (it.player.uniqueId == player.uniqueId) {
                    return it
                }
            }
        }
        return null // uhoh
    }

    fun destroyRace(raceId: Int) {
        races[raceId]?.racers?.forEach {
            boater.boaterStates[it.player] = BoaterState.NONE
        }
        races.remove(raceId)
    }

    fun leaveRace(player: Player) {
        races.forEach { (_, race) ->
            race.racers.removeIf { it.player.uniqueId == player.uniqueId }
        }
    }

    fun createRace(trackId: Int, coordinator: Player, laps: Int) : Int? {
        val track = boater.dbManager.getTrack(trackId) ?: return null
        val raceId = intKey()
        val racer = Racer(
            coordinator,
            emptyList(),
            raceId
        )
        val race = Race(
            track,
            trackId,
            laps,
            mutableListOf(racer)
        )
        races[raceId] = race
        return raceId
    }

    private fun getNextSegment(racer: Racer) : Segment? {
        val race = races[racer.raceId] ?: return null
        return if (racer.checkpointStatus+1 == race.track.checkpoints.size) {
            race.track.checkpoints[0].segment
        } else {
            race.track.checkpoints[racer.checkpointStatus].segment
        }
    }

}