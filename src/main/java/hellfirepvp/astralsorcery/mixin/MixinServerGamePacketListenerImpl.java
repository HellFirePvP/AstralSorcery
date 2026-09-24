/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin;

import hellfirepvp.astralsorcery.common.entity.ItemEntityChiselAttackable;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: MixinServerGamePacketListenerImpl
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
@Mixin(ServerGamePacketListenerImpl.class)
public class MixinServerGamePacketListenerImpl {

    @Shadow public ServerPlayer player;

    // TODO find a more stable solution that doesn't use redirect lol
    @Redirect(
            method = "handleInteract",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ServerboundInteractPacket;dispatch(Lnet/minecraft/network/protocol/game/ServerboundInteractPacket$Handler;)V")
    )
    public void allowInteractableEntityAttack(ServerboundInteractPacket instance, ServerboundInteractPacket.Handler decorated) {
        instance.dispatch(new ServerboundInteractPacket.Handler() {
            @Override
            public void onInteraction(InteractionHand hand) {
                decorated.onInteraction(hand);
            }

            @Override
            public void onInteraction(InteractionHand hand, Vec3 interactionLocation) {
                decorated.onInteraction(hand, interactionLocation);
            }

            @Override
            public void onAttack() {
                ServerLevel sLevel = MixinServerGamePacketListenerImpl.this.player.serverLevel();
                Entity interacted = instance.getTarget(sLevel);
                if (interacted instanceof ItemEntityChiselAttackable attackable) {
                    MixinServerGamePacketListenerImpl.this.player.attack(attackable);
                } else {
                    decorated.onAttack();
                }
            }
        });
    }

}
