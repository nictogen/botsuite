package com.nic.mercedes.handlers

import com.nic.mercedes.games.Blackjack
import com.nic.mercedes.games.Roulette
import com.nic.mercedes.init.Mercedes
import com.nic.mercedes.util.getRpName
import com.nic.mercedes.util.sendEmbedMessage
import de.btobastian.javacord.entities.Server
import de.btobastian.javacord.entities.User
import de.btobastian.javacord.entities.message.Message
import de.btobastian.javacord.entities.message.Reaction
import de.btobastian.javacord.events.message.MessageCreateEvent
import de.btobastian.javacord.events.message.MessageDeleteEvent
import de.btobastian.javacord.events.message.reaction.ReactionAddEvent
import de.btobastian.javacord.listeners.message.MessageCreateListener
import de.btobastian.javacord.listeners.message.MessageDeleteListener
import de.btobastian.javacord.listeners.message.reaction.ReactionAddListener
import java.awt.Color
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by Nictogen on 1/20/18
 */
object QuestionHandler : ReactionAddListener, MessageCreateListener, MessageDeleteListener {

    override fun onMessageCreate(event: MessageCreateEvent) {
        if(event.message.userAuthor.get().isBot && event.message.content.contains(Mercedes.api.yourself.id.toString())) event.message.delete()
        questionList.filter { it.isValidMessager(event.message.userAuthor.get()) }.forEach {
            it.onMessage(event.channel.asServerTextChannel().get().server, event.message.userAuthor.get(), event.message)
        }
    }

    override fun onReactionAdd(event: ReactionAddEvent) {
        questionList.filter { !event.user.isBot && it.isValidReactor(event.user) && it.messageID == event.message.get().id }.forEach {
            it.onReaction(event.channel.asServerTextChannel().get().server, event.user, event.reaction.get())
        }
    }

    override fun onMessageDelete(event: MessageDeleteEvent) {
        questionList.removeAll(questionList.filter { event.messageId == it.messageID })
    }

    val questionList = ArrayList<Question>()

    abstract class Question(message: Message, var user: User, var messageID: Long = message.id) {
        init {
            QuestionHandler.questionList.add(this)
        }

        abstract fun onReaction(server: Server, user: User, reaction: Reaction)

        abstract fun onMessage(server: Server, user: User, message: Message)

        open fun isValidReactor(user: User) : Boolean {
            return user == this.user
        }

        open fun isValidMessager(user: User) : Boolean {
            return user == this.user
        }
    }

    class Menu(originalMessage: Message, user: User, message: Message = originalMessage.channel.sendEmbedMessage(Color.YELLOW, "") {
        setDescription("Please select a a menu item, ${user.getRpName(originalMessage.channel.asServerTextChannel().get().server)}.")
        addField("\uD83C\uDFE6", "Check Balance", true)
        addField("\uD83D\uDCB0", "Gain Income", true)
        addField("\uD83D\uDCEC", "Send to other", true)
        addField("⚙", "Adjust Settings", true)
        if(originalMessage.channel.asServerTextChannel().get().category.isPresent && originalMessage.channel.asServerTextChannel().get().category.get().name.contains("$")){
            addField("\uD83C\uDFB2", "Play a Game", true)
        }
    }) : Question(message, user, message.id) {

        init {
            message.addReaction("\uD83C\uDFE6")
            message.addReaction("\uD83D\uDCB0")
            message.addReaction("\uD83D\uDCEC")
            message.addReaction("⚙")
            if(originalMessage.channel.asServerTextChannel().get().name.contains("roulette")){
                message.addReaction("\uD83C\uDFB2")
            }
        }

        override fun onMessage(server: Server, user: User, message: Message) {}

        override fun onReaction(server: Server, user: User, reaction: Reaction) {
            if (reaction.emoji.isUnicodeEmoji) {
                when (reaction.emoji.asUnicodeEmoji().get()) {
                    "\uD83C\uDFE6" -> {
                        Balance(reaction.message, user)
                        reaction.message.delete()
                    }
                    "\uD83D\uDCB0" -> {
                        val lastIncome = DataHandler.getData(reaction.message.channel.asServerTextChannel().get().server, user).lastIncome / 3600000
                        val hours = Calendar.getInstance().time.time / 3600000
                        if (hours - lastIncome >= 12) {
                            GainIncome(reaction.message, user)
                            DataHandler.getData(reaction.message.channel.asServerTextChannel().get().server, user).lastIncome = Calendar.getInstance().time.time
                            reaction.message.delete()
                        }
                    }
                    "\uD83D\uDCEC" -> {
                        Send(reaction.message, user)
                        reaction.message.delete()
                    }
                    "⚙" -> {
                        Settings(reaction.message, user)
                        reaction.message.delete()
                    }
                    "\uD83C\uDFB2" -> {
                        Games(reaction.message, user)
                        reaction.message.delete()
                    }
                }
            }
        }

    }

