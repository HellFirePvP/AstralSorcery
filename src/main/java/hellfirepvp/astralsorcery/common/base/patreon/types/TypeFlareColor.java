/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon.types;

import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffect;
import hellfirepvp.astralsorcery.common.base.patreon.entity.PatreonFlareDynamicColor;
import hellfirepvp.astralsorcery.common.base.patreon.entity.PatreonPartialEntity;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TypeFlareColor
 * Created by HellFirePvP
 * Date: 31.08.2019 / 11:16
 */
public class TypeFlareColor extends PatreonEffect {

    private final Supplier<Color> colorProvider;

    public TypeFlareColor(UUID uniqueId, Supplier<Color> colorProvider) {
        super(uniqueId, null);
        this.colorProvider = colorProvider;
    }

    public Supplier<Color> getColorProvider() {
        return colorProvider;
    }

    @Override
    public boolean hasPartialEntity() {
        return true;
    }

    @Nullable
    @Override
    public PatreonPartialEntity createEntity(UUID playerUUID) {
        return new PatreonFlareDynamicColor(getEffectUUID(), playerUUID);
    }
}
