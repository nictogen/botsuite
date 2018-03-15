package com.nic.botsuite.mercedes

import com.nic.botsuite.common.util.QuestionHandler
import com.nic.botsuite.common.util.QuestionHandler.Question
import com.nic.botsuite.common.util.toEmoji
import com.nic.botsuite.gwen.champions.CardPacks
import com.nic.botsuite.gwen.champions.api.Champion
import com.nic.botsuite.gwen.champions.api.Spell
import com.nic.botsuite.mercedes.handlers.DataHandler
import de.btobastian.javacord.entities.Server
import de.btobastian.javacord.entities.User
import de.btobastian.javacord.entities.channels.TextChannel
import de.btobastian.javacord.entities.message.Message
import de.btobastian.javacord.entities.message.Reaction
import java.awt.Color
import java.text.NumberFormat
import java.util.*
import kotlin.reflect.full.createInstance


/**
 * Created by Nictogen on 1/20/18
 */
object MercedesMenus {

    class Menu(user: User, channel: TextChannel, vararg fields : QuestionHandler.Field) : QuestionHandler.Question(user, channel, Color.YELLOW, "Please select a a menu item, ${user.getDisplayName(channel.asServerTextChannel().get().server)}.", Optional.empty(), *fields) {

        override fun onMessage(server: Server, user: User, message: Message) {}

        override fun onReaction(server: Server, user: User, reaction: Reaction) {
            if (reaction.emoji.isUnicodeEmoji) {
                when (reaction.emoji.asUnicodeEmoji().get()) {
                    "\uD83D\uDCB0" -> {
                        QuestionHandler.Notify(user, reaction.message.channel, "Your balance is ${NumberFormat.getNumberInstance(Locale.US).format(DataHandler.getData(server, user).amount)}, ${user.getDisplayName(reaction.message.channel.asServerTextChannel().get().server)}.", Color.YELLOW)
                        reaction.message.delete()
                    }
                    "\uD83D\uDCB5" -> {
                        val data = DataHandler.getData(server, user)
                        if(System.nanoTime()/1000 - data.lastIncome/1000 >= 43200){
                            data.amount += 500
                            data.lastIncome = System.nanoTime()
                            DataHandler.saveData(server)
                            QuestionHandler.Notify(user, reaction.message.channel, "${user.getDisplayName(server)} got their income of 500", Color.GREEN)
                        }
                        reaction.message.delete()
                    }
                    "\uD83D\uDCEC" -> {
                        Send(user, reaction.message.channel)
                        reaction.message.delete()
                    }
                    "\uD83D\uDED2" -> {
                        if (reaction.message.channel.asServerTextChannel().get().name.contains("card-shop")) BuyCardPack(user, reaction.message.channel)
                        else QuestionHandler.Notify(user, reaction.message.channel, "You need to be in a card shop to buy a card pack.")
                        reaction.message.delete()
                    }
                }
            }
        }
    }

    class Send(user: User, channel: TextChannel) : Question(user, channel, Color.YELLOW, "Your balance is ${NumberFormat.getNumberInstance(Locale.US).format(DataHandler.getData(channel.asServerTextChannel().get().server, user).amount)}, ${user.getDisplayName(channel.asServerTextChannel().get().server)}. \nSend to another player with format: `@Mercedes the Money Bot#8859 <player> <amount>`", Optional.empty(),QuestionHandler.Field("⏩", "Cancel", true)) {

        override fun onMessage(server: Server, user: User, message: Message) {
            val args = message.content.split(" ")
            if (args.size > 2) {
                if (args[0] == server.api.yourself.mentionTag && server.members.any { args[1].contains("${it.id}") }) {
                    val player = server.members.first { args[1].contains("${it.id}") }
                    val amount = args[2].toInt()
                    val d = DataHandler.getData(server, user).amount
                    if (amount in 1..d) {
                        DataHandler.getData(server, user).amount -= amount
                        DataHandler.getData(server, player).amount += amount
                        DataHandler.saveData(server)
                        QuestionHandler.Notify(user, message.channel, "Your balance is ${NumberFormat.getNumberInstance(Locale.US).format(DataHandler.getData(server, user).amount)}, ${user.getDisplayName(message.channel.asServerTextChannel().get().server)}.")
                        QuestionHandler.Notify(player, message.channel, "Your balance is ${NumberFormat.getNumberInstance(Locale.US).format(DataHandler.getData(server, player).amount)}, ${player.getDisplayName(message.channel.asServerTextChannel().get().server)}.")
                        message.delete()
                        this.message.delete()
                    } else {
                        QuestionHandler.Notify(user, message.channel, "You don't have that much, sorry!")
                        message.delete()
                        this.message.delete()
                    }
                }
            }
        }

