/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.source;

import hellfirepvp.astralsorcery.client.effect.EffectProperties;
import hellfirepvp.astralsorcery.client.effect.context.base.BatchRenderContext;
import hellfirepvp.astralsorcery.client.effect.vfx.FXCube;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.constellation.world.DayTimeHelper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fluids.FluidStack;

import java.awt.*;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FXSourceLiquidFountain
 * Created by HellFirePvP
 * Date: 18.07.2019 / 20:44
 */
public class FXSourceLiquidFountain extends FXSource<FXCube, BatchRenderContext<FXCube>> {

    private final TextureAtlasSprite sprite;
    private final FluidStack fluid;

    public FXSourceLiquidFountain(Vector3 pos, FluidStack fluid) {
        super(pos, EffectTemplatesAS.CUBE_TRANSLUCENT_ATLAS);
        this.sprite = RenderingUtils.getParticleTexture(fluid);
        this.fluid = fluid;

        this.setMaxAge(40 + rand.nextInt(30));
    }

    @Override
    public void tickSpawnFX(Function<Vector3, FXCube> effectRegistrar) {
        Vector3 motion = Vector3.positiveYRandom();
        motion.setY(motion.getY() * 8).normalize().multiply(new Vector3(
                0.01F + rand.nextFloat() * 0.015F,
                0.1F + rand.nextFloat() * 0.015F,
                0.01F + rand.nextFloat() * 0.015F));

        effectRegistrar.apply(getPosition())
                .setTextureAtlasSprite(this.sprite)
                .setTextureSubSizePercentage(1F)
                .tumble()
                .setAlphaMultiplier(DayTimeHelper.getCurrentDaytimeDistribution(Minecraft.getInstance().world))
                .setScaleMultiplier(0.1F + rand.nextFloat() * 0.05F)
                .setMotion(motion)
                .color((fx, pTicks) -> new Color(fluid.getFluid().getAttributes().getColor(fluid)))
                .setGravityStrength(0.003F)
                .setMaxAge(40 + rand.nextInt(40));
    }

    @Override
    public void populateProperties(EffectProperties<FXCube> properties) {}
}
