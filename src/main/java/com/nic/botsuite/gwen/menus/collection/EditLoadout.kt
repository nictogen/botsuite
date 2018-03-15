package com.nic.botsuite.gwen.menus.collection

import com.nic.botsuite.common.util.QuestionHandler
import com.nic.botsuite.common.util.toEmoji
import com.nic.botsuite.gwen.DataHandler
import com.nic.botsuite.gwen.champions.api.Champion
import com.nic.botsuite.gwen.champions.api.Spell
import de.btobastian.javacord.entities.Server
import de.btobastian.javacord.entities.User
import de.btobastian.javacord.entities.channels.TextChannel
import de.btobastian.javacord.entities.message.Message
import de.btobastian.javacord.entities.message.Reaction
import java.awt.Color
import java.util.*
import kotlin.collections.ArrayList
import kotlin.reflect.full.createInstance

/**
 * Created by Nictogen on 3/3/18.
 */
class EditLoadout(user: User, channel: TextChannel, currentLoadout : String) : QuestionHandler.Question(user, channel, Color.WHITE, "What would you like to change, ${user.getDisplayName(channel.asServerTextChannel().get().server)}?\n\n" + currentLoadout, Optional.empty(),
        QuestionHandler.Field("\uD83C\uDDE8", "Change Champion", true),
        QuestionHandler.Field(1.toEmoji().toString(), "Change Spell #1", true),
        QuestionHandler.Field(2.toEmoji().toString(), "Change Spell #2", true),
        QuestionHandler.Field(3.toEmoji().toString(), "Change Spell #3", true),
        QuestionHandler.Field(4.toEmoji().toString(), "Change Spell #4", true),
        QuestionHandler.Field(5.toEmoji().toString(), "Change Spell #5", true),
        QuestionHandler.Field("\uD83D\uDEAB", "Cancel", true))
{

    override fun onMessage(server: Server, user: User, message: Message) {}

    override fun onReaction(server: Server, user: User, reaction: Reaction) {
        if (reaction.emoji.isUnicodeEmoji) {

            when (reaction.emoji.asUnicodeEmoji().get()) {
                "\uD83C\uDDE8" -> {
                    val fields = ArrayList<QuestionHandler.Field>()
                    fields.add(QuestionHandler.Field("\uD83D\uDEAB", "Cancel", true))
                    DataHandler.getData(server, user).collection.filter { it.type == DataHandler.CardType.CHAMPION }.toSet().forEach {
                        val champion = it.getClass().createInstance() as Champion
                        fields.add(QuestionHandler.Field(champion.emoji.mentionTag, champion.name, true, Optional.of(champion.emoji)))
                    }
                    ChangeChampion(user, reaction.message.channel, *fields.toTypedArray())
                    reaction.message.delete()
                }
                "\uD83D\uDEAB" -> {
                    reaction.message.delete()
                }
                1.toEmoji() -> editSpell(user, 0, message, server)
                2.toEmoji() -> editSpell(user, 1, message, server)
                3.toEmoji() -> editSpell(user, 2, message, server)
                4.toEmoji() -> editSpell(user, 3, message, server)
                5.toEmoji() -> editSpell(user, 4, message, server)
            }
        }
    }

    private fun editSpell(user: User, int: Int, message: Message, server: Server){
        val fields = ArrayList<QuestionHandler.Field>()
        fields.add(QuestionHandler.Field("\uD83D\uDEAB", "Cancel", true))
        val data = DataHandler.getData(server, user)
        data.collection.filter { it.type == DataHandler.CardType.SPELL }.toSet().forEach {
            val spell = it.getClass().createInstance() as Spell
            fields.add(QuestionHandler.Field(spell.emoji!!.mentionTag, spell.name, true, Optional.of(spell.emoji)))
        }
        ChangeSpell(user, message.channel, int, *fields.toTypedArray())
        message.delete()
    }

}