        override fun onReaction(server: Server, user: User, reaction: Reaction) {
            if (reaction.emoji.isUnicodeEmoji && reaction.emoji.asUnicodeEmoji().get() == "⏩") {
                reaction.message.delete()
            }
        }

    }

    class BuyCardPack(user: User, channel: TextChannel) : QuestionHandler.Question(user, channel, Color.YELLOW, "Choose which pack to buy, ${user.getDisplayName(channel.asServerTextChannel().get().server)}.", Optional.empty(),
            QuestionHandler.Field(1.toEmoji().toString(), "Humble Beginnings", true),
            QuestionHandler.Field("\uD83D\uDEAB", "Cancel", true)) {

        override fun onMessage(server: Server, user: User, message: Message) {}

        override fun onReaction(server: Server, user: User, reaction: Reaction) {
            if (reaction.emoji.isUnicodeEmoji) {
                when (reaction.emoji.asUnicodeEmoji().get()) {
                    1.toEmoji() -> {
                        if(DataHandler.getData(server, user).amount >= 500) {
                            val cardData = com.nic.botsuite.gwen.DataHandler.getData(server, user)
                            val list = CardPacks.pack1.pullCards()
                            var string = "${user.getDisplayName(server)} pulled:\n\n"
                            list.forEach { c ->
                                val card = c.getClass().createInstance()
                                if (card is Champion) string += "\t${card.emoji.mentionTag} ${card.name}"
                                else if (card is Spell) string += "\t${card.emoji!!.mentionTag} ${card.name}"
                                if (cardData.collection.any { it.getClass() == c.getClass() } || cardData.currentSpells.any { it.getClass() == c.getClass() } || cardData.currentChampion.getClass() == c.getClass()) {
                                    string += " (Repeat)\n\n"
                                } else {
                                    string += "\n\n"
                                    cardData.collection.add(c)
                                }
                            }
                            com.nic.botsuite.gwen.DataHandler.saveData(server)
                            DataHandler.getData(server, user).amount -= 500
                            DataHandler.saveData(server)
                            QuestionHandler.Notify(user, reaction.message.channel, string)
                        } else QuestionHandler.Notify(user, reaction.message.channel, "You don't have enough for that pack.")
                        reaction.message.delete()
                    }
                    "\uD83D\uDEAB" -> {
                        reaction.message.delete()
                    }
                }
            }
        }
    }

//    class Games(user: User, channel: TextChannel) : Question(user, channel, Color.YELLOW, "Please select a game, ${user.getDisplayName(channel.asServerTextChannel().get().server)}.",Optional.empty(),
//            QuestionHandler.Field("\uD83D\uDD04", "Roulette", true),
//            QuestionHandler.Field("♠️", "Blackjack", true)) {
//        override fun onMessage(server: Server, user: User, message: Message) {}
//
//        override fun onReaction(server: Server, user: User, reaction: Reaction) {
//            if (reaction.emoji.isUnicodeEmoji) {
//                when (reaction.emoji.asUnicodeEmoji().get()) {
////                    "\uD83D\uDD04" -> {
////                        Roulette(reaction.message, user)
////                        reaction.message.delete()
////                    }
////                    "♠" -> {
////                        val players = ArrayList<Blackjack.Player>()
////                        players.add(Blackjack.Player(user, ArrayList(), 0, false))
////                        Blackjack(reaction.message, players)
////                        reaction.message.delete()
////                    }
//                }
//            }
//        }
//
//    }
}
