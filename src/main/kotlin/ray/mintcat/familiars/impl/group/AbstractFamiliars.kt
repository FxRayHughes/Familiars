package ray.mintcat.familiars.impl.group

import ink.ptms.adyeshach.core.entity.EntityInstance
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import ray.mintcat.familiars.Familiars
import ray.mintcat.familiars.impl.data.FamiliarsData

interface AbstractFamiliars {

    val id: String

    fun create(livingEntity: LivingEntity): FamiliarsData?

    fun run(livingEntity: LivingEntity) {
        val getter = if (get(livingEntity) == null) {
            register(livingEntity)
        } else {
            get(livingEntity)
        } ?: return
        if (livingEntity is Player && !livingEntity.isOnline) {
            getter.delete()
        }
        if (getter.delete) {
            Familiars.data.remove("${livingEntity.uniqueId}::${id}")
        } else {
            getter.update()
        }
    }

    fun get(livingEntity: LivingEntity): FamiliarsData? {
        return Familiars.getData(livingEntity.uniqueId, id)
    }

    fun build(livingEntity: LivingEntity): EntityInstance?

    fun register(livingEntity: LivingEntity): FamiliarsData? {
        val creates = create(livingEntity)
        if (creates != null) {
            Familiars.data["${livingEntity.uniqueId}::${id}"] = creates
            return creates
        }
        return null
    }
}