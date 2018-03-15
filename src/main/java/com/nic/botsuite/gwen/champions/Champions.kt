package com.nic.botsuite.gwen.champions

import com.nic.botsuite.gwen.champions.api.Champion
import java.util.*
import kotlin.reflect.KClass

/**
 * Created by Nictogen on 3/2/18.
 */
class Champions {

    companion object {
        val list = HashMap<Int, KClass<out Champion>>()
        init {
            list.put(0, Orion::class)
            list.put(1, Hellspawn::class)
            list.put(2, Lich::class)

            list.put(3, Drachast::class)
            list.put(4, Dractum::class)
            list.put(5, Odysseus::class)
            list.put(6, Escalon::class)

            list.put(7, Chaos::class)
            list.put(8, Lilith::class)
            list.put(9, Dave::class)
            list.put(10, Seer::class)

            list.put(11, Sureny::class)
            list.put(12, Kane::class)
            list.put(13, Avalon::class)
            list.put(14, Durza::class)
        }
    }

    //Regular
    class Orion : Champion("Orion", Stats(20, 3, 1), ChampionSpells.OrionCharge(), Race.ORC)
    class Hellspawn : Champion("Hellspawn", Stats(25, 2, 3), ChampionSpells.FlameShield(), Race.DEMON)
    class Drachast : Champion("Drachast", Stats(20, 2, 1), ChampionSpells.TwinFang(), Race.DRAGON)
    class Chaos : Champion("Chaos", Stats(15, 1, 1), ChampionSpells.ChaosMagic(), Race.DRAGON)
    class Lich : Champion("Lich", Stats(30, 1, 1), ChampionSpells.ChannelLife(), Race.DEMON)
    class Dractum : Champion("Dractum", Stats(20, 1, 2), ChampionSpells.IronDefense(), Race.DRAGON)
    class Escalon : Champion("Escalon", Stats(20, 2, 2), ChampionSpells.ExperiencedBlocker(), Race.HUMAN)
    class Lilith : Champion("Lilith", Stats(25, 1, 2), ChampionSpells.SpawnBrood(), Race.DEMON)
    class Dave : Champion("Dave", Stats(30, 2, 1), ChampionSpells.BarterSoul(), Race.DEMON)
    class Odysseus : Champion("Odysseus", Stats(20, 3, 1), ChampionSpells.GoHome(), Race.HUMAN)
    class Seer : Champion("Seer", Stats(15, 2, 2), ChampionSpells.SeeFuture(), Race.HUMAN)

    //Legends
    class Sureny : Champion("Sureny", Stats(35, 3, 3), ChampionSpells.Masochism(), Race.LEGEND)
    class Kane : Champion("Kane", Stats(30, 2, 5), ChampionSpells.ProtagonistPower(), Race.LEGEND)
    class Avalon : Champion("Avalon", Stats(20, 5, 1), ChampionSpells.RighteousSmite(), Race.LEGEND)
    class Durza : Champion("Durza", Stats(30, 3, 3), ChampionSpells.EdgeFactor(), Race.LEGEND)

}
