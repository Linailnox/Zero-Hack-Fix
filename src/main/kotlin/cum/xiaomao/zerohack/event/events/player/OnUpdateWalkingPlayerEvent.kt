package cum.xiaomao.zerohack.event.events.player

import cum.xiaomao.zerohack.event.Cancellable
import cum.xiaomao.zerohack.event.Event
import cum.xiaomao.zerohack.event.EventBus
import cum.xiaomao.zerohack.event.EventPosting
import cum.xiaomao.zerohack.manager.managers.PlayerPacketManager
import cum.xiaomao.zerohack.util.math.vector.Vec2f
import net.minecraft.util.math.Vec3d

sealed class OnUpdateWalkingPlayerEvent(
    position: Vec3d,
    rotation: Vec2f,
    onGround: Boolean
) : Cancellable(), Event {

    var position = position; private set
    var rotation = rotation; private set

    var onGround = onGround
        @JvmName("isOnGround") get
        private set

    var cancelMove = false; private set
    var cancelRotate = false; private set
    var cancelAll = false; private set

    val rotationX: Float
        get() = rotation.x

    val rotationY: Float
        get() = rotation.y

    fun apply(packet: PlayerPacketManager.Packet) {
        cancel()

        packet.position?.let {
            this.position = it
        }
        packet.rotation?.let {
            this.rotation = it
        }
        packet.onGround?.let {
            this.onGround = it
        }

        this.cancelMove = packet.cancelMove
        this.cancelRotate = packet.cancelRotate
        this.cancelAll = packet.cancelAll
    }

    class Pre(position: Vec3d, rotation: Vec2f, onGround: Boolean) : OnUpdateWalkingPlayerEvent(position, rotation, onGround), EventPosting by Companion {
        constructor(position: Vec3d, rotationX: Float, rotationY: Float, onGround: Boolean) : this(position, Vec2f(rotationX, rotationY), onGround)

        companion object : EventBus()
    }

    class Post(position: Vec3d, rotation: Vec2f, onGround: Boolean) : OnUpdateWalkingPlayerEvent(position, rotation, onGround), EventPosting by Companion {
        constructor(position: Vec3d, rotationX: Float, rotationY: Float, onGround: Boolean) : this(position, Vec2f(rotationX, rotationY), onGround)

        companion object : EventBus()
    }
}