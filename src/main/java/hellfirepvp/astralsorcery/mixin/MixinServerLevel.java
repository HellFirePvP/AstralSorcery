/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin;

import hellfirepvp.astralsorcery.common.perk.type.AttributeTypeMiningSize;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: MixinServerLevel
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
@Mixin(ServerLevel.class)
public class MixinServerLevel {

    @Inject(method = "destroyBlockProgress", at = @At("RETURN"))
    public void addMiningSizeDestroyProgress(int breakerId, BlockPos pos, int progress, CallbackInfo ci) {
        ServerLevel thisLevel = MiscUtil.cast(this);
        if (thisLevel.getEntity(breakerId) instanceof ServerPlayer sPlayer) {
            AttributeTypeMiningSize.sendBlockBreakProgressSync(thisLevel, sPlayer, pos, progress);
        }
    }
}
