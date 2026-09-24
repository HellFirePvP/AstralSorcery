/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin;

import hellfirepvp.astralsorcery.common.perk.tree.perk.key.KeyPerkAllToolTypes;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.SidedHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.LogicalSide;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: MixinInventory
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
@Mixin(Inventory.class)
public class MixinInventory {

    @Inject(method = "getDestroySpeed", at = @At("RETURN"), cancellable = true)
    public void addToolHarvestSpeed(BlockState state, CallbackInfoReturnable<Float> cir) {
        KeyPerkAllToolTypes.adjustHarvestSpeed(MiscUtil.cast(this), state, cir);
    }
}
