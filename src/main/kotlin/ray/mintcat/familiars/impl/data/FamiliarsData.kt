package ray.mintcat.familiars.impl.data

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.ai.general.GeneralGravity
import ink.ptms.adyeshach.common.entity.ai.general.GeneralMove
import ink.ptms.adyeshach.common.entity.path.PathType
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
    var pathType: PathType = PathType.WALK_1,
    var speed: Double = 0.5,
    var delete: Boolean = false,
    var onUpdate: FamiliarsData.() -> Unit = {},
    var onDelete: FamiliarsData.() -> Unit = {},
) {

    init {
        entity.registerController(GeneralMove(entity))
        entity.registerController(GeneralGravity(entity))
    }

    val player: Player?
        get() = Bukkit.getPlayer(owner)

    fun delete() {
        onDelete.invoke(this)
        entity.delete()
        delete = true
    }

    var lastLocation: Location = entity.getLocation().block.location

    fun update() {
        if (player == null || !player!!.isOnline) {
            delete()
            return
        }
        val pos = player!!.location.clone()
            .add(player!!.location.clone().direction.normalize().setY(0).multiply(deviationZ))
            .add(player!!.location.clone().apply { yaw += 90 }.direction.normalize().setY(0).multiply(deviationX))
        if (player!!.world != entity.getWorld()) {
            delete()
            return
        }
        if (player!!.location.distance(entity.getLocation()) >= 10) {
            entity.teleport(pos)
            return
        }
        if (player!!.location.block.location != lastLocation) {
            entity.controllerMove(pos, pathType = pathType, speed)
            lastLocation = player!!.location.block.location
        }
        onUpdate.invoke(this)
    }

}