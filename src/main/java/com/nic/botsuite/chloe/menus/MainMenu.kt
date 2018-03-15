package com.nic.botsuite.chloe.menus

import com.nic.botsuite.chloe.CharacterHandler
import com.nic.botsuite.chloe.SettingsHandler
import com.nic.botsuite.common.util.QuestionHandler
import com.nic.botsuite.common.util.toEmoji
import de.btobastian.javacord.entities.Server
import de.btobastian.javacord.entities.User
import de.btobastian.javacord.entities.channels.TextChannel
import de.btobastian.javacord.entities.message.Message
import de.btobastian.javacord.entities.message.Reaction
import de.btobastian.javacord.entities.permissions.PermissionType
import java.awt.Color
import java.util.*

/**
 * Created by Nictogen on 2/2/18
 */
class MainMenu(user: User, channel: TextChannel, vararg fields: QuestionHandler.Field) : QuestionHandler.Question(user, channel, Color.WHITE, "Please select a a menu item, ${user.getDisplayName(channel.asServerTextChannel().get().server)}.", Optional.empty(), *fields) {

    override fun onMessage(server: Server, user: User, message: Message) {}

    override fun onReaction(server: Server, user: User, reaction: Reaction) {
        if (reaction.emoji.isUnicodeEmoji) {
            val admin = server.hasPermission(user, PermissionType.ADMINISTRATOR)
            val settings = SettingsHandler.getSettings(server)
            val exempt = settings.exemptChannels.any { it == reaction.message.channel.id }

            when (reaction.emoji.asUnicodeEmoji().get()) {
                "\uD83D\uDC64" -> {
                    if(admin || settings.anyoneCreate) {
                        SetName(user, reaction.message.channel)
                        reaction.message.delete()
                    }
                }
                "\uD83D\uDC94" -> {
                    if (CharacterHandler.characters.any { it.serverID == server.id && it.userID == user.id }) {
                        val fields = ArrayList<QuestionHandler.Field>()
                        var int = 0
                        CharacterHandler.characters.filter { it.serverID == reaction.message.server.get().id && it.userID == user.id }.forEach {
                            if (int < 10)
                                fields.add(QuestionHandler.Field(int.toEmoji()!!, it.name, true))
                            int++
                        }
                        fields.add(QuestionHandler.Field("\uD83D\uDEAB", "Cancel", true))
                        DeleteCharacter(user, reaction.message.channel, *fields.toTypedArray())
                        reaction.message.delete()
                    }
                }
                "⚙" -> {
                    if(admin) {
                        AdjustSettings(user, message.channel)
                        reaction.message.delete()
                    }
                }
                "\uD83D\uDD95" -> {
                    if(admin && !exempt) {
                        settings.exemptChannels.add(reaction.message.channel.id)
                        SettingsHandler.saveData(server.id)
                        reaction.message.delete()
                    }
                }
                "\uD83D\uDC4C" -> {
                    if(admin && exempt) {
                        settings.exemptChannels.remove(reaction.message.channel.id)
                        SettingsHandler.saveData(server.id)
                        reaction.message.delete()
                    }
                }
                "\uD83D\uDEAB" -> {
                    reaction.message.delete()
                }
            }
        }
    }

}