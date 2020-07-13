/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon.entity;

import hellfirepvp.astralsorcery.client.data.config.entry.RenderingConfig;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.vfx.FXFacingSprite;
import hellfirepvp.astralsorcery.client.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.resource.query.SpriteQuery;
import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffect;
import hellfirepvp.astralsorcery.common.base.patreon.types.TypeFlareColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatreonFlareDynamicColor
 * Created by HellFirePvP
 * Date: 31.08.2019 / 11:18
 */
public class PatreonFlareDynamicColor extends PatreonFlare {

    public PatreonFlareDynamicColor(UUID effectUUID, UUID ownerUUID) {
        super(effectUUID, ownerUUID);
    }

    @Nullable
    @Override
    protected SpriteQuery getSpriteQuery() {
        return new SpriteQuery(AssetLoader.TextureLocation.EFFECT, 1, 48, "patreonflares", "gray_mono");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected Color getColor() {
        PatreonEffect effect = getEffect();
        if (!(effect instanceof TypeFlareColor)) {
            return Color.WHITE;
        }

        Color color = ((TypeFlareColor) effect).getColorProvider().get();
        return rand.nextInt(3) == 0 ? color : color.brighter();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void tickClient() {
        if (clientSprite != null) {
            FXFacingSprite p = (FXFacingSprite) clientSprite;
            if (!p.isRemoved() && RenderingConfig.CONFIG.patreonEffects.get()) {
                PatreonEffect effect = getEffect();
                if (effect instanceof TypeFlareColor) {
                    p.color(VFXColorFunction.constant(((TypeFlareColor) effect).getColorProvider().get()));
                }
            }
        }

        super.tickClient();
    }
}
