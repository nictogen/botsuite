package com.nic.botsuite.gwen.champions

import com.nic.botsuite.gwen.champions.api.*
import java.util.*
import kotlin.reflect.KClass

/**
 * Created by Nictogen on 3/2/18.
 */
class PlayerSpells {

    companion object {
        val list = HashMap<Int, KClass<out Spell>>()
        init {
            list.put(0, Boost::class)
            list.put(1, Weaken::class)
            list.put(2, Heal::class)
            list.put(3, Burn::class)
            list.put(4, Block::class)
            list.put(5, Haste::class)
            list.put(6, Haze::class)
            list.put(7, Taunt::class)
//8-12
            list.put(13, Sharpen::class)
            list.put(14, Sap::class)
            list.put(15, Swap::class)
            list.put(16, Corrupt::class)
            list.put(17, Purify::class)
            list.put(18, ImprovedBlock::class)
            list.put(19, ScalingBoost::class)
            list.put(20, ScalingWeaken::class)
            list.put(21, Tendrils::class)
            list.put(22, Thorns::class)
            list.put(23, ShieldBreak::class)
            list.put(24, CatchUp::class)
            list.put(25, HardBrake::class)
            list.put(26, MasterBlock::class)
            list.put(27, Meditate::class)
            list.put(28, DrainLife::class)
            list.put(29, ShieldBash::class)
            list.put(30, BladeStorm::class)
            list.put(31, SecondWind::class)
            list.put(32, FinalFlash::class)
            list.put(33, Focus::class)
            list.put(34, Flurry::class)
        }
    }
    class Boost : Spell("Boost", CastPhase.BEFORE_COMBAT, 2) {
        override fun effect(battle: Battle, caster: Player, opponent: Player) = caster.increaseStat(2, Champion.StatType.ATTACK, false)
    }

    class Weaken : Spell("Weaken", CastPhase.BEFORE_COMBAT, 2) {
        override fun effect(battle: Battle, caster: Player, opponent: Player) = caster.reduceStat(2, Champion.StatType.ATTACK, false)
    }

    class Heal : Spell("Heal", CastPhase.BEFORE_COMBAT, 2) {
        override fun effect(battle: Battle, caster: Player, opponent: Player) = caster.heal(2)
    }

    class Burn : Spell("Burn", CastPhase.BEFORE_COMBAT, 2) {
        override fun effect(battle: Battle, caster: Player, opponent: Player) = opponent.takeDamage(battle, caster, 2, Battle.DamageType.MAGICAL)
    }

    class Swap : Spell("Swap", CastPhase.BEFORE_COMBAT, 3) {
        override fun effect(battle: Battle, caster: Player, opponent: Player) : String {
            val userAtk = caster.champion.stats.attack
            caster.champion.stats.attack = opponent.champion.stats.attack
            opponent.champion.stats.attack = userAtk
            return "The attack power of both champions was swapped for the round!"
        }
    }

    class HardBrake : Spell("Hard Brake", CastPhase.BEFORE_COMBAT, 3){
        override fun effect(battle: Battle, caster: Player, opponent: Player): String {
            val ogDifference = caster.champion.stats.ogSpeed - opponent.champion.stats.speed
            val difference = caster.champion.stats.speed - opponent.champion.stats.speed - ogDifference
            return if(difference > 0 || ogDifference > 0){
                var string = ""
                if(ogDifference > 0) string += "${caster.reduceStat(ogDifference, Champion.StatType.SPEED, true)}\n\n"
                if(difference > 0) string += caster.reduceStat(difference, Champion.StatType.SPEED, false) + "\n\n"
                string += opponent.takeDamage(battle, caster, ogDifference + difference, Battle.DamageType.MAGICAL)
                return string
            } else {
                "${caster.name} tried to cast $name, but their ${caster.champion.name}'s speed was too low."
            }
        }
    }

    class ScalingBoost : Spell("Scaling Boost", CastPhase.BEFORE_COMBAT, 1){
        override fun effect(battle: Battle, caster: Player, opponent: Player) = caster.increaseStat(manaCost++, Champion.StatType.ATTACK, false)
    }

    class ScalingWeaken : Spell("Scaling Weaken", CastPhase.BEFORE_COMBAT, 1){
        override fun effect(battle: Battle, caster: Player, opponent: Player) = opponent.reduceStat(manaCost++, Champion.StatType.ATTACK, false)
    }

