package com.nic.botsuite.chloe.menus

import com.nic.botsuite.chloe.CharacterHandler
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
class SetName(user: User, channel: TextChannel) : QuestionHandler.Question(user, channel, Color.WHITE, "What should the character be named, ${user.getDisplayName(channel.asServerTextChannel().get().server)}?", Optional.empty()) {
    override fun onMessage(server: Server, user: User, message: Message) {
        SetPicture(user, message.channel, message.content)
        this.message.delete()
        message.delete()
    }

    override fun onReaction(server: Server, user: User, reaction: Reaction) {}

}

class SetPicture(user: User, channel: TextChannel, private val name: String) : QuestionHandler.Question(user, channel, Color.WHITE, "Please paste a link to a picture for the character, ${user.getDisplayName(channel.asServerTextChannel().get().server)}.\n\n" +
        "An image of at least 256x256 is recommended.", Optional.empty()) {
    override fun onMessage(server: Server, user: User, message: Message) {
        SetSymbol(user, message.channel, name, message.content)
        this.message.delete()
        message.delete()
    }

    override fun onReaction(server: Server, user: User, reaction: Reaction) {}

}

class SetSymbol(user: User, channel: TextChannel, private val name: String, private val picture: String) : QuestionHandler.Question(user, channel, Color.WHITE, "Please send a one character symbol that you will put at the front of the message to talk as the character, ${user.getDisplayName(channel.asServerTextChannel().get().server)}.\n\n" +
        "You may also skip this step, and have the character always replace your messages.", Optional.empty(), QuestionHandler.Field("⏩", "Skip", true)) {
    override fun onMessage(server: Server, user: User, message: Message) {
        if (message.content.length == 1) {
            CharacterHandler.characters.add(CharacterHandler.Character(user.id, server.id, name, picture, message.content[0]))
            CharacterHandler.saveData(server)
            this.message.delete()
            message.delete()
        }
    }

    override fun onReaction(server: Server, user: User, reaction: Reaction) {
        if (reaction.emoji.isUnicodeEmoji && reaction.emoji.asUnicodeEmoji().get() == "⏩") {
            CharacterHandler.characters.add(CharacterHandler.Character(user.id, server.id, name, picture, '~'))
            CharacterHandler.saveData(server)
            reaction.message.delete()
        }
    }

}