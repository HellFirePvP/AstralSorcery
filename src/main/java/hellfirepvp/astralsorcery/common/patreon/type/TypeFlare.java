/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.patreon.type;

import hellfirepvp.astralsorcery.common.patreon.FlareColor;
import hellfirepvp.astralsorcery.common.patreon.PatreonEffect;
import hellfirepvp.astralsorcery.common.patreon.entity.PatreonPartialEntity;
import hellfirepvp.astralsorcery.common.patreon.entity.client.PatreonFlareClientEntity;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TypeFlare
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TypeFlare extends PatreonEffect {

    private final FlareColor flareColor;
    private final PatreonPartialEntity.Provider provider;

    public TypeFlare(UUID effectUUID, FlareColor flareColor) {
        super(effectUUID);
        this.flareColor = flareColor;
        this.provider = new PatreonPartialEntity.Provider(playerId -> {
            return new PatreonPartialEntity(this.getEffectUUID(), playerId);
        }, playerId -> {
            return new PatreonFlareClientEntity(this.getEffectUUID(), playerId);
        });
    }

    public FlareColor getFlareColor() {
        return this.flareColor;
    }

    @Nullable
    @Override
    public PatreonPartialEntity.Provider getPartialEntityProvider() {
        return this.provider;
    }
}
