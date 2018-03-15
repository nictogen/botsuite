package com.nic.botsuite.chloe.menus

import com.nic.botsuite.chloe.CharacterHandler
import com.nic.botsuite.common.util.QuestionHandler
import com.nic.botsuite.common.util.toEmoji
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
class DeleteCharacter(user: User, channel: TextChannel, vararg fields : QuestionHandler.Field) : QuestionHandler.Question(user, channel, Color.WHITE, "Select a character to delete", Optional.empty(), *fields) {

    override fun onMessage(server: Server, user: User, message: Message) {}

    override fun onReaction(server: Server, user: User, reaction: Reaction) {
        if (reaction.emoji.isUnicodeEmoji) {
            when (reaction.emoji.asUnicodeEmoji().get()) {
                "\uD83D\uDEAB" -> {
                    reaction.message.delete()
                }
                else -> {
                    var int = 0
                    CharacterHandler.characters.filter { it.serverID == server.id && it.userID == user.id }.forEach {
                        if(reaction.emoji.asUnicodeEmoji().get() == int.toEmoji()){
                            CharacterHandler.characters.remove(it)
                            CharacterHandler.saveData(server)
                            reaction.message.delete()
                        }
                        int++
                    }
                }
            }
        }
    }

}