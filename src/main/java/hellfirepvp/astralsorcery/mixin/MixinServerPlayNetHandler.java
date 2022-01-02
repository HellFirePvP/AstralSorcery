/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin;

import hellfirepvp.astralsorcery.common.entity.InteractableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.network.play.client.CUseEntityPacket;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MixinServerPlayNetHandler
 * Created by HellFirePvP
 * Date: 01.01.2022 / 09:52
 */
@Mixin(ServerPlayNetHandler.class)
public class MixinServerPlayNetHandler {

    @Shadow public ServerPlayerEntity player;

    //Due to compatibility, this will not be used. see reach_set_server_entity_interact.js
    /*@ModifyConstant(method = "processUseEntity", constant = @Constant(doubleValue = 36.0, ordinal = 1), require = 1)
    public double overrideEntityInteractDistanceLimit(double distance) {
        ServerPlayNetHandler playNetHandler = (ServerPlayNetHandler)(Object) this;
        PlayerEntity player = playNetHandler.player;

        PlayerProgress prog = ResearchHelper.getProgress(player, player.getEntityWorld().isRemote() ? LogicalSide.CLIENT : LogicalSide.SERVER);
        if (prog.isValid() && prog.getPerkData().hasPerkEffect(perk -> perk instanceof KeyEntityReach)) {
            return Double.MAX_VALUE;
        }
        return distance;
    }*/

    @Inject(
            method = "processUseEntity",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/play/ServerPlayNetHandler;disconnect(Lnet/minecraft/util/text/ITextComponent;)V"),
            cancellable = true
    )
    public void allowInteractableEntity(CUseEntityPacket packet, CallbackInfo ci) {
        ServerWorld world = this.player.getServerWorld();
        Entity interacted = packet.getEntityFromWorld(world);
        if (interacted instanceof InteractableEntity) {
            this.player.attackTargetEntityWithCurrentItem(interacted);
            ci.cancel();
        }
    }

}
