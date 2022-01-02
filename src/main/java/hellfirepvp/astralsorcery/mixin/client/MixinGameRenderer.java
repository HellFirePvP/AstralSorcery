/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin.client;

import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.perk.node.key.KeyEntityReach;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.fml.LogicalSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MixinGameRenderer
 * Created by HellFirePvP
 * Date: 01.01.2022 / 09:52
 */
@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @ModifyConstant(method = "getMouseOver", constant = @Constant(doubleValue = 6.0, ordinal = 0), require = 1)
    public double getOverriddenCreativeEntityReach(double defaultExtendedReach) {
        PlayerProgress prog = ResearchHelper.getProgress(Minecraft.getInstance().player, LogicalSide.CLIENT);
        if (prog.isValid() && prog.getPerkData().hasPerkEffect(perk -> perk instanceof KeyEntityReach)) {
            return Minecraft.getInstance().playerController.getBlockReachDistance();
        }
        return defaultExtendedReach;
    }

    @ModifyConstant(method = "getMouseOver", constant = @Constant(intValue = 1, ordinal = 0), require = 1)
    public int adjustDistanceCheck(int flagDoDistanceCheck) {
        PlayerProgress prog = ResearchHelper.getProgress(Minecraft.getInstance().player, LogicalSide.CLIENT);
        if (prog.isValid() && prog.getPerkData().hasPerkEffect(perk -> perk instanceof KeyEntityReach)) {
            return 0;
        }
        return flagDoDistanceCheck;
    }

}
