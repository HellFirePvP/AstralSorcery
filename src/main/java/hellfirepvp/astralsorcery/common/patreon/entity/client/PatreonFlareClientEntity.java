/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.patreon.entity.client;

import hellfirepvp.astralsorcery.client.config.RenderingConfig;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.function.*;
import hellfirepvp.astralsorcery.client.effect.vfx.VFXFacingParticle;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.resource.query.SpriteSheetQuery;
import hellfirepvp.astralsorcery.common.patreon.FlareColor;
import hellfirepvp.astralsorcery.common.patreon.PatreonEffect;
import hellfirepvp.astralsorcery.common.patreon.type.TypeFlare;
import hellfirepvp.astralsorcery.common.util.ClientObject;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PatreonFlareClientEntity
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PatreonFlareClientEntity extends PatreonPartialClientEntity {

    private final ClientObject<VFXFacingParticle> flareParticle = new ClientObject<>();

    public PatreonFlareClientEntity(UUID effectUUID, UUID ownerUUID) {
        super(effectUUID, ownerUUID);
    }

    @Override
    public void tickEffects(Level level) {
        super.tickEffects(level);

        if (!this.flareParticle.isNull() && this.flareParticle.get().isRemoved()) {
             this.flareParticle.set(null);
        }

        if (this.flareParticle.isNull()) {
            this.getSprite().ifPresent(spriteQuery -> {
                VFXFacingParticle particle = EffectHelper.of(EffectTemplatesAS.IMMEDIATE_FACING_SPRITE)
                        .spawn(this.getPosition())
                        .setSpriteSheet(spriteQuery.resolveSprite())
                        .setScale(0.5F)
                        .renderOffset((fx, renderPosition, pTicks) -> PatreonFlareClientEntity.this.getInterpolatedPosition(pTicks))
                        .position((fx, position, motionToBeMoved) -> PatreonFlareClientEntity.this.getPosition())
                        .refresh(fx -> !PatreonFlareClientEntity.this.isRemoved() && RenderingConfig.CONFIG.patreonEffects.get());
                this.flareParticle.set(particle);
            });
        }

        this.getColor().ifPresent(color -> {
            int age = 30 + this.rand.nextInt(15);
            float scale = 0.15F + this.rand.nextFloat() * 0.1F;
            Vector3 pos = this.getPosition().add(Vector3.random(this.rand).multiply(0.08F));

            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(pos)
                    .setScale(scale)
                    .alpha(FXAlphaFunction.FADE_OUT)
                    .color(FXColorFunction.constant(color))
                    .setGravity(Vector3.y(0.00035F))
                    .setMaxAge(age);

            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(pos)
                    .setScale(scale * 0.3F)
                    .alpha(FXAlphaFunction.FADE_OUT)
                    .setGravity(Vector3.y(0.00035F))
                    .setMaxAge(age - 5);
        });
    }

    protected Optional<SpriteSheetQuery> getSprite() {
        return this.resolveFlareColor().map(FlareColor::getQuery);
    }

    protected Optional<ColorWrapper> getColor() {
        return this.resolveFlareColor().map(flareColor -> {
            if (flareColor == FlareColor.RAINBOW) {
                return ColorWrapper.ofHSB(this.rand.nextFloat(), 1F, 1F);
            }
            return this.rand.nextInt(3) == 0 ? flareColor.color1 : flareColor.color2;
        });
    }

    protected Optional<FlareColor> resolveFlareColor() {
        PatreonEffect effect = this.getEffect();
        if (effect instanceof TypeFlare flareEffect) {
            return Optional.of(flareEffect.getFlareColor());
        }
        return Optional.empty();
    }
}
