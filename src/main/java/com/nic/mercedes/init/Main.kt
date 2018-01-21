package com.nic.mercedes.init


/**
 * Created by Nictogen on 1/20/18
 */
class Main {

    companion object {

        @JvmStatic
        fun main() {

            //Connect to Mercedes Account
            Mercedes.api = PrivateTokens.getAPI()


        }


    }


}