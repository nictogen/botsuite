package com.nic.botsuite.common.util

import de.btobastian.javacord.DiscordApi
import de.btobastian.javacord.entities.User
import de.btobastian.javacord.entities.channels.TextChannel

/**
 * Created by Nictogen on 2/4/18
 */
abstract class Module(var api: DiscordApi) {

    abstract fun main()

    fun setup() : Module {
        QuestionHandler.questionLists.put(api, ArrayList())
        api.addMessageCreateListener(QuestionHandler)
        api.addReactionAddListener(QuestionHandler)
        api.addMessageDeleteListener(QuestionHandler)
        api.addMessageCreateListener({ event ->
            val author = event.message.userAuthor
            if (author.isPresent && !author.get().isBot && event.message.content.contains(api.yourself.idAsString) && event.message.content.split(" ".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray().size == 1) {
                newMainMenu(author.get(), event.channel)
                event.message.delete()
            }
        })
        main()
        return this
    }

    abstract fun newMainMenu(user: User, channel : TextChannel) : QuestionHandler.Question
}