    class Block : Spell("Block", CastPhase.PRIORITY, 2){
        override fun effect(battle: Battle, caster: Player, opponent: Player) = caster.addShield(1, battle)
    }

    class ImprovedBlock : Spell("Improved Block", CastPhase.PRIORITY, 4){
        override fun effect(battle: Battle, caster: Player, opponent: Player) = caster.addShield(2, battle)
    }

    class MasterBlock : Spell("Master Block", CastPhase.PRIORITY, 6){
        override fun effect(battle: Battle, caster: Player, opponent: Player) = caster.addShield(3, battle)
    }

    class DrainLife : Spell("Drain Life", CastPhase.BEFORE_COMBAT, 4){
        override fun effect(battle: Battle, caster: Player, opponent: Player) = "${opponent.takeDamage(battle, caster, 2, Battle.DamageType.MAGICAL)}\n\n${caster.heal(2)}"
    }

    class Taunt : Spell("Taunt", CastPhase.BEFORE_COMBAT, 2){
        override fun effect(battle: Battle, caster: Player, opponent: Player): String {
            return if(!opponent.champion.attacking){
                opponent.champion.attacking = true
                "${opponent.name}'s ${opponent.champion.name} was forced to attack!"
            } else {
                "But ${opponent.name}'s ${opponent.champion.name} was already attacking."
            }
        }
    }

    class Corrupt : Spell("Corrupt", CastPhase.BEFORE_COMBAT, 2){
        override fun effect(battle: Battle, caster: Player, opponent: Player) = opponent.changeRace(battle, caster, Champion.Race.DEMON)
    }

    class Purify : Spell("Purify", CastPhase.BEFORE_COMBAT, 2){
        override fun effect(battle: Battle, caster: Player, opponent: Player) = if(caster.champion.ogRace == Champion.Race.DEMON) caster.changeRace(battle, opponent, Champion.Race.HUMAN) else caster.changeRace(battle, opponent, caster.champion.ogRace)
    }

    class Meditate : Spell("Meditate", CastPhase.LAST, 0){
        override fun effect(battle: Battle, caster: Player, opponent: Player): String {
            var canCast = !caster.champion.attacking
            if(caster.champion.spell.casting && caster.champion.spell.castPhase != CastPhase.PASSIVE) canCast = false
            caster.spells.forEach {
                if(it.casting && it.castPhase != CastPhase.PASSIVE && it !is Meditate) canCast = false
            }
            return if(canCast){
                caster.addMana(2)
            } else "${caster.name}'s $name failed."
        }
    }

    class ShieldBash : Spell("Shield Bash", CastPhase.BEFORE_COMBAT, 1) {
        override fun effect(battle: Battle, caster: Player, opponent: Player): String {
            val amount = caster.champion.shields
            return if(amount > 0){
                caster.champion.shields -= amount
                "${caster.name} ${caster.champion.name}'s $amount shields broke.\n\n" + opponent.takeDamage(battle, opponent, amount*2, Battle.DamageType.MAGICAL)
            } else "But ${caster.name}'s ${caster.champion.name} didn't have any shields to break."
        }
    }

    class ShieldBreak : Spell("Shield Break", CastPhase.BEFORE_COMBAT, 3) {
        override fun effect(battle: Battle, caster: Player, opponent: Player): String {
            var amount = opponent.champion.shields
            return if(amount > 0){
                if(amount > 2) amount = 2
                caster.champion.shields -= amount
                "${caster.name} ${caster.champion.name} broke $amount shields on ${opponent.name}'s ${opponent.champion.name}. They now have ${opponent.champion.shields} left."
            } else "But ${opponent.name}'s ${opponent.champion.name} didn't have any shields to break."
        }
    }

    class Tendrils : Spell("Tendrils", CastPhase.PRIORITY, 1) {
        override fun effect(battle: Battle, caster: Player, opponent: Player) = opponent.reduceStat(manaCost++, Champion.StatType.SPEED, false)
    }

