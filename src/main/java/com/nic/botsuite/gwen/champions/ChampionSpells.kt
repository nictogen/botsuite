package com.nic.botsuite.gwen.champions

import com.nic.botsuite.gwen.champions.api.*
import java.util.*

/**
 * Created by Nictogen on 3/2/18.
 */
class ChampionSpells {

    class FlameShield : OnDamagedSpell("Flame Shield", CastPhase.PASSIVE, 0) {
        override fun onDamaged(battle: Battle, attacker: Player, defender: Player, amount: Int, type: Battle.DamageType) = if (type == Battle.DamageType.PHYSICAL) "${defender.name}'s ${defender.champion.name} was attacked physically, activating its $name.\n\n" + attacker.takeDamage(battle, defender, 1, Battle.DamageType.MAGICAL) else ""
        override fun effect(battle: Battle, caster: Player, opponent: Player) = ""
    }

    class OrionCharge : Spell("Orion's Charge", CastPhase.PRIORITY, 0) {
        override fun effect(battle: Battle, caster: Player, opponent: Player): String {
            val difference = opponent.champion.stats.speed + 2 - caster.champion.stats.speed
            return if (difference > 0) caster.increaseStat(difference, Champion.StatType.SPEED, true) else "But ${caster.champion.name} already had more than enough speed."
        }
    }

    class RighteousSmite : OnAttackSpell("Righteous Smite", CastPhase.PASSIVE, 0) {
        override fun effect(battle: Battle, caster: Player, opponent: Player) = ""
        override fun onAttack(attacker: Player, defender: Player, battle: Battle) = if (defender.champion.race == Champion.Race.DEMON) "${defender.name}'s ${defender.champion.name} is a demon, activating ${attacker.name}'s ${attacker.champion.name}'s $name!\n\n" + attacker.increaseStat(2, Champion.StatType.ATTACK, false) + "\n\n" else ""
    }

    class TwinFang : Spell("Twin Fang", CastPhase.BEFORE_COMBAT, 0) {
        override fun effect(battle: Battle, caster: Player, opponent: Player): String {
            caster.champion.extraAttacks++
            return "${caster.name}'s ${caster.champion.name} will make an extra attack this round."
        }
    }

    class ChaosMagic : Spell("Chaos Magic", CastPhase.AFTER_COMBAT, 0) {
        override fun effect(battle: Battle, caster: Player, opponent: Player) = caster.increaseStat(1, Champion.StatType.values()[Random().nextInt(Champion.StatType.values().size)], true)
    }

    class ChannelLife : Spell("Channel Life", CastPhase.HIGH_PRIORITY, 0) {
        override fun effect(battle: Battle, caster: Player, opponent: Player) = if (caster.champion.health > 2) caster.takeDamage(battle, opponent, 2, Battle.DamageType.SELF_MAGICAL) + "\n\n" + caster.addMana(2) else "${caster.name}'s ${caster.champion.name} only had ${caster.champion.health} health, and couldn't use $name."
    }

    class IronDefense : Spell("Iron Defense", CastPhase.PRIORITY, 0){
        override fun effect(battle: Battle, caster: Player, opponent: Player) = caster.addShield(1, battle)
    }

    class Masochism : OnDamagedSpell("Masochism", CastPhase.PASSIVE, 0) {
        override fun onDamaged(battle: Battle, attacker: Player, defender: Player, amount: Int, type: Battle.DamageType) = if (type == Battle.DamageType.PHYSICAL) "${defender.name}'s ${defender.champion.name} was attacked physically, activating its $name.\n\n" + defender.increaseStat(1, Champion.StatType.ATTACK, true) else ""
        override fun effect(battle: Battle, caster: Player, opponent: Player) = ""
    }

    class ExperiencedBlocker : OnAddShieldSpell("Experienced Blocker", CastPhase.PASSIVE, 0){
        override fun effect(battle: Battle, caster: Player, opponent: Player) = ""

        override fun onAddShield(player: Player, battle: Battle, amount: Int): String {
            player.champion.shields++
            return "${player.champion.name} gained an extra shield with $name\n\n"
        }
    }

    class SpawnBrood : OnDamagedSpell("Spawn Brood", CastPhase.LAST, 0) {
        var extraAttacks = 0

        override fun onDamaged(battle: Battle, attacker: Player, defender: Player, amount: Int, type: Battle.DamageType) =
                if (extraAttacks > 0) {
                    extraAttacks = 0
                    "${defender.name}'s ${defender.champion.name} was hurt, and their brood of $extraAttacks all died.\n\n"
                } else ""


        override fun effect(battle: Battle, caster: Player, opponent: Player) =
                if (!caster.champion.wasHurt) {
                    caster.champion.extraAttacks += ++extraAttacks
                    "${caster.name}'s ${caster.champion.name} spawned a broodling, for a total of $extraAttacks extra brood attacks next round."
                } else "${caster.name}'s ${caster.champion.name} tried to cast $name, but was hurt this round, and couldn't."
    }

    class BarterSoul : Spell("Barter Soul", CastPhase.BEFORE_COMBAT, 0){
        override fun effect(battle: Battle, caster: Player, opponent: Player) = opponent.takeDamage(battle, caster, 4, Battle.DamageType.SELF_MAGICAL) + "\n\n" + opponent.increaseStat(2, Champion.StatType.ATTACK, false)
    }

    class GoHome : OnDamagedSpell("I Just Wanna Go Home", CastPhase.PASSIVE, 0){
        override fun effect(battle: Battle, caster: Player, opponent: Player) = ""

        override fun onDamaged(battle: Battle, attacker: Player, defender: Player, amount: Int, type: Battle.DamageType): String {
            return "${defender.name}'s ${defender.champion.name} just wants to go home already.\n\n" + defender.increaseStat(1, Champion.StatType.SPEED, true)
        }
    }

    class ProtagonistPower : Spell("Protagonist Power", CastPhase.PASSIVE, 0){
        override fun effect(battle: Battle, caster: Player, opponent: Player) = if(caster.champion.health <= ((caster.champion.stats.maxHealth.toDouble())/4.0)) "${caster.name}'s ${caster.champion.name} is at low health! $name activated!!!!\n\n" + caster.increaseStat(5, Champion.StatType.ATTACK, false) else ""
    }

    class EdgeFactor : Spell("Edge Factor", CastPhase.BEFORE_COMBAT, 0){
        override fun effect(battle: Battle, caster: Player, opponent: Player) = "${caster.takeDamage(battle, opponent, 2, Battle.DamageType.SELF_MAGICAL)}\n\n${caster.increaseStat(2, Champion.StatType.ATTACK, false)}"
    }

    class SeeFuture : Spell("See Future", CastPhase.BEFORE_COMBAT, 0){
        override fun effect(battle: Battle, caster: Player, opponent: Player): String {
            caster.champion.dodgeChance += 25
            return "${caster.name}'s ${caster.champion.name} may forsee an attack and dodge."
        }

    }
}
