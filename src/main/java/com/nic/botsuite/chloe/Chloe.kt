package com.nic.botsuite.chloe

import com.nic.botsuite.chloe.menus.MainMenu
import com.nic.botsuite.common.util.Module
import com.nic.botsuite.common.util.QuestionHandler
import de.btobastian.javacord.DiscordApi
import de.btobastian.javacord.entities.User
import de.btobastian.javacord.entities.channels.TextChannel
import de.btobastian.javacord.entities.permissions.PermissionType

/**
 * Created by Nictogen on 2/1/18
 */
class Chloe(api: DiscordApi) : Module(api) {

    override fun main() {
        api.servers.forEach {
            try {
                SettingsHandler.loadData(it)
                CharacterHandler.loadData(it)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        api.addMessageCreateListener(CharacterHandler)
    }

    override fun newMainMenu(user: User, channel: TextChannel): QuestionHandler.Question {
        val admin = channel.asServerTextChannel().get().hasPermission(user, PermissionType.ADMINISTRATOR)
        val fields = ArrayList<QuestionHandler.Field>()
        val settings = SettingsHandler.getSettings(channel.asServerTextChannel().get().server)
        if (admin || settings.anyoneCreate) {
            fields.add(QuestionHandler.Field("\uD83D\uDC64", "Create a Character", true))
        }
        if (CharacterHandler.characters.any { it.serverID == channel.asServerTextChannel().get().server.id && it.userID == user.id }) {
            fields.add(QuestionHandler.Field("\uD83D\uDC94", "Delete a Character", true))
        }
        if (admin) {
            fields.add(QuestionHandler.Field("⚙", "Adjust Settings", true))
            val exempt = settings.exemptChannels.any { it == channel.id }
            fields.add(QuestionHandler.Field(if (exempt) "\uD83D\uDC4C" else "\uD83D\uDD95", if (exempt) "Use characters again in this channel" else "Don't use characters in this channel", true))
        }
        fields.add(QuestionHandler.Field("\uD83D\uDEAB", "Cancel", true))
        return MainMenu(user, channel, *fields.toTypedArray())
    }

}