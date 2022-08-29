package ray.mintcat.familiars.impl.group

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.type.AdyCat
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.LivingEntity
import ray.mintcat.familiars.impl.data.FamiliarsData
import ray.mintcat.familiars.utils.Cooldown
import taboolib.common.platform.Schedule
import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigFile

object DefaultFamiliars : AbstractFamiliars {

    override val id: String = "default"

    @Config(value = "group/default.yml")
    lateinit var config: ConfigFile
        private set

    @Schedule(period = 10, async = true)
    fun update() {
        Bukkit.getOnlinePlayers().forEach {
            run(it)
        }
    }

    override fun create(livingEntity: LivingEntity): FamiliarsData {
        return FamiliarsData(
            id,
            livingEntity.uniqueId,
            build(livingEntity),
            1.5, 1.5,
            onUpdate = {
                if (Cooldown.check("${livingEntity}::${id}::狗叫", (20..180).random().toLong())) {
                    player?.playSound(entity.getLocation(), Sound.ENTITY_WOLF_AMBIENT, 1f, 1f)
                }
            }
        )
    }

    override fun build(livingEntity: LivingEntity): EntityInstance {
        return AdyeshachAPI.getEntityManagerPublicTemporary().create(
            EntityTypes.WOLF,
            livingEntity.location
        ).apply {
            if (this is AdyCat) {
                setCustomName("六郎")
                setCustomNameVisible(true)
            }
        }
    }
}