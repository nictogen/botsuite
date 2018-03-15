package com.nic.botsuite.chloe.menus

import com.nic.botsuite.chloe.SettingsHandler
import com.nic.botsuite.common.util.QuestionHandler
import de.btobastian.javacord.entities.Server
import de.btobastian.javacord.entities.User
import de.btobastian.javacord.entities.channels.TextChannel
import de.btobastian.javacord.entities.message.Message
import de.btobastian.javacord.entities.message.Reaction
import java.awt.Color
import java.util.*

/**
 * Created by Nictogen on 2/2/18
 */
class AdjustSettings(user: User, channel : TextChannel) : QuestionHandler.Question(user, channel, Color.WHITE, "Please select a a menu item, ${user.getDisplayName(channel.asServerTextChannel().get().server)}.", Optional.empty(),
        QuestionHandler.Field("\uD83D\uDC64", if (SettingsHandler.getSettings(channel.asServerTextChannel().get().server).anyoneCreate) "Only allow admins to create characters." else "Allow anyone to create characters.", true),
        QuestionHandler.Field("\uD83D\uDEAB", "Cancel", true)) {

    override fun onMessage(server: Server, user: User, message: Message) {}

    override fun onReaction(server: Server, user: User, reaction: Reaction) {
        if (reaction.emoji.isUnicodeEmoji) {
            when (reaction.emoji.asUnicodeEmoji().get()) {
                "\uD83D\uDC64" -> {
                    val settings = SettingsHandler.getSettings(server)
                    settings.anyoneCreate = !settings.anyoneCreate
                    SettingsHandler.saveData(server.id)
                    reaction.message.delete()
                }
                "\uD83D\uDEAB" -> {
                    reaction.message.delete()
                }
            }
        }
    }

}