/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin.client;

import hellfirepvp.astralsorcery.client.helper.FocalPointEffectHelper;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: MixinMinecraft
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Shadow @Nullable public HitResult hitResult;
    @Unique private static HitResult astralSorcery_1_21$tempHitResult = null;

    @Inject(method = "startUseItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isHandsBusy()Z"))
    public void onStartUsingStellarFilament(CallbackInfo ci) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        if (player.getMainHandItem().is(ItemsAS.BLOCK_STELLAR_FILAMENT) || player.getOffhandItem().is(ItemsAS.BLOCK_STELLAR_FILAMENT)) {
            if (this.hitResult instanceof BlockHitResult bhr && this.hitResult.getType() == HitResult.Type.MISS) {
                if (FocalPointEffectHelper.isInFocalPointArea(bhr.getBlockPos())) {
                    astralSorcery_1_21$tempHitResult = this.hitResult;
                    this.hitResult = new BlockHitResult(bhr.getLocation(), bhr.getDirection(), bhr.getBlockPos(), bhr.isInside());
                }
            }
        }
    }

    @Inject(method = "startUseItem", at = @At("RETURN"))
    public void onUsingStellarFilamentCleanUp(CallbackInfo ci) {
        if (astralSorcery_1_21$tempHitResult != null) {
            this.hitResult = astralSorcery_1_21$tempHitResult;
            astralSorcery_1_21$tempHitResult = null;
        }
    }
}
