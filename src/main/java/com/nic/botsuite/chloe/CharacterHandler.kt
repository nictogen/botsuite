package com.nic.botsuite.chloe

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.nic.botsuite.common.util.createWebhook
import com.nic.botsuite.common.util.execute
import de.btobastian.javacord.entities.Server
import de.btobastian.javacord.events.message.MessageCreateEvent
import de.btobastian.javacord.listeners.message.MessageCreateListener
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.PrintWriter
import java.util.*

/**
 * Created by Nictogen on 2/1/18
 */
object CharacterHandler : MessageCreateListener {

    val characters = ArrayList<Character>()

    override fun onMessageCreate(event : MessageCreateEvent) {
        try {
            if (event.message.author.isUser && event.channel.asServerTextChannel().isPresent && !event.message.mentionedUsers.any { it.isBot }) {
                val channel = event.channel.asServerTextChannel().get()
                if (!SettingsHandler.getSettings(channel.server).exemptChannels.any { it == channel.id }) {
                    val symbol = if (event.message.content.length > 2) if (event.message.content[1] == ' ') event.message.content[0] else '~' else '~'
                    val character = characters.filter { it.serverID == event.server.get().id && it.userID == event.message.author.id }.firstOrNull { it.symbol == symbol }
                    if (character != null) {
                        if (!channel.webhooks.get().any { it.name.get() == "chloe-webhook" }) {
                            channel.createWebhook("chloe-webhook")
                        }
                        val webhook = channel.webhooks.get().firstOrNull { it.name.get() == "chloe-webhook" }
                        if(webhook != null) {
                            val content = if (symbol == '~') event.message.content else event.message.content.substring(2, event.message.content.length)
                            webhook.execute(character.name, character.picUrl, content)
                            event.message.delete()
                        }
                    }
                }
            }
        } catch (e : Exception){
            e.printStackTrace()
        }
    }

    data class Character(val userID : Long, val serverID : Long, val name : String, val picUrl : String, val symbol : Char)


    fun loadData(server : Server){
        val dr = File("chloeData/${server.id}/characters")
        dr.mkdirs()
        val files = dr.listFiles().filter { it.isFile }
        val parser = JsonParser()
        files.forEach {
            try {
                val json = parser.parse(FileReader(it)).asJsonObject
                val userID : Long = if(json.has("userID")) json.get("userID").asLong else 0
                val name : String = if(json.has("name")) json.get("name").asString else ""
                val picUrl : String = if(json.has("picUrl")) json.get("picUrl").asString else ""
                val symbol : Char = if(json.has("symbol")) json.get("symbol").asCharacter else ' '
                val character = Character(userID, server.id, name, picUrl, symbol)
                characters.add(character)
            } catch (e : Exception){}
        }
    }

    fun saveData(server : Server){
        val gson = GsonBuilder().setPrettyPrinting().create()
        val dr = File("chloeData/${server.id}/characters")
        dr.mkdirs()
        val files = dr.listFiles().filter { it.isFile }
        files.forEach { it.delete() }
        val list = characters.filter { it.serverID == server.id }
        list.forEach {
            val json = gson.toJson(it).toString()
            val dataFile = File(dr, it.userID.toString() + "-" + it.name + ".json")
            dataFile.createNewFile()
            val printWriter = PrintWriter(FileWriter(dataFile))
            printWriter.write(json)
            printWriter.close()
        }
    }
}