package com.nic.botsuite.chloe

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import de.btobastian.javacord.entities.Server
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.PrintWriter

/**
 * Created by Nictogen on 2/2/18
 */
object SettingsHandler {

    private val list = ArrayList<Settings>()

    fun getSettings(server : Server): Settings {
        var settings = list.firstOrNull { it.serverID == server.id }
        if (settings == null) {
            settings = Settings(server.id, false, ArrayList())
            list.add(settings)
            saveData(server.id)
        }
        return settings
    }

    class Settings(val serverID: Long, var anyoneCreate : Boolean, val exemptChannels : ArrayList<Long>)


    fun loadData(server: Server) {
        val dr = File("chloeData/${server.id}")
        dr.mkdirs()
        val file = File(dr, "settings.json")
        if (!file.createNewFile()) {
            val parser = JsonParser()
            try {
                val json = parser.parse(FileReader(file)).asJsonObject
                val anyoneCreate = if(json.has("anyoneCreate")) json.get("anyoneCreate").asBoolean else false
                val exemptChannelsJson = if(json.has("exemptChannels")) json.get("exemptChannels").asJsonArray else JsonArray()
                val exemptChannels = ArrayList<Long>()
                exemptChannelsJson.forEach {
                    exemptChannels.add(it.asLong)
                }
                list.add(Settings(server.id, anyoneCreate, exemptChannels))
            } catch (e: Exception) {
            }
        } else file.delete()
    }

    fun saveData(serverID: Long) {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val dr = File("chloeData/$serverID")
        dr.mkdirs()
        val settings = list.firstOrNull { it.serverID == serverID }
        if (settings != null) {
            val json = gson.toJson(settings).toString()
            val dataFile = File(dr, "settings.json")
            dataFile.createNewFile()
            val printWriter = PrintWriter(FileWriter(dataFile))
            printWriter.write(json)
            printWriter.close()
        }
    }
}