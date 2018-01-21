package com.nic.mercedes.init

import com.nic.mercedes.handlers.DataHandler
import com.nic.mercedes.handlers.QuestionHandler
import com.nic.mercedes.util.getRpName
import com.nic.mercedes.util.sendEmbedMessage
import java.awt.Color
import java.util.*


/**
 * Created by Nictogen on 1/20/18
 */
class Main {

    companion object {

        var messagesSinceCheck = 0

        @JvmStatic
        fun main() {

            //Connect to Mercedes Account
            Mercedes.api = PrivateTokens.getAPI()

            Mercedes.api.addMessageCreateListener { event ->
                if (!event.message.author.isYourself && event.message.content.contains(Mercedes.api.yourself.mentionTag)) {
                    val args = event.message.content.split(" ")
                    try {
                        if (args.size == 1) QuestionHandler.Menu(event.message, event.message.userAuthor.get())
                    } catch (e: Exception) {
                    }
                    event.message.delete()
                }
            }

            Mercedes.api.addMessageCreateListener { event ->
                if(messagesSinceCheck >= 100) {
                    val currentHours = Calendar.getInstance().time.time / 3600000
                    DataHandler.data.forEach { server, dataList ->
                        dataList.forEach {
                            try {
                                val lastCost = DataHandler.getData(event.message.channel.asServerTextChannel().get().server, event.message.userAuthor.get()).lastCost / 3600000
                                if (currentHours - lastCost >= 48) {
                                    val data = DataHandler.getData(event.message.channel.asServerTextChannel().get().server, event.message.userAuthor.get())
                                    data.lastCost = Calendar.getInstance().time.time
                                    data.amount = when (data.livingCost) {
                                        DataHandler.LivingCost.BASIC -> data.amount - 1000
                                        DataHandler.LivingCost.MEDIUM -> data.amount - 3000
                                        DataHandler.LivingCost.HIGH_LIFE -> data.amount - 10000
                                    }
                                    if (data.amount <= 0) {
                                        data.amount = 0
                                        event.message.serverTextChannel.get().sendEmbedMessage(Color.RED, "") {
                                            setDescription("${event.message.userAuthor.get().getRpName(server)} doesn't have any points to keep up their lifestyle.")
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                            }
                        }
                        DataHandler.saveData(server)
                    }
                messagesSinceCheck = 0
                } else messagesSinceCheck++
            }

            Mercedes.api.addMessageCreateListener(QuestionHandler)
            Mercedes.api.addReactionAddListener(QuestionHandler)

            Mercedes.api.servers.forEach { DataHandler.loadData(it) }
        }


    }


}