//package com.nic.botsuite.common.mercedes.games
//
//import com.nic.botsuite.common.mercedes.MercedesMenus
//import com.nic.botsuite.common.mercedes.handlers.DataHandler
//import com.nic.botsuite.common.init.Main
//import com.nic.botsuite.util.QuestionHandler.Question
//import com.nic.botsuite.util.sendEmbedMessage
//import de.btobastian.javacord.entities.Server
//import de.btobastian.javacord.entities.User
//import de.btobastian.javacord.entities.message.Message
//import de.btobastian.javacord.entities.message.Reaction
//import java.awt.Color
//import java.util.*
//
///**
// * Created by Nictogen on 1/21/18
// */
//class Roulette(originalMessage: Message, user: User, var message: Message = originalMessage.channel.sendEmbedMessage(Color.YELLOW, "") {
//    setTitle("Roulette")
//    setDescription("Place your bet. Options:")
//    addField("Red/Black/Odd/Even/High/Low", "`@Main the Money Bot#8859 bet <type> <amount>`", true)
//    addField("Single Number", "`@Main the Money Bot#8859 bet <number> <amount>`", true)
//    addField("Quit", "\uD83D\uDEAB", true)
//}) : Question(message, user, message.id) {
//
//    override fun onMessage(server: Server, user: User, message: Message) {
//        try {
//            val args = message.content.split(" ")
//            if (args[0] == Main.mercedesAPI.yourself.mentionTag && args[1] == "bet") {
//                val bet = args[3].toInt()
//                if (bet > 0 && DataHandler.getData(message.channel.asServerTextChannel().get().server, user).amount >= bet) {
//                    val number = Random().nextInt(37)
//                    val odd = if (number == 0) false else number % 2 != 0
//                    val even = if (number == 0) false else !odd
//                    val red = if (number in 1..10 || number in 19..28) odd else if (number in 11..18 || number in 29..36) even else false
//                    val black = if (number == 0) false else !red
//
//                    val result = "$number" + if (red) "(Red)" else if (black) "(Black)" else ""
//                    var betString: String
//                    var amount = bet
//                    when (args[2].toUpperCase()) {
//                        "RED" -> {
//                            if (!red) amount *= -1
//                            betString = "You bet on red."
//                        }
//                        "BLACK" -> {
//                            if (!black) amount *= -1
//                            betString = "You bet on black."
//                        }
//                        "ODD" -> {
//                            if (!odd) amount *= -1
//                            betString = "You bet on odds."
//                        }
//                        "EVEN" -> {
//                            if (!even) amount *= -1
//                            betString = "You bet on evens."
//                        }
//                        "HIGH" -> {
//                            if (number < 19) amount *= -1
//                            betString = "You bet on high, 19-36."
//                        }
//                        "LOW" -> {
//                            if (number > 18 || number == 0) amount *= -1
//                            betString = "You bet on low, 1-18."
//                        }
//                        else -> {
//                            val betNumber = args[2].toInt()
//                            amount *= if (number == betNumber) 35 else -1
//                            betString = "You bet on a single number, $betNumber"
//                        }
//                    }
//                    Result(betString, result, amount, message, user)
//                    Roulette(this.message, user)
//                    this.message.delete()
//                    message.delete()
//                } else MercedesMenus.Notify(message, "You don't have that much, sorry!", user)
//
//            }
//        } catch (e: Exception) { }
//    }
//
//    override fun onReaction(server: Server, user: User, reaction: Reaction) {
//        if (reaction.emoji.isUnicodeEmoji) {
//            when (reaction.emoji.asUnicodeEmoji().get()) {
//                "\uD83D\uDEAB" -> {
//                    this.message.delete()
//                }
//            }
//        }
//    }
//
//    class Result(bet : String, result: String, amount : Int, originalMessage: Message, user: User, message: Message = originalMessage.channel.sendEmbedMessage(if(amount > 0) Color.GREEN else Color.RED, "") {
//        setDescription(bet + "\n\nThe ball landed on.... $result!\n\n" + if(amount > 0) "You won $amount points!" else "You lost ${amount*-1} points, sorry.")
//        DataHandler.getData(originalMessage.channel.asServerTextChannel().get().server, user).amount += amount
//        DataHandler.saveData(originalMessage.channel.asServerTextChannel().get().server)
//        addField("⏩", "Thank you", true)
//    }) : Question(message, user, message.id) {
//        init {
//            message.addReaction("⏩")
//        }
//
//        override fun onMessage(server: Server, user: User, message: Message) {}
//
//        override fun onReaction(server: Server, user: User, reaction: Reaction) {
//            if (reaction.emoji.isUnicodeEmoji && reaction.emoji.asUnicodeEmoji().get() == "⏩") reaction.message.delete()
//        }
//    }
//}