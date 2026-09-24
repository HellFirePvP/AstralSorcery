/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.source.orbital;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EffectTemplate;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXMotionFunction;
import hellfirepvp.astralsorcery.client.effect.source.FXOrbitalSource;
import hellfirepvp.astralsorcery.client.effect.vfx.VFXAtlasSpriteParticle;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.util.RenderSpriteUtil;
import hellfirepvp.astralsorcery.common.util.VectorUtil;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.function.BiConsumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FXInfuserOrbitalSource
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class FXInfuserOrbitalSource extends FXOrbitalSource {

    private final FluidStack fluidStack;
    private final Vector3 infuserPos;

    public FXInfuserOrbitalSource(Vector3 pos, FluidStack fluidStack) {
        super(pos);
        this.fluidStack = fluidStack;
        this.infuserPos = pos.copy().addY(1F);
    }

    @Override
    public void spawnOrbitalParticle(Vector3 pos) {
        Vector3 motion = this.getPos().subtract(pos).crossProduct(this.getOrbitAxis()).normalize().multiply(0.2 + rand.nextFloat() * 0.04);
        motion.add(this.getOrbitAxis().normalize().multiply(0.2 + rand.nextFloat() * 0.05));

        pos = VectorUtil.withRandomOffset(pos, rand, 0.15F);

        if (rand.nextInt(4) != 0) {
            EffectHelper.of(EffectTemplatesAS.BLOCK_PARTICLE)
                    .spawn(pos)
                    .setSprite(RenderSpriteUtil.getTexture(this.fluidStack))
                    .setSpriteFraction(0.25F)
                    .setScale(0.03F)
                    .color((fx, pTicks) -> RenderSpriteUtil.getColorOverlay(this.fluidStack))
                    .alpha(FXAlphaFunction.proximity(() -> this.infuserPos, 2F))
                    .motion(FXMotionFunction.target(this.infuserPos::copy, 0.08F))
                    .setMotion(motion);
        } else {
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(pos)
                    .setScale(0.15F)
                    .setAlpha(1F)
                    .alpha(FXAlphaFunction.proximity(() -> this.infuserPos, 2F))
                    .motion(FXMotionFunction.target(this.infuserPos::copy, 0.08F))
                    .setMotion(motion);
        }
    }
}
