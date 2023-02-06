package ray.mintcat.familiars.impl.group

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.manager.ManagerType
import ink.ptms.adyeshach.impl.entity.trait.impl.setTraitTitle
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import ray.mintcat.familiars.getString
import ray.mintcat.familiars.impl.data.FamiliarsData
import taboolib.common.platform.Schedule
import taboolib.module.chat.colored
import taboolib.platform.util.isAir

object DefaultFamiliars : AbstractFamiliars {

    override val id: String = "default"

    @Schedule(period = 5)
    fun update() {
        Bukkit.getOnlinePlayers().forEach {
            run(it)
        }
    }

    override fun create(livingEntity: LivingEntity): FamiliarsData? {
        if (get(livingEntity) != null) {
            return get(livingEntity)
        }
        return FamiliarsData(
            id,
            livingEntity.uniqueId,
            build(livingEntity) ?: return null,
            1.5, 2.0,
            onUpdate = {
                val items = player?.inventory?.getItem(8)
                if (items == null || items.isAir) {
                    delete()
                    return@FamiliarsData
                }
                if (items.getString("Familiars.name") == "null") {
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
        val name = items.getString("Familiars.name")
        if (name == "null") {
            return null
        }
        val type = items.getString("Familiars.type")
        val api = Adyeshach.api().getPublicEntityManager(ManagerType.TEMPORARY)
        val npc = api.create(EntityTypes.valueOf(type), livingEntity.location).apply {
            setCustomName(name.colored())
            setCustomNameVisible(true)
            setTraitTitle(listOf("[${livingEntity.name}]").colored())
        }
        return npc
    }
}