/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon.entity;

import hellfirepvp.astralsorcery.client.data.config.entry.RenderingConfig;
import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXPositionController;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.vfx.FXFacingSprite;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.resource.query.SpriteQuery;
import hellfirepvp.astralsorcery.common.base.patreon.FlareColor;
import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffect;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.world.IWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatreonFlare
 * Created by HellFirePvP
 * Date: 30.08.2019 / 18:15
 */
public class PatreonFlare extends PatreonPartialEntity {

    public Object clientSprite = null;

    public PatreonFlare(UUID effectUUID, UUID ownerUUID) {
        super(effectUUID, ownerUUID);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void tickClient() {
        super.tickClient();

        if (this.clientSprite != null) {
            FXFacingSprite sprite = (FXFacingSprite) clientSprite;
            if (sprite.isRemoved() && RenderingConfig.CONFIG.patreonEffects.get()) {
                this.clientSprite = null;
            }
        }

        if (this.clientSprite == null) {
            SpriteQuery sprite = getSpriteQuery();
            if (sprite != null) {
                this.clientSprite = EffectHelper.of(EffectTemplatesAS.FACING_SPRITE)
                        .spawn(pos)
                        .setSprite(sprite.resolveSprite())
                        .setScaleMultiplier(0.35F)
                        .position(new VFXPositionController<EntityVisualFX>() {
                            @Nonnull
                            @Override
                            public Vector3 updatePosition(@Nonnull EntityVisualFX fx, @Nonnull Vector3 position, @Nonnull Vector3 motionToBeMoved) {
                                return PatreonFlare.this.pos.clone();
                            }
                        })
                        .refresh(fx -> !PatreonFlare.this.removed && RenderingConfig.CONFIG.patreonEffects.get());
            }
        }
    }

    @Nullable
    protected SpriteQuery getSpriteQuery() {
        FlareColor color = getFlareColor();
        if (color != null) {
            return color.getSpriteQuery();
        }
        return null;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void tickEffects(IWorld world) {
        super.tickEffects(world);

        if (!RenderingConfig.CONFIG.patreonEffects.get() || rand.nextBoolean()) {
            return;
        }
        Color c = this.getColor();
        if (c == null) {
            return;
        }

        int age = 30 + rand.nextInt(15);
        float scale = 0.1F + rand.nextFloat() * 0.1F;
        Vector3 at = new Vector3(this.pos);
        at.add(rand.nextFloat() * 0.08 * (rand.nextBoolean() ? 1 : -1),
                rand.nextFloat() * 0.08 * (rand.nextBoolean() ? 1 : -1),
                rand.nextFloat() * 0.08 * (rand.nextBoolean() ? 1 : -1));

        EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                .spawn(at)
                .alpha(VFXAlphaFunction.FADE_OUT)
                .setScaleMultiplier(scale)
                .color(VFXColorFunction.constant(c))
                .setMaxAge(age);

        if (rand.nextBoolean()) {
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(at)
                    .alpha(VFXAlphaFunction.FADE_OUT)
                    .setScaleMultiplier(scale * 0.3F)
                    .setMaxAge(age - 10);
        }
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    protected Color getColor() {
        FlareColor col = this.getFlareColor();
        if (col == null) {
            return null;
        }
        return rand.nextInt(3) == 0 ? col.color2 : col.color1;
    }

    @Nullable
    private FlareColor getFlareColor() {
        PatreonEffect effect = this.getEffect();
        if (effect != null) {
            return effect.getFlareColor();
        }
        return null;
    }
}
