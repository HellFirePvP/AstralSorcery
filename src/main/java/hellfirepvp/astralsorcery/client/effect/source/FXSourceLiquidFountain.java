/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
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
import hellfirepvp.astralsorcery.common.util.data.Vector3;
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
    }

    @Override
    public void tickSpawnFX(Function<Vector3, FXCube> effectRegistrar) {
        Vector3 motion = Vector3.positiveYRandom();
        motion.setY(motion.getY() * 8).normalize().multiply(new Vector3(
                0.01F + rand.nextFloat() * 0.015F,
                0.09F + rand.nextFloat() * 0.015F,
                0.01F + rand.nextFloat() * 0.015F));

        FXCube cube = effectRegistrar.apply(getPosition())
                .setTextureAtlasSprite(this.sprite)
                .setTextureSubSizePercentage(1F / 16F)
                .tumble();
        cube.setScaleMultiplier(0.1F);
        cube.setMaxAge(40 + rand.nextInt(40));
        cube.setMotion(motion);
        cube.color((fx, pTicks) -> new Color(fluid.getFluid().getAttributes().getColor(fluid)));
        cube.motion((fx, vec) -> vec.setY(vec.getY() - 0.003F));
    }

    @Override
    public void populateProperties(EffectProperties<FXCube> properties) {}
}
