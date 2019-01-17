/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon.entity;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingSprite;
import hellfirepvp.astralsorcery.client.util.resource.RowSpriteSheetResource;
import hellfirepvp.astralsorcery.client.util.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffectHelper;
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
 * Class: PartialEntityFlare
 * Created by HellFirePvP
 * Date: 23.06.2018 / 14:19
 */
public class PartialEntityFlare extends PatreonPartialEntity {

    private final PatreonEffectHelper.FlareColor flareColor;

    public Object clientSprite = null;

    public PartialEntityFlare(PatreonEffectHelper.FlareColor flareColor, UUID ownerUUID) {
        super(ownerUUID);
        this.flareColor = flareColor;
    }

    @SideOnly(Side.CLIENT)
    protected void spawnEffects() {
        super.spawnEffects();

        if (rand.nextBoolean() || !Config.enablePatreonEffects) return;

        int age = 30 + rand.nextInt(15);
        float scale = 0.1F + rand.nextFloat() * 0.1F;
        Vector3 at = new Vector3(this.pos);
        at.add(rand.nextFloat() * 0.08 * (rand.nextBoolean() ? 1 : -1),
                rand.nextFloat() * 0.08 * (rand.nextBoolean() ? 1 : -1),
                rand.nextFloat() * 0.08 * (rand.nextBoolean() ? 1 : -1));
        EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(at);
        particle.scale(scale).gravity(0.004).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
        particle.setColor(getColor());
        particle.setMaxAge(age);

        if (rand.nextBoolean()) {
            particle = EffectHelper.genericFlareParticle(at);
            particle.setColor(Color.WHITE).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
            particle.scale(scale * 0.3F).gravity(0.004);
            particle.setMaxAge(age - 10);
        }
    }

    @SideOnly(Side.CLIENT)
    protected Color getColor() {
        return rand.nextInt(3) == 0 ? this.flareColor.color2 : this.flareColor.color1;
    }

    @SideOnly(Side.CLIENT)
    protected SpriteSheetResource getSprite() {
        return RowSpriteSheetResource.crop(this.flareColor.getTexture(), this.flareColor.spriteRowIndex());
    }

    @SideOnly(Side.CLIENT)
    public void tickInRenderDistance() {
        super.tickInRenderDistance();

        if (clientSprite != null) {
            EntityFXFacingSprite p = (EntityFXFacingSprite) clientSprite;
            if(p.isRemoved() && Config.enablePatreonEffects) {
                EffectHandler.getInstance().registerFX(p);
            }
        } else {
            EntityFXFacingSprite p = EntityFXFacingSprite.fromSpriteSheet(getSprite(), pos.getX(), pos.getY(), pos.getZ(), 0.35F, 2);
            p.setPositionUpdateFunction((fx, v, m) -> this.getPos());
            p.setRefreshFunc(() -> !removed && Config.enablePatreonEffects);
            EffectHandler.getInstance().registerFX(p);
            this.clientSprite = p;
        }
    }

}
