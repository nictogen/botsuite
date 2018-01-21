package com.nic.mercedes.handlers

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

    class CharacterData(var userID: Long, var amount: Int, var livingCost: LivingCost, var lastIncome: Long, var lastCost : Long)

    enum class LivingCost {
        BASIC,
        MEDIUM,
        HIGH_LIFE
    }

    fun getData(server: Server, user: User): CharacterData {
        return if (data.containsKey(server)) {
            if (data[server]!!.any { it.userID == user.id })
                data[server]!!.first { it.userID == user.id }
            else {
                val charData = DataHandler.CharacterData(user.id, 0, LivingCost.BASIC, 0, Calendar.getInstance().time.time)
                data[server]!!.add(charData)
                saveData(server)
                charData
            }
        } else {
            val list = ArrayList<CharacterData>()
            val charData = DataHandler.CharacterData(user.id, 0, LivingCost.BASIC, 0, Calendar.getInstance().time.time)
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
                val livingCost : LivingCost = if(json.has("livingCost")) LivingCost.valueOf(json.get("livingCost").asString) else LivingCost.BASIC
                val lastIncome : Long = if(json.has("lastIncome")) json.get("lastIncome").asLong else 0
                val lastCost : Long = if(json.has("lastCost")) json.get("lastCost").asLong else  Calendar.getInstance().time.time
                val data = CharacterData(userID, amount, livingCost, lastIncome, lastCost)
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