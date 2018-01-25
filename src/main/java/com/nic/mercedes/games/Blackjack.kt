package com.nic.mercedes.games

import com.nic.mercedes.handlers.DataHandler
import com.nic.mercedes.handlers.QuestionHandler
import com.nic.mercedes.init.Mercedes
import com.nic.mercedes.util.sendEmbedMessage
import de.btobastian.javacord.entities.Server
import de.btobastian.javacord.entities.User
import de.btobastian.javacord.entities.message.Message
import de.btobastian.javacord.entities.message.Reaction
import java.awt.Color
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Nictogen on 1/22/18
 */
class Blackjack(originalMessage: Message, var players: ArrayList<Player>, var message: Message = originalMessage.channel.sendEmbedMessage(Color.YELLOW, "")  {
    setTitle("Blackjack : Joining/Betting")
    setDescription("Place your starting bet.")
    addField("To Bet:", "`@Mercedes the Money Bot#8859 bet <amount>`", true)
    addField("Join Game", "✅", true)
    addField("Leave Game", "\uD83D\uDEAB", true)
}) : QuestionHandler.Question(message, players[0].user, message.id) {

    val deck : ArrayList<Card> = ArrayList()

    init {
        message.addReaction("✅")
        message.addReaction("\uD83D\uDEAB")
    }

    companion object {
        fun drawCard(deck: ArrayList<Card>) : Card {
            if(deck.isEmpty()){
                Suit.values().forEach { suit ->
                    Value.values().forEach { value ->
                        deck.add(Card(value, suit))
                    }
                }
            }
            Collections.shuffle(deck)
            val card = deck[0]
            deck.remove(card)
            return card
        }

        fun getValue(hand : ArrayList<Card>) : Int {
            var value = 0
            hand.filter { it.value != Value.ACE }.forEach {
                value += it.value.value
            }
            if(value > 21 || !hand.any { it.value == Value.ACE }) return value

            value += hand.filter { it.value == Value.ACE }.size
            if(value <= 11){
                value += 10
            }

            return value
        }
    }

    override fun onMessage(server: Server, user: User, message: Message) {
        try {
            val args = message.content.split(" ")
            if (args[0] == Mercedes.api.yourself.mentionTag && args[1] == "bet") {
                val bet = args[2].toInt()
                if (bet > 0 && DataHandler.getData(server, user).amount >= bet) {
                    this.players.first { it.user == user }.bet = bet
                    DataHandler.getData(server, user).amount -= bet
                    DataHandler.saveData(server)
                    message.delete()
                    if(!players.any { it.bet == 0 }){
                        val wait = Wait(this.message, this.user, drawCard(deck), deck, players)
                        players.forEach {
                            //TODO blackjack
                            it.cards.add(drawCard(deck))
                            it.cards.add(drawCard(deck))
                            Turn(wait, it, deck)
                        }
                        this.message.delete()
                    }
                } else {
                    QuestionHandler.Notify(message, "You don't have that much, sorry!", user)
                }
            }
        } catch (e: Exception) { }
    }

    override fun onReaction(server: Server, user: User, reaction: Reaction) {
        if (reaction.emoji.isUnicodeEmoji) {
            when (reaction.emoji.asUnicodeEmoji().get()) {
                "✅" -> {
                    if(!players.any { it.user == user }) {
                        players.add(Player(user, ArrayList(), 0, false))
                        message.removeReactionByEmoji(user, reaction.emoji)
                    }
                }
                "\uD83D\uDEAB" -> {
                    if(players.any { it.user == user }) {
                        players.remove(players.first { it.user == user })
                        message.removeReactionByEmoji(user, reaction.emoji)
                        if(players.isEmpty()){
                            this.message.delete()
                        }
                    }
                }
            }
        }
    }

    override fun isValidReactor(user: User) : Boolean {
        return true
    }

    override fun isValidMessager(user: User) : Boolean {
        return players.any { it.user == user }
    }

    class Wait(var originalMessage: Message, user: User, var dealerCard : Card, var deck: ArrayList<Card>, var players : ArrayList<Player>, var message: Message = originalMessage.channel.sendEmbedMessage(Color.YELLOW, "")  {
        setTitle("Blackjack : Waiting")
        var card = ""
        card += dealerCard.value.toString().toLowerCase().capitalize()
        card += " of "
        card += dealerCard.suit.toString().toLowerCase().capitalize()
        setDescription("The dealer's face up card is: $card. Please settle your hands.")
    }) : QuestionHandler.Question(message, user, message.id) {

        override fun onMessage(server: Server, user: User, message: Message) {}

        override fun onReaction(server: Server, user: User, reaction: Reaction) {}

        fun checkDone(server: Server){
            if(!players.any { it.done }) return
            else {
                val hand = ArrayList<Card>()
                hand.add(dealerCard)

                while(getValue(hand) <= 17) {
                    hand.add(drawCard(deck))
                }

                var dealerString = "The dealer got: "
                hand.forEach {
                    dealerString += it.value.toString().toLowerCase().capitalize()
                    dealerString += " of "
                    dealerString += it.suit.toString().toLowerCase().capitalize()
                    dealerString += ", "
                }

                dealerString += "for a total of ${getValue(hand)}"

                if(getValue(hand) > 21){
                    players.forEach {
                        if(getValue(it.cards) <= 21){
                            DataHandler.getData(message.channel.asServerTextChannel().get().server, user).amount += it.bet*2
                            DataHandler.saveData(server)

                            QuestionHandler.Notify(message, "$dealerString, and busted.\n\nYou won ${it.bet} points, ${it.user.getDisplayName(server)}!", user)
                        } else {
                            QuestionHandler.Notify(message, "$dealerString, and busted.\n\nYou busted and lost your bet, ${it.bet} points, ${it.user.getDisplayName(server)}.", user)
                        }
                    }
                } else {
                    players.forEach {
                        when {
                            getValue(it.cards) > 21 -> {
                                QuestionHandler.Notify(message, "$dealerString.\n\nYou busted and lost your bet, ${it.bet} points, ${it.user.getDisplayName(server)}.", user)
                            }
                            getValue(hand) > getValue(it.cards) -> {
                                QuestionHandler.Notify(message, "$dealerString.\n\nYou had ${getValue(it.cards)}, and lost your bet, ${it.bet} points, ${it.user.getDisplayName(server)}.", user)
                            }
                            getValue(hand) < getValue(it.cards) -> {
                                DataHandler.getData(message.channel.asServerTextChannel().get().server, user).amount += it.bet*2
                                DataHandler.saveData(server)

                                QuestionHandler.Notify(message, "$dealerString.\n\nYou had ${getValue(it.cards)}, and won ${it.bet} points, ${it.user.getDisplayName(server)}!", user)
                            }
                            else -> {
                                DataHandler.getData(message.channel.asServerTextChannel().get().server, user).amount += it.bet
                                DataHandler.saveData(server)

                                QuestionHandler.Notify(message, "$dealerString.\n\nYou tied the dealer and kept your bet, ${it.bet} points, ${it.user.getDisplayName(server)}.", user)

                            }
                        }
                    }
                }
                val newPlayers = ArrayList<Player>()
                this.players.forEach {
                    newPlayers.add(Player(it.user, ArrayList(), 0, false))
                }
                Blackjack(this.message, newPlayers)
                this.message.delete()
            }
        }

    }

    class Turn(var waiting: Wait, var currentPlayer: Player, var deck : ArrayList<Card>, message: Message = waiting.message.channel.sendEmbedMessage(Color.YELLOW, "")  {
        setTitle("Blackjack : ${currentPlayer.user.getDisplayName(waiting.message.channel.asServerTextChannel().get().server)}")
        var cards = ""
        currentPlayer.cards.forEach {
            cards += it.value.toString().toLowerCase().capitalize()
            cards += " of "
            cards += it.suit.toString().toLowerCase().capitalize()
            cards += ", "
        }
        setDescription("What will you do, ${currentPlayer.user.getDisplayName(waiting.message.channel.asServerTextChannel().get().server)}?\n" +
                "You have: ${cards}for a value of: ${getValue(currentPlayer.cards)}.")
        addField("Hit", "\uD83D\uDC46", true)
        addField("Stand", "\uD83D\uDD90", true)
    }) : QuestionHandler.Question(message, currentPlayer.user, message.id) {

        init {
            message.addReaction("\uD83D\uDC46")
            message.addReaction("\uD83D\uDD90")
        }

        override fun onMessage(server: Server, user: User, message: Message) {}

        override fun onReaction(server: Server, user: User, reaction: Reaction) {
            if (reaction.emoji.isUnicodeEmoji) {
                when (reaction.emoji.asUnicodeEmoji().get()) {
                    "\uD83D\uDC46" -> {
                       //HIT
                        currentPlayer.cards.add(drawCard(deck))
                        if(getValue(currentPlayer.cards) < 21){
                            Turn(this.waiting, currentPlayer, deck)
                        } else{
                            currentPlayer.done = true
                            if(getValue(currentPlayer.cards) == 21){
                                QuestionHandler.Notify(reaction.message, "You got 21, and must stand, ${currentPlayer.user.getDisplayName(server)}.", user)
                            } else {
                                QuestionHandler.Notify(reaction.message, "You got ${getValue(currentPlayer.cards)}, and busted, ${currentPlayer.user.getDisplayName(server)}.", user)
                            }
                        }
                        reaction.message.delete()
                    }
                    "\uD83D\uDD90" -> {
                        //STAND
                        currentPlayer.done = true
                        reaction.message.delete()
                    }
                }
            }
            if(currentPlayer.done){
                waiting.checkDone(server)
            }
        }

    }

    class Player(var user: User, var cards: ArrayList<Card>, var bet : Int, var done : Boolean)

    class Card(var value : Value, var suit: Suit)

    enum class Value(var value : Int){
        ONE(1),
        TWO(2),
        THREE(3),
        FOUR(4),
        FIVE(5),
        SIX(6),
        SEVEN(7),
        EIGHT(8),
        NINE(9),
        TEN(10),
        JACK(10),
        QUEEN(10),
        KING(10),
        ACE(11)
    }

    enum class Suit {
        SPADES,
        HEARTS,
        CLUBS,
        DIAMONDS
    }
}