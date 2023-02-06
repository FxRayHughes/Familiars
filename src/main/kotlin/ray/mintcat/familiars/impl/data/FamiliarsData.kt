package ray.mintcat.familiars.impl.data

import ink.ptms.adyeshach.core.entity.EntityInstance
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

class FamiliarsData(
    val id: String,
    val owner: UUID,
    val entity: EntityInstance,
    val deviationX: Double,
    val deviationZ: Double,
    var speed: Double = 0.35,
    var delete: Boolean = false,
    var onUpdate: FamiliarsData.() -> Unit = {},
    var onDelete: FamiliarsData.() -> Unit = {},
) {

    init {
        entity.moveSpeed = speed
    }

    val player: Player?
        get() = Bukkit.getPlayer(owner)

    fun delete() {
        onDelete.invoke(this)
        entity.remove()
        delete = true
    }

    var lastLocation: Location = entity.getLocation().block.location

    var afk = 0
    fun update() {
        if (player == null || !player!!.isOnline) {
            delete()
            return
        }
        val pos = player!!.location.clone()
            .add(player!!.location.clone().direction.normalize().setY(0).multiply(deviationZ))
            .add(player!!.location.clone().apply { yaw += 90 }.direction.normalize().setY(0).multiply(deviationX))
        if (player!!.world != entity.world) {
            delete()
            return
        }
        if ((player!!.location.distance(entity.getLocation()) >= 10 && afk <= 20) && player!!.isOnGround) {
            entity.teleport(pos)
            return
        }
        if (player!!.location.block.location != lastLocation) {
            afk = 0
            entity.controllerMoveTo(pos)
            lastLocation = player!!.location.block.location
        } else {
            afk++
            if (afk >= 45) {
                val target = player?.getNearbyEntities(20.0, 20.0, 20.0)?.randomOrNull() ?: return
                entity.controllerMoveTo(target.location)
            }
        }
        onUpdate.invoke(this)
    }

}