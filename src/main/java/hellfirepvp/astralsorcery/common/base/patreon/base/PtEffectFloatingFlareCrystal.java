/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon.base;

import hellfirepvp.astralsorcery.client.util.resource.TextureQuery;
import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffectHelper;
import hellfirepvp.astralsorcery.common.base.patreon.entity.PartialEntityFlareCrystal;
import hellfirepvp.astralsorcery.common.base.patreon.flare.PatreonPartialEntity;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PtEffectFloatingFlareCrystal
 * Created by HellFirePvP
 * Date: 27.12.2018 / 01:29
 */
public class PtEffectFloatingFlareCrystal extends PatreonEffectHelper.PatreonEffect {

    private Color colorTheme;
    private PatreonEffectHelper.FlareColor flareColor;
    private TextureQuery crystalTexture;

    public PtEffectFloatingFlareCrystal(UUID uniqueId, Color colorTheme,
                                        PatreonEffectHelper.FlareColor flareColor,
                                        TextureQuery crystalTexture) {
        super(uniqueId, null);
        this.colorTheme = colorTheme;
        this.flareColor = flareColor;
        this.crystalTexture = crystalTexture;
    }

    @Override
    public boolean hasPartialEntity() {
        return true;
    }

    @Nullable
    @Override
    public PatreonPartialEntity createEntity(UUID playerUUID) {
        return new PartialEntityFlareCrystal(this.flareColor, playerUUID)
                .setQueryTexture(this.crystalTexture)
                .setColorTheme(this.colorTheme);
    }

}