    class Balance(originalMessage: Message, user: User, message: Message = originalMessage.channel.sendEmbedMessage(Color.YELLOW, "") {
        val d = DataHandler.getData(originalMessage.channel.asServerTextChannel().get().server, user).amount
        setDescription("Your balance is ${NumberFormat.getNumberInstance(Locale.US).format(d)}, ${user.getRpName(originalMessage.channel.asServerTextChannel().get().server)}.")
        addField("⏩", "Thank you", true)
    }) : Question(message, user, message.id) {

        init {
            message.addReaction("⏩")
        }

        override fun onMessage(server: Server, user: User, message: Message) {}

        override fun onReaction(server: Server, user: User, reaction: Reaction) {
            if (reaction.emoji.isUnicodeEmoji && reaction.emoji.asUnicodeEmoji().get() == "⏩") {
                reaction.message.delete()
            }
        }

    }

    class Settings(private var originalMessage: Message, user: User, message: Message = originalMessage.channel.sendEmbedMessage(Color.YELLOW, "") {
        val d = DataHandler.getData(originalMessage.channel.asServerTextChannel().get().server, user).livingCost
        setDescription("Your current living cost is: ${d.name}.")
        addField("\uD83D\uDCB5", "Change to basic", true)
        addField("\uD83D\uDCB0", "Change to medium", true)
        addField("\uD83D\uDCB8", "Change to high-life", true)
        addField("⏩", "Thank you", true)
    }) : Question(message, user, message.id) {

        init {
            message.addReaction("\uD83D\uDCB5")
            message.addReaction("\uD83D\uDCB0")
            message.addReaction("\uD83D\uDCB8")
            message.addReaction("⏩")
        }

        override fun onMessage(server: Server, user: User, message: Message) {}

        override fun onReaction(server: Server, user: User, reaction: Reaction) {
            if (reaction.emoji.isUnicodeEmoji) {
                when (reaction.emoji.asUnicodeEmoji().get()) {
                    "⏩" -> reaction.message.delete()
                    "\uD83D\uDCB5" -> {
                        DataHandler.getData(originalMessage.channel.asServerTextChannel().get().server, user).livingCost = DataHandler.LivingCost.BASIC
                        DataHandler.saveData(originalMessage.serverTextChannel.get().server)
                        reaction.message.delete()
                    }
                    "\uD83D\uDCB0" -> {
                        DataHandler.getData(originalMessage.channel.asServerTextChannel().get().server, user).livingCost = DataHandler.LivingCost.MEDIUM
                        DataHandler.saveData(originalMessage.serverTextChannel.get().server)
                        reaction.message.delete()
                    }
                    "\uD83D\uDCB8" -> {
                        DataHandler.getData(originalMessage.channel.asServerTextChannel().get().server, user).livingCost = DataHandler.LivingCost.HIGH_LIFE
                        DataHandler.saveData(originalMessage.serverTextChannel.get().server)
                        reaction.message.delete()
                    }
                }
            }
        }
    }

