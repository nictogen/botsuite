package com.nic.mercedes.util

import de.btobastian.javacord.entities.Server
import de.btobastian.javacord.entities.User
import de.btobastian.javacord.entities.channels.TextChannel
import de.btobastian.javacord.entities.message.Message
import de.btobastian.javacord.entities.message.embed.EmbedBuilder
import de.btobastian.javacord.entities.permissions.PermissionType
import java.awt.Color



/**
 * Created by AFlyingGrayson on 9/3/17
 */
object TessUtils {
    fun isAdmin(user: User, server: Server) = user.getRoles(server).any { it.allowedPermissions.contains(PermissionType.MANAGE_CHANNELS) }

    fun listFromString(string: String): List<String> {
        var s = string
        s = s.replace("[", "")
        s = s.replace("]", "")
        var list = s.split(",")
        list = list.map { if(it.isNotEmpty() && it[0] == ' ') it.substring(1, it.length) else it }
        list = list.map { it.replace(",", "") }
        return list
    }

}

fun User.getRpName(server: Server) : String{
    return if(this.getNickname(server).isPresent) this.getNickname(server).get()
    else this.name
}

inline fun TextChannel.sendEmbedMessage(color: Color, content: String = "", func: EmbedBuilder.() -> Unit): Message = this.sendMessage(content, EmbedBuilder().setColor(color).apply(func)).get()