package commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import org.bukkit.entity.Player
import Boater
import entity.ConfirmationState
import org.bukkit.entity.Boat
import sendBoater

@CommandAlias("boater")
@Description("Boat ORE : )")
class BoaterCommand(private val boater: Boater) : BaseCommand() {
    @Default @CatchUnknown
    @Subcommand("version")
    fun version(player: Player) {
        player.sendBoater("Version ${boater.description.version}")
    }
    @Subcommand("joinboat")
    @CommandAlias("joinboat")
    @CommandCompletion("@players")
    @CommandPermission("boater.join")
    fun joinboat(player: Player, @Single target: String) {
        val targetPlayer = boater.server.getPlayer(target) ?: run {
            player.sendBoater("Player $target is not online")
            return
        }
        val boat = targetPlayer.vehicle as? Boat ?: run {
            player.sendBoater("Invalid target")
            return
        }
        boat.addPassenger(player)
    }
    @Subcommand("editor")
    @CommandPermission("boater.edit")
    inner class EditorSub : BaseCommand() {
        @Subcommand("new")
        fun new(player: Player, @Single trackName: String) {

        }
        @Subcommand("undo")
        fun undo(player: Player) {

        }
        @Subcommand("finish")
        fun finish(player: Player) {

        }
        @CommandAlias("confirm")
        @Subcommand("confirm")
        fun confirm(player: Player) {
            when (boater.confirmStates[player]) {
                ConfirmationState.CONFIRM_CHECKPOINT -> player.sendBoater("Checkpoint confirmed")
                ConfirmationState.QUIT_EDITING -> player.sendBoater("Quitting editor")
                ConfirmationState.QUIT_RACING -> player.sendBoater("Quitting race")
                ConfirmationState.NONE -> player.sendBoater("Nothing to confirm :(")
                null -> player.sendBoater("Uuuummmm??")
            }
        }
        @Subcommand("quit")
        fun quit(player: Player) {

        }
        @Subcommand("add")
        inner class AddSub : BaseCommand() {
            @Subcommand("owner")
            @CommandCompletion("@players")
            fun owner(player: Player, @Single target: String) {

            }
            @Subcommand("helper")
            @CommandCompletion("@players")
            fun helper(player: Player, @Single target: String) {

            }
        }
    }
    @Subcommand("race")
    @CommandPermission("boater.race")
    inner class RaceSub : BaseCommand() {
        @Subcommand("prepare")
        fun prepare(player: Player, @Single id: String) {
            boater.racing.createRace(id.toInt(), player, 2)
        }
        @Subcommand("ready")
        fun ready(player: Player) {

        }
        @Subcommand("quit")
        fun quit(player: Player) {

        }
        @Subcommand("group")
        @CommandCompletion("@boater_tracks")
        fun group(player: Player) {

        }
        @Subcommand("solo")
        @CommandCompletion("@boater_tracks")
        fun solo(player: Player) {

        }
        @Subcommand("addracer")
        @CommandCompletion("@players")
        fun addRacer(player: Player, @Single target: String) {

        }
    }
    @Subcommand("times")
    @CommandPermission("boater.times")
    inner class TimesSub : BaseCommand() {
        @Subcommand("track")
        fun track(player: Player) {

        }
        @Subcommand("player")
        @CommandCompletion("@players")
        fun player(player: Player, @Single target: String) {

        }
    }
}