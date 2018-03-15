package com.nic.botsuite.gwen.menus.collection

import com.nic.botsuite.common.util.QuestionHandler
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
import kotlin.reflect.full.createInstance

/**
 * Created by Nictogen on 3/3/18.
 */
//TODO spells run off page
class ChangeSpell(user: User, channel: TextChannel, private val spellSlot : Int, vararg fields: QuestionHandler.Field) : QuestionHandler.Question(user, channel, Color.WHITE, "", Optional.empty(), *fields) {
    override fun onMessage(server: Server, user: User, message: Message){}

    override fun onReaction(server: Server, user: User, reaction: Reaction) {
        if(reaction.emoji.isCustomEmoji){
            val newSpell = DataHandler.getData(server, user).collection.filter { it.type == DataHandler.CardType.SPELL }.firstOrNull { (it.getClass().createInstance() as Spell).emoji!!.id == reaction.emoji.asCustomEmoji().get().id }
            if(newSpell != null){
                val data = DataHandler.getData(server, user)
                data.collection.add(data.currentSpells[spellSlot])
                data.collection.remove(newSpell)
                data.currentSpells[spellSlot] = newSpell
                DataHandler.saveData(server)
                val champion = (data.currentChampion.getClass().createInstance() as Champion)
                val spells = data.currentSpells.map { (it.getClass().createInstance() as Spell) }
                var currentLoadout = "Current Champion: ${champion.emoji.mentionTag} ${champion.name}\n\n"
                currentLoadout += "Spells: "
                var int = 0
                spells.forEach {
                    currentLoadout += "Spell #${++int}: ${it.emoji!!.mentionTag} ${it.name}\n\n"
                }
                EditLoadout(user, reaction.message.channel, currentLoadout)
                reaction.message.delete()
            }
        } else if (reaction.emoji.asUnicodeEmoji().get() == "\uD83D\uDEAB"){
            val data = DataHandler.getData(server, user)
            val champion = (data.currentChampion.getClass().createInstance() as Champion)
            val spells = data.currentSpells.map { (it.getClass().createInstance() as Spell) }
            var currentLoadout = "Current Champion: ${champion.emoji.mentionTag} ${champion.name}\n\n"
            currentLoadout += "Spells: "
            var int = 0
            spells.forEach {
                currentLoadout += "Spell #${++int}: ${it.emoji!!.mentionTag} ${it.name}\n\n"
            }
            EditLoadout(user, reaction.message.channel, currentLoadout)
            reaction.message.delete()
        }
    }
}