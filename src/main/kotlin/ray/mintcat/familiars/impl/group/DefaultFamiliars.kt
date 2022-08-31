package ray.mintcat.familiars.impl.group

import eos.moe.armourers.api.DragonAPI
import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.type.AdyHuman
import io.lumine.xikage.mythicmobs.MythicMobs
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import ray.mintcat.familiars.getString
import ray.mintcat.familiars.impl.data.FamiliarsData
import taboolib.common.platform.Schedule
import taboolib.common.platform.function.submit
import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigFile
import taboolib.platform.util.hasLore
import taboolib.platform.util.isAir

object DefaultFamiliars : AbstractFamiliars {

    override val id: String = "default"

    @Config(value = "group/default.yml")
    lateinit var config: ConfigFile
        private set

    @Schedule(period = 5)
    fun update() {
        Bukkit.getOnlinePlayers().forEach {
            run(it)
        }
    }

    override fun create(livingEntity: LivingEntity): FamiliarsData? {
        if (get(livingEntity) != null) {
            return null
        }
        return FamiliarsData(
            id,
            livingEntity.uniqueId,
            build(livingEntity) ?: return null,
            1.5, 1.5,
            onUpdate = {
                val items = player?.inventory?.getItem(8)
                if (items == null || items.isAir) {
                    delete()
                    return@FamiliarsData
                }
                if (!items.hasLore("跟宠")) {
                    delete()
                    return@FamiliarsData
                }
                val mid = items.getString("mmi", "null")
                val mmi = MythicMobs.inst().itemManager.getItem(mid)
                if (mmi == null) {
                    delete()
                    return@FamiliarsData
                }
                val name = mmi.get().config.getString("Familiars.name", "跟宠")
                if ("[${player?.name}]$name" != entity.getCustomName()) {
                    delete()
                    return@FamiliarsData
                }
            }
        )
    }

    override fun build(livingEntity: LivingEntity): EntityInstance? {
        if (livingEntity !is Player) {
            return null
        }
        val items = livingEntity.inventory.getItem(8) ?: return null
        if (!items.hasLore("跟宠")) {
            return null
        }
        val mid = items.getString("mmi", "null")
        val mmi = MythicMobs.inst().itemManager.getItem(mid) ?: return null
        if (!mmi.isPresent) {
            return null
        }
        val name = mmi.get().config.getString("Familiars.name", "跟宠")
        val armor = mmi.get().config.getStringList("Familiars.armor")
        val disname = "[${livingEntity.name}]$name"
        val npc = AdyeshachAPI.getEntityManagerPublicTemporary().create(
            if (armor.isEmpty()) {
                EntityTypes.WOLF
            } else {
                EntityTypes.PLAYER
            },
            livingEntity.location
        ).apply {
            setCustomName(disname)
            setCustomNameVisible(true)
            if (this is AdyHuman) {
                this.setName(disname)
                submit(delay = 1) {
                    DragonAPI.setEntitySkin(this@apply.normalizeUniqueId, armor)
                }
            }
        }
        AdyeshachAPI.getEntityManagerPublicTemporary().getEntities().forEach {
            if ((it.getCustomName() == disname) && (it.uniqueId != npc.uniqueId)) {
                it.delete()
            }
        }
        return npc
    }
}