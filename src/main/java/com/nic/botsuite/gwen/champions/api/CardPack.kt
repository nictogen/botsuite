package com.nic.botsuite.gwen.champions.api

import com.nic.botsuite.gwen.DataHandler
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Nictogen on 3/6/18.
 */
class CardPack {

    private val cards = HashMap<Rarity, ArrayList<DataHandler.Card>>()

    fun add(rarity: Rarity, card : DataHandler.Card) : CardPack {
        if(cards.containsKey(rarity)){
           cards[rarity]!!.add(card)
        } else {
            cards.put(rarity, arrayListOf(card))
        }
        return this
    }

    fun pullCards() : ArrayList<DataHandler.Card>{
        val r = Random()
        val list = ArrayList<DataHandler.Card>()
        val commons = cards[Rarity.COMMON]!!
        val uncommons = cards[Rarity.UNCOMMON]!!
        val rares = cards[Rarity.RARE]!!
        val legendaries = cards[Rarity.LEGENDARY]!!

        list.add(commons[r.nextInt(commons.size)])
        list.add(commons[r.nextInt(commons.size)])
        if(r.nextInt(3) != 0) list.add(commons[r.nextInt(commons.size)]) else list.add(uncommons[r.nextInt(uncommons.size)])
        if(r.nextInt(3) != 0) list.add(rares[r.nextInt(rares.size)]) else list.add(legendaries[r.nextInt(legendaries.size)])


        val returnList = ArrayList<DataHandler.Card>()

        list.forEach {
            returnList.add(DataHandler.Card(it.type, it.id))
        }

        return returnList
    }

    enum class Rarity{
        COMMON,
        UNCOMMON,
        RARE,
        LEGENDARY
    }
}
