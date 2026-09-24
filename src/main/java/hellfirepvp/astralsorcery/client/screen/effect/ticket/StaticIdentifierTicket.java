/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.effect.ticket;

import hellfirepvp.astralsorcery.client.screen.effect.ScreenEffectContainer;
import hellfirepvp.astralsorcery.client.screen.effect.ScreenEffectTicket;
import net.minecraft.util.RandomSource;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: StaticIdentifierTicket
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class StaticIdentifierTicket extends ScreenEffectTicket<StaticIdentifierTicket.Container, StaticIdentifierTicket> {

    private final UUID uuid;

    public static final StaticIdentifierTicket ASTROLABE_OVERLAY = new StaticIdentifierTicket(UUID.fromString("9ad05e6e-064c-440e-8225-6e6b45169f85"));
    public static final StaticIdentifierTicket TOME_LUMEN_OVERVIEW = new StaticIdentifierTicket(UUID.fromString("e074eff3-68a5-47ec-b24f-244bfcaf1748"));

    private StaticIdentifierTicket(UUID uuid) {
        this.uuid = uuid;
    }

    public static StaticIdentifierTicket ofIdentifier(ResourceLocation id) {
        long seed = (long) id.getNamespace().hashCode() << 32 | (id.getPath().hashCode() & 0xFFFFFFFFL);
        RandomSource random = RandomSource.create(seed);
        return new StaticIdentifierTicket(new UUID(random.nextLong(), random.nextLong()));
    }

    @Override
    public Container createEffectContainer() {
        return new Container(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StaticIdentifierTicket that = (StaticIdentifierTicket) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uuid);
    }

    public static class Container extends ScreenEffectContainer<StaticIdentifierTicket.Container, StaticIdentifierTicket> {

        public Container(StaticIdentifierTicket ticket) {
            super(ticket);
        }
    }
}
