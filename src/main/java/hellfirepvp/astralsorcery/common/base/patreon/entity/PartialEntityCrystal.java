/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon.entity;

import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.TextureQuery;
import hellfirepvp.astralsorcery.common.base.patreon.flare.PatreonPartialEntity;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PartialEntityCrystal
 * Created by HellFirePvP
 * Date: 14.07.2018 / 19:25
 */
public class PartialEntityCrystal extends PatreonPartialEntity {

    private TextureQuery queryTexture = new TextureQuery(AssetLoader.TextureLocation.MODELS, "crystal_big_blue");
    private Color colorTheme = Color.WHITE;
    private Object clientEffect = null;

    public PartialEntityCrystal(UUID ownerUUID) {
        super(ownerUUID);
    }

    public PartialEntityCrystal setQueryTexture(TextureQuery queryTexture) {
        this.queryTexture = queryTexture;
        return this;
    }

    public PartialEntityCrystal setColorTheme(Color colorTheme) {
        this.colorTheme = colorTheme;
        return this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected void spawnEffects() {
        super.spawnEffects();

        for (int i = 0; i < rand.nextInt(2) + 1; i++) {
            int age = 30 + rand.nextInt(15);
            float scale = 0.1F + rand.nextFloat() * 0.1F;
            Vector3 at = new Vector3(this.pos);
            at.add(rand.nextFloat() * 0.07 * (rand.nextBoolean() ? 1 : -1),
                    rand.nextFloat() * 0.07 * (rand.nextBoolean() ? 1 : -1),
                    rand.nextFloat() * 0.07 * (rand.nextBoolean() ? 1 : -1));
            at.addY(0.2F);
            Vector3 motion = Vector3.random().multiply(0.01F);
            EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(at);
            particle.scale(scale).gravity(0.004).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
            particle.motion(motion.getX(), motion.getY(), motion.getZ());
            particle.setColor(rand.nextInt(3) == 0 ? this.colorTheme.brighter() : this.colorTheme);
            particle.setMaxAge(age);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void tickInRenderDistance() {
        super.tickInRenderDistance();

        if (clientEffect != null) {
            EffectFloatingCrystal crystal = (EffectFloatingCrystal) clientEffect;
            if(crystal.isRemoved() && Config.enablePatreonEffects) {
                EffectHandler.getInstance().registerFX(crystal);
            }
        } else {
            EffectFloatingCrystal crystal = new EffectFloatingCrystal();
            crystal.setPositionUpdateFunction((fx, v, m) -> this.getPos());
            crystal.setRefreshFunc(() -> !removed && Config.enablePatreonEffects);
            crystal.setTexture(queryTexture);
            crystal.setColorTheme(colorTheme);
            EffectHandler.getInstance().registerFX(crystal);
            clientEffect = crystal;
        }
    }
}