    class Thorns : Spell("Thorns", CastPhase.PRIORITY, 0) {
        override fun effect(battle: Battle, caster: Player, opponent: Player) = if(manaCost++ == 0) "${caster.name} planted thorns on ${opponent.name}'s ${opponent.champion.name}." else "${caster.name}'s thorns grew on ${opponent.champion.name}\n\n${opponent.takeDamage(battle, caster, manaCost, Battle.DamageType.MAGICAL)}"
    }

    class CatchUp: Spell("Catch Up", CastPhase.BEFORE_COMBAT, 2){
        override fun effect(battle: Battle, caster: Player, opponent: Player): String {
            val difference = opponent.champion.stats.speed - caster.champion.stats.speed
            return if (difference > 0) caster.increaseStat(difference, Champion.StatType.SPEED, true) else "But ${caster.champion.name} already had enough speed."
        }
    }

    class Haste : Spell("Haste", CastPhase.BEFORE_COMBAT, 3){
        override fun effect(battle: Battle, caster: Player, opponent: Player): String {
            caster.champion.extraAttacks++
            return "${caster.name}'s ${caster.champion.name} gained an extra attack."
        }
    }

    class Haze : Spell("Haze", CastPhase.PRIORITY, 2){
        override fun effect(battle: Battle, caster: Player, opponent: Player): String {
            caster.champion.dodgeChance += 25
            return "${caster.name} surrounded their ${caster.champion.name} with mist, giving them a chance to dodge attacks."
        }
    }

    class Sharpen : Spell("Sharpen", CastPhase.BEFORE_COMBAT, 4){
        override fun effect(battle: Battle, caster: Player, opponent: Player) = caster.increaseStat(1, Champion.StatType.ATTACK, true)
    }

    class Sap : Spell("Sap", CastPhase.BEFORE_COMBAT, 4){
        override fun effect(battle: Battle, caster: Player, opponent: Player) = caster.reduceStat(1, Champion.StatType.ATTACK, true)
    }

    class Focus : Spell("Focus", CastPhase.HIGH_PRIORITY, 8){
        override fun effect(battle: Battle, caster: Player, opponent: Player): String {
            caster.spells.forEach {
                it.manaCost--
            }
            return "${caster.name} reduced the cost of all their spells by one, permanently."
        }
    }

    class BladeStorm : Spell("Blade Storm", CastPhase.BEFORE_COMBAT, 3){
        override fun effect(battle: Battle, caster: Player, opponent: Player) = opponent.takeDamage(battle, caster, Random().nextInt(4) + 1, Battle.DamageType.MAGICAL)
    }

    class SecondWind : Spell("Second Wind", CastPhase.BEFORE_COMBAT, 3){
        override fun effect(battle: Battle, caster: Player, opponent: Player) = when {
                caster.champion.health <= (caster.champion.stats.maxHealth.toDouble())/4.0 -> caster.heal(5)
                caster.champion.health <= (caster.champion.stats.maxHealth.toDouble())/2.0 -> caster.heal(3)
                else -> caster.heal(1)
            }
    }

    class FinalFlash : Spell("Final Flash", CastPhase.BEFORE_COMBAT, 3){
        override fun effect(battle: Battle, caster: Player, opponent: Player) = when {
            caster.champion.health <= (caster.champion.stats.maxHealth.toDouble())/4.0 -> opponent.takeDamage(battle, caster, 5, Battle.DamageType.MAGICAL)
            caster.champion.health <= (caster.champion.stats.maxHealth.toDouble())/2.0 -> opponent.takeDamage(battle, caster, 3, Battle.DamageType.MAGICAL)
            else -> opponent.takeDamage(battle, caster, 1, Battle.DamageType.MAGICAL)
        }
    }

    class Flurry : OnGiveDamageSpell("Flurry", CastPhase.BEFORE_COMBAT, 5){

        var toReduce = 0

        override fun onGiveDamage(battle: Battle, attacker: Player, defender: Player, amount: Int, type: Battle.DamageType) = if(toReduce > 0)  "\n\n${attacker.name}'s $name activates.\n\n" + attacker.reduceStat(1, Champion.StatType.ATTACK, false) else ""

        override fun effect(battle: Battle, caster: Player, opponent: Player): String {
            toReduce = 3
            caster.champion.extraAttacks += 2
            return "${caster.name}'s ${caster.champion.name} gained two more attacks!"
        }

        override fun reset() {
            super.reset()
            toReduce = 0
        }
    }

}
