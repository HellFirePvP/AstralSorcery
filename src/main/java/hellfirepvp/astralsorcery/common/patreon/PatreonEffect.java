/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.patreon;

import hellfirepvp.astralsorcery.common.patreon.entity.PatreonPartialEntity;
import net.minecraft.util.RandomSource;
import net.neoforged.bus.api.IEventBus;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PatreonEffect
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PatreonEffect {

    protected final RandomSource rand = RandomSource.create();

    private final UUID effectUUID;

    public PatreonEffect(UUID effectUUID) {
        this.effectUUID = effectUUID;
    }

    public UUID getEffectUUID() {
        return this.effectUUID;
    }

    public void initialize() {}

    public void attachEventListeners(IEventBus bus) {}

    @Nullable
    public PatreonPartialEntity.Provider getPartialEntityProvider() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PatreonEffect that = (PatreonEffect) o;
        return Objects.equals(this.effectUUID, that.effectUUID);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.effectUUID);
    }
}
