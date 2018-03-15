package com.nic.botsuite.common.util

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import de.btobastian.javacord.entities.Webhook
import de.btobastian.javacord.entities.channels.ServerTextChannel
import de.btobastian.javacord.entities.channels.TextChannel
import de.btobastian.javacord.entities.message.Message
import de.btobastian.javacord.entities.message.embed.EmbedBuilder
import de.btobastian.javacord.utils.rest.RestEndpoint
import de.btobastian.javacord.utils.rest.RestMethod
import de.btobastian.javacord.utils.rest.RestRequest
import java.awt.Color




/**
 * Created by AFlyingGrayson on 9/3/17
 */

fun Int.toEmoji() : String? {
    return when(this){
        in 0..9 -> "$this\u20e3"
        else -> null
    }
}

fun ServerTextChannel.createWebhook(name: String) {
    RestRequest<Void>(this.api, RestMethod.POST, RestEndpoint.CHANNEL_WEBHOOK).setUrlParameters(this.id.toString()).setBody(JsonNodeFactory.instance.objectNode().put("name", name)).executeBlocking()
}

fun Webhook.execute(username: String, avatarUrl: String, content: String){
    RestRequest<Void>(this.channel.get().api, RestMethod.POST, RestEndpoint.WEBHOOK)
            .setUrlParameters("${this.id}/${this.token.get()}")
            .setBody(JsonNodeFactory.instance.objectNode()
            .put("username", username)
            .put("avatar_url", avatarUrl)
            .put("content", content)).executeBlocking()
}

inline fun TextChannel.sendEmbedMessage(color: Color, content: String = "", func: EmbedBuilder.() -> Unit): Message = this.sendMessage(content, EmbedBuilder().setColor(color).apply(func)).get()