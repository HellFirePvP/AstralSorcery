/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon.entity;

import hellfirepvp.astralsorcery.client.data.config.entry.RenderingConfig;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.vfx.FXCrystal;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.resource.query.TextureQuery;

import java.awt.*;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatreonCrystalFlare
 * Created by HellFirePvP
 * Date: 05.04.2020 / 16:53
 */
public class PatreonCrystalFlare extends PatreonFlare {

    private TextureQuery queryTexture = new TextureQuery(AssetLoader.TextureLocation.MODEL, "crystal_blue");
    private Color colorTheme = Color.WHITE;

    private Object crystalEffect = null;

    public PatreonCrystalFlare(UUID effectUUID, UUID ownerUUID) {
        super(effectUUID, ownerUUID);
    }

    public PatreonCrystalFlare setQueryTexture(TextureQuery queryTexture) {
        this.queryTexture = queryTexture;
        return this;
    }

    public PatreonCrystalFlare setColorTheme(Color colorTheme) {
        this.colorTheme = colorTheme;
        return this;
    }

    @Override
    public void tickClient() {
        super.tickClient();

        if (this.crystalEffect != null) {
            FXCrystal crystal = (FXCrystal) this.crystalEffect;
            if (crystal.isRemoved() && RenderingConfig.CONFIG.patreonEffects.get()) {
                EffectHelper.refresh(crystal, EffectTemplatesAS.CRYSTAL);
            }
        } else {
            this.crystalEffect = EffectHelper.of(EffectTemplatesAS.CRYSTAL)
                    .spawn(getPos())
                    .setTexture(this.queryTexture)
                    .setLightRayColor(this.colorTheme)
                    .setScaleMultiplier(0.03F)
                    .position((fx, position, motionToBeMoved) -> this.getPos().clone())
                    .refresh((fx) -> !this.removed && RenderingConfig.CONFIG.patreonEffects.get());
        }
    }
}
