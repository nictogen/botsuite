package com.nic.botsuite.mercedes.handlers

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import de.btobastian.javacord.entities.Server
import de.btobastian.javacord.entities.User
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.PrintWriter
import java.util.*

/**
 * Created by Nictogen on 1/20/18
 */
object DataHandler {

    val data = HashMap<Server, ArrayList<CharacterData>>()

    class CharacterData(var userID: Long, var amount: Int, var lastIncome: Long)

    fun getData(server: Server, user: User): CharacterData {
        return if (data.containsKey(server)) {
            if (data[server]!!.any { it.userID == user.id })
                data[server]!!.first { it.userID == user.id }
            else {
                val charData = CharacterData(user.id, 0, 0)
                data[server]!!.add(charData)
                saveData(server)
                charData
            }
        } else {
            val list = ArrayList<CharacterData>()
            val charData = CharacterData(user.id, 0, 0)
            list.add(charData)
            data.put(server, list)
            saveData(server)
            charData
        }
    }

    fun loadData(server: Server){
        val dr = File("mercedesData/${server.id}")
        dr.mkdirs()
        val files = dr.listFiles().filter { it.isFile }
        val parser = JsonParser()
        val list =
        if(data.containsKey(server)){
            data[server]!!
        } else {
            val dataList = ArrayList<CharacterData>()
            data.put(server, dataList)
            dataList
        }
        files.forEach {
            try {
                val json = parser.parse(FileReader(it)).asJsonObject
                val userID : Long = if(json.has("userID")) json.get("userID").asLong else 0
                val amount : Int = if(json.has("amount")) json.get("amount").asInt else 0
                val lastIncome : Long = if(json.has("lastIncome")) json.get("lastIncome").asLong else 0
                val data = CharacterData(userID, amount, lastIncome)
                list.add(data)
            } catch (e : Exception){}
        }
    }

    fun saveData(server: Server){
        val gson = GsonBuilder().setPrettyPrinting().create()
        val dr = File("mercedesData/${server.id}")
        dr.mkdirs()
        val list = data[server]!!
        list.forEach {
            val json = gson.toJson(it).toString()
            val dataFile = File(dr, it.userID.toString() + ".json")
            dataFile.createNewFile()
            val printWriter = PrintWriter(FileWriter(dataFile))
            printWriter.write(json)
            printWriter.close()
        }
    }
}