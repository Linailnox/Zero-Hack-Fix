@file:Suppress("UNUSED")

package cum.xiaomao.zerohack.util

import cum.xiaomao.zerohack.event.SafeClientEvent
import net.minecraft.network.play.client.CPacketAnimation
import net.minecraft.util.EnumHand

enum class SwingMode {
    CLIENT {
        override fun swingHand(event: SafeClientEvent, hand: EnumHand) {
            event.player.swingArm(hand)
        }
    },
    PACKET {
        override fun swingHand(event: SafeClientEvent, hand: EnumHand) {
            event.connection.sendPacket(CPacketAnimation(hand))
        }
    };

    abstract fun swingHand(event: SafeClientEvent, hand: EnumHand)
}
