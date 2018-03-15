package com.nic.botsuite.common.util

import de.btobastian.javacord.DiscordApi
import de.btobastian.javacord.entities.Server
import de.btobastian.javacord.entities.User
import de.btobastian.javacord.entities.channels.TextChannel
import de.btobastian.javacord.entities.message.Message
import de.btobastian.javacord.entities.message.Reaction
import de.btobastian.javacord.entities.message.emoji.CustomEmoji
import de.btobastian.javacord.events.message.MessageCreateEvent
import de.btobastian.javacord.events.message.MessageDeleteEvent
import de.btobastian.javacord.events.message.reaction.ReactionAddEvent
import de.btobastian.javacord.listeners.message.MessageCreateListener
import de.btobastian.javacord.listeners.message.MessageDeleteListener
import de.btobastian.javacord.listeners.message.reaction.ReactionAddListener
import java.awt.Color
import java.util.*

/**
 * Created by Nictogen on 2/2/18
 */
object QuestionHandler : ReactionAddListener, MessageCreateListener, MessageDeleteListener {

    override fun onMessageCreate(event: MessageCreateEvent) {
        if (event.message.author.isUser && event.message.userAuthor.isPresent && event.message.userAuthor.get().isBot && event.message.content.contains(event.message.api.yourself.id.toString())) event.message.delete()
        val questionList = questionLists[event.api]
        questionList!!.filter { event.message.userAuthor.isPresent && it.isValidMessager(event.message.userAuthor.get()) }.forEach {
            it.onMessage(event.channel.asServerTextChannel().get().server, event.message.userAuthor.get(), event.message)
        }
    }

    override fun onReactionAdd(event: ReactionAddEvent) {
        val questionList = questionLists[event.api]
        questionList!!.filter {!event.user.isBot && it.isValidReactor(event.user) && it.messageID == event.message.get().id }.forEach {
            it.onReaction(event.channel.asServerTextChannel().get().server, event.user, event.reaction.get())
        }
    }

    override fun onMessageDelete(event: MessageDeleteEvent) {
        val questionList = questionLists[event.api]
        questionList!!.removeAll(questionList.filter { event.messageId == it.messageID })
    }

    val questionLists = HashMap<DiscordApi, ArrayList<Question>>()

    abstract class Question(val user: User, channel: TextChannel, color: Color, description: String, author : Optional<Author>, vararg fields : Field) {
        var message : Message
        var messageID : Long
        init {
            val questionList = questionLists[channel.api]
            questionList!!.add(this)
            this.message = channel.sendEmbedMessage(color) {
                setDescription(description)
                fields.forEach {
                    addField(it.name, it.value, it.inline)
                }
                if(author.isPresent){
                    setAuthor(author.get().name, author.get().url, author.get().url)
                }
            }
            fields.forEach {
                if(it.customEmoji.isPresent){
                    message.addReaction(it.customEmoji.get())
                } else message.addReaction(it.name)
            }
            this.messageID = this.message.id
        }

        abstract fun onReaction(server: Server, user: User, reaction: Reaction)

        abstract fun onMessage(server: Server, user: User, message: Message)

        open fun isValidReactor(user: User): Boolean {
            return user == this.user
        }

        open fun isValidMessager(user: User): Boolean {
            return user == this.user
        }
    }

    class Field(val name : String, val value : String, val inline : Boolean, val customEmoji: Optional<CustomEmoji> = Optional.empty())

    class Author(val name: String, val url : String)

    class Notify(user: User, channel: TextChannel, description: String, color : Color = Color.RED) : Question(user, channel, color, description, Optional.empty(), Field("⏩", "Thank You", true)) {

        override fun onMessage(server: Server, user: User, message: Message) {}

        override fun onReaction(server: Server, user: User, reaction: Reaction) {
            if (reaction.emoji.isUnicodeEmoji && reaction.emoji.asUnicodeEmoji().get() == "⏩") {
                reaction.message.delete()
            }
        }

    }
}
