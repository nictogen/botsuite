package com.nic.botsuite.gwen

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.nic.botsuite.gwen.champions.Champions
import com.nic.botsuite.gwen.champions.PlayerSpells
import de.btobastian.javacord.entities.Server
import de.btobastian.javacord.entities.User
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.PrintWriter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.reflect.KClass

/**
 * Created by Nictogen on 3/3/18.
 */
object DataHandler {

    private val data = HashMap<Long, ArrayList<ChampionData>>()

    class ChampionData(var userID: Long, val collection : ArrayList<Card>, var currentChampion : Card, var currentSpells : Array<Card>)

    class Card(val type : CardType, val id : Int){
        fun getClass() : KClass<out Any>{
            return if(type == CardType.SPELL)PlayerSpells.list[id]!! else Champions.list[id]!!
        }
    }

    enum class CardType {
        SPELL,
        CHAMPION
    }

    fun getData(server: Server, user: User): ChampionData {
        return if (data.containsKey(server.id)) {
            if (data[server.id]!!.any { it.userID == user.id })
                data[server.id]!!.first { it.userID == user.id }
            else {
                val charData = ChampionData(user.id, ArrayList(), Card(CardType.CHAMPION, Random().nextInt(3)), arrayOf(Card(CardType.SPELL, 0), Card(CardType.SPELL, 1), Card(CardType.SPELL, 2), Card(CardType.SPELL, 3), Card(CardType.SPELL, 4)))
                data[server.id]!!.add(charData)
                saveData(server)
                charData
            }
        } else {
            val list = ArrayList<ChampionData>()
            val charData = ChampionData(user.id, ArrayList(), Card(CardType.CHAMPION, Random().nextInt(3)), arrayOf(Card(CardType.SPELL, 0), Card(CardType.SPELL, 1), Card(CardType.SPELL, 2), Card(CardType.SPELL, 3), Card(CardType.SPELL, 4)))
            list.add(charData)
            data.put(server.id, list)
            saveData(server)
            charData
        }
    }

    fun loadData(server: Server){
        val dr = File("gwenData/${server.id}")
        dr.mkdirs()
        val files = dr.listFiles().filter { it.isFile }
        val parser = JsonParser()
        val list =
                if(data.containsKey(server.id)){
                    data[server.id]!!
                } else {
                    val dataList = ArrayList<ChampionData>()
                    data.put(server.id, dataList)
                    dataList
                }
        files.forEach {
            try {
                val json = parser.parse(FileReader(it)).asJsonObject
                val userID : Long = if(json.has("userID")) json.get("userID").asLong else 0

                val collectionJson = if(json.has("collection")) json.get("collection").asJsonArray else JsonArray()
                val collection = ArrayList<Card>()
                collectionJson.forEach{
                    val card = it.asJsonObject
                    val type = if(card.has("type")) CardType.valueOf(card.get("type").asString) else CardType.SPELL
                    val id = if(card.has("id")) card.get("id").asInt else 0
                    collection.add(Card(type, id))
                }

                val championCard = if(json.has("currentChampion")) json.get("currentChampion").asJsonObject else JsonObject()
                val championId = if(championCard.has("id")) championCard.get("id").asInt else 0

                val spellsJson = if(json.has("currentSpells")) json.get("currentSpells").asJsonArray else JsonArray()
                val spells = ArrayList<Card>()
                spellsJson.forEach{
                    val card = it.asJsonObject
                    val id = if(card.has("id")) card.get("id").asInt else 0
                    spells.add(Card(CardType.SPELL, id))
                }

                val data = ChampionData(userID, collection, Card(CardType.CHAMPION, championId), spells.toTypedArray())
                list.add(data)
            } catch (e : Exception){}
        }
    }

    fun saveData(server: Server){
        val gson = GsonBuilder().setPrettyPrinting().create()
        val dr = File("gwenData/${server.id}")
        dr.mkdirs()
        val list = data[server.id]!!
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
