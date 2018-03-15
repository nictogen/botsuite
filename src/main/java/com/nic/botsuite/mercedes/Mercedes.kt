package com.nic.botsuite.mercedes

import com.nic.botsuite.common.util.Module
import com.nic.botsuite.common.util.QuestionHandler
import com.nic.botsuite.mercedes.handlers.DataHandler
import de.btobastian.javacord.DiscordApi
import de.btobastian.javacord.entities.User
import de.btobastian.javacord.entities.channels.TextChannel


/**
 * Created by Nictogen on 1/20/18
 */
class Mercedes(api: DiscordApi) : Module(api) {

    override fun newMainMenu(user: User, channel: TextChannel): QuestionHandler.Question {
        val data = DataHandler.getData(channel.asServerChannel().get().server, user)
        val fields = ArrayList<QuestionHandler.Field>()
        fields.add(QuestionHandler.Field("\uD83D\uDCB0", "Check Balance", true))
        if (System.nanoTime() / 1000 - data.lastIncome >= 43200)
            fields.add(QuestionHandler.Field("\uD83D\uDCB5", "Claim Income", true))
        fields.add(QuestionHandler.Field("\uD83D\uDCEC", "Send to other", true))
        if (channel.asServerTextChannel().get().name.contains("card-shop"))
            fields.add(QuestionHandler.Field("\uD83D\uDED2", "Buy a card pack", true))
        return MercedesMenus.Menu(user, channel, *fields.toTypedArray())
    }

    override fun main() {
        api.servers.forEach { DataHandler.loadData(it) }
    }

}