    class Send(private var originalMessage: Message, user: User, var message: Message = originalMessage.channel.sendEmbedMessage(Color.YELLOW, "") {
        val d = DataHandler.getData(originalMessage.channel.asServerTextChannel().get().server, user).amount
        setDescription("Your balance is ${NumberFormat.getNumberInstance(Locale.US).format(d)}, ${user.getRpName(originalMessage.channel.asServerTextChannel().get().server)}. \nSend to another player with format: `@Mercedes the Money Bot#8859 <player> <amount>`")
        addField("⏩", "Cancel", true)
    }) : Question(message, user, message.id) {

        init {
            message.addReaction("⏩")
        }

        override fun onMessage(server: Server, user: User, message: Message) {
            try {
                val args = message.content.split(" ")
                if (args[0] == Mercedes.api.yourself.mentionTag && server.members.any { args[1].contains("${it.id}") }) {
                    val player = server.members.first { args[1].contains("${it.id}") }
                    val amount = args[2].toInt()
                    val d = DataHandler.getData(server, user).amount
                    if (amount in 1..d) {
                        DataHandler.getData(server, user).amount -= amount
                        DataHandler.getData(server, player).amount += amount
                        DataHandler.saveData(server)
                        Balance(message, user)
                        Balance(message, player)
                        message.delete()
                        this.message.delete()
                    } else {
                        Notify(message, "You don't have that much, sorry!", user)
                        message.delete()
                        this.message.delete()
                    }
                }
            } catch (e: Exception) {
            }
        }

        override fun onReaction(server: Server, user: User, reaction: Reaction) {
            if (reaction.emoji.isUnicodeEmoji && reaction.emoji.asUnicodeEmoji().get() == "⏩") {
                reaction.message.delete()
            }
        }

    }

    class Notify(originalMessage: Message, var content : String, user: User, message: Message = originalMessage.channel.sendEmbedMessage(Color.RED, "") {
        setDescription(content)
        addField("⏩", "Thank you", true)
    }) : Question(message, user, message.id) {

        init {
            message.addReaction("⏩")
        }

        override fun onMessage(server: Server, user: User, message: Message) {}

        override fun onReaction(server: Server, user: User, reaction: Reaction) {
            if (reaction.emoji.isUnicodeEmoji && reaction.emoji.asUnicodeEmoji().get() == "⏩") {
                reaction.message.delete()
            }
        }

    }

    class GainIncome(originalMessage: Message, user: User, message: Message = originalMessage.channel.sendEmbedMessage(Color.RED, "") {
        setDescription("You have gained 500 points.")
        addField("⏩", "Thank you", true)
    }) : Question(message, user, message.id) {

        init {
            DataHandler.getData(originalMessage.channel.asServerTextChannel().get().server, user).amount += 500
            DataHandler.saveData(originalMessage.channel.asServerTextChannel().get().server)
            message.addReaction("⏩")
        }

        override fun onMessage(server: Server, user: User, message: Message) {}

        override fun onReaction(server: Server, user: User, reaction: Reaction) {
            if (reaction.emoji.isUnicodeEmoji && reaction.emoji.asUnicodeEmoji().get() == "⏩") {
                reaction.message.delete()
            }
        }

    }

    class Games(originalMessage: Message, user: User, message: Message = originalMessage.channel.sendEmbedMessage(Color.YELLOW, "") {
        setDescription("Please select a game, ${user.getRpName(originalMessage.channel.asServerTextChannel().get().server)}.")
        addField("\uD83D\uDD04", "Roulette", true)
        addField("♠️", "Blackjack", true)
    }) : Question(message, user, message.id) {

        init {
            message.addReaction("\uD83D\uDD04")
        }

        override fun onMessage(server: Server, user: User, message: Message) {}

        override fun onReaction(server: Server, user: User, reaction: Reaction) {
            if (reaction.emoji.isUnicodeEmoji) {
                when (reaction.emoji.asUnicodeEmoji().get()) {
                    "\uD83D\uDD04" -> {
                        Roulette(reaction.message, user)
                        reaction.message.delete()
                    }
                    "♠" -> {
                        val players = ArrayList<Blackjack.Player>()
                        players.add(Blackjack.Player(user, ArrayList(), 0, false))
                        Blackjack(reaction.message, players)
                        reaction.message.delete()
                    }
                }
            }
        }

    }
}
