/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon;

import hellfirepvp.astralsorcery.common.base.patreon.entity.PatreonFlare;
import hellfirepvp.astralsorcery.common.base.patreon.entity.PatreonPartialEntity;
import hellfirepvp.observerlib.common.util.tick.ITickHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatreonEffect
 * Created by HellFirePvP
 * Date: 30.08.2019 / 17:54
 */
public class PatreonEffect {

    protected static final Random rand = new Random();

    private FlareColor flareColor;
    private UUID effectUUID;

    public PatreonEffect(UUID effectUUID, @Nullable FlareColor flareColor) {
        this.effectUUID = effectUUID;
        this.flareColor = flareColor;
    }

    @Nullable
    public FlareColor getFlareColor() {
        return flareColor;
    }

    public boolean hasPartialEntity() {
        return this.getFlareColor() != null;
    }

    public UUID getEffectUUID() {
        return effectUUID;
    }

    public void initialize() {}

    public void attachEventListeners(IEventBus bus) {}

    public void attachTickListeners(Consumer<ITickHandler> registrar) {}

    @OnlyIn(Dist.CLIENT)
    public void doClientEffect(PlayerEntity player) {}

    @Nullable
    public PatreonPartialEntity createEntity(UUID playerUUID) {
        if (this.hasPartialEntity()) {
            return new PatreonFlare(this.getEffectUUID(), playerUUID);
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PatreonEffect that = (PatreonEffect) o;
        return Objects.equals(effectUUID, that.effectUUID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(effectUUID);
    }
}
