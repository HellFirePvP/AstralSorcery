/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon.types;

import hellfirepvp.astralsorcery.client.resource.query.TextureQuery;
import hellfirepvp.astralsorcery.common.base.patreon.FlareColor;
import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffect;
import hellfirepvp.astralsorcery.common.base.patreon.entity.PatreonCrystalFlare;
import hellfirepvp.astralsorcery.common.base.patreon.entity.PatreonPartialEntity;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TypeFlareCrystal
 * Created by HellFirePvP
 * Date: 05.04.2020 / 16:46
 */
public class TypeFlareCrystal extends PatreonEffect {

    private final Color colorTheme;
    private final TextureQuery crystalTexture;

    public TypeFlareCrystal(UUID effectUUID, @Nullable FlareColor flareColor, Color colorTheme, TextureQuery crystalTexture) {
        super(effectUUID, flareColor);
        this.colorTheme = colorTheme;
        this.crystalTexture = crystalTexture;
    }

    @Nullable
    @Override
    public PatreonPartialEntity createEntity(UUID playerUUID) {
        //TODO fix layering flare before crystal
        return new PatreonCrystalFlare(this.getEffectUUID(), playerUUID)
                .setQueryTexture(this.crystalTexture)
                .setColorTheme(this.colorTheme);
    }
}
