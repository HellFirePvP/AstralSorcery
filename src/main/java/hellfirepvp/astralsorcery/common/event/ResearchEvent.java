/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event;

import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchFlag;
import hellfirepvp.astralsorcery.common.research.ResearchTier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ResearchEvent
 * Created by HellFirePvP
 * Date: 05.10.2026 / 13:36
 */
public class ResearchEvent extends PlayerEvent {

    private final ServerPlayer sPlayer;
    private final PlayerProgress progress;

    public ResearchEvent(ServerPlayer sPlayer, PlayerProgress progress) {
        super(sPlayer);
        this.sPlayer = sPlayer;
        this.progress = progress;
    }

    public ServerPlayer getPlayer() {
        return this.sPlayer;
    }

    public PlayerProgress getProgress() {
        return this.progress;
    }

    public static class MemorizeConstellation extends ResearchEvent {

        private final BaseConstellation constellation;

        public MemorizeConstellation(ServerPlayer sPlayer, PlayerProgress progress, BaseConstellation constellation) {
            super(sPlayer, progress);
            this.constellation = constellation;
        }

        public BaseConstellation getConstellation() {
            return this.constellation;
        }
    }

    public static class DiscoverConstellation extends ResearchEvent {

        private final BaseConstellation constellation;

        public DiscoverConstellation(ServerPlayer sPlayer, PlayerProgress progress, BaseConstellation constellation) {
            super(sPlayer, progress);
            this.constellation = constellation;
        }

        public BaseConstellation getConstellation() {
            return this.constellation;
        }
    }

    public static class MemorizedFocalPoint extends ResearchEvent {

        private final BaseConstellation constellation;

        public MemorizedFocalPoint(ServerPlayer sPlayer, PlayerProgress progress, BaseConstellation constellation) {
            super(sPlayer, progress);
            this.constellation = constellation;
        }

        public BaseConstellation getConstellation() {
            return this.constellation;
        }
    }

    public static class DiscoveredLumen extends ResearchEvent {

        private final Lumen lumen;

        public DiscoveredLumen(ServerPlayer sPlayer, PlayerProgress progress, Lumen lumen) {
            super(sPlayer, progress);
            this.lumen = lumen;
        }

        public Lumen getLumen() {
            return this.lumen;
        }
    }

    public static class ResearchTierSet extends ResearchEvent {

        private final ResearchTier previous, tier;

        public ResearchTierSet(ServerPlayer sPlayer, PlayerProgress progress, ResearchTier previous, ResearchTier tier) {
            super(sPlayer, progress);
            this.previous = previous;
            this.tier = tier;
        }

        public ResearchTier getPrevious() {
            return this.previous;
        }

        public ResearchTier getTier() {
            return this.tier;
        }
    }

    public static class ResearchFlagSet extends ResearchEvent {

        private final ResearchFlag flag;

        public ResearchFlagSet(ServerPlayer sPlayer, PlayerProgress progress, ResearchFlag flag) {
            super(sPlayer, progress);
            this.flag = flag;
        }

        public ResearchFlag getFlag() {
            return this.flag;
        }
    }

    public static class AttunedConstellationSet extends ResearchEvent {

        @Nullable
        private final BaseConstellation previous, attunedTo;

        public AttunedConstellationSet(ServerPlayer sPlayer, PlayerProgress progress, @Nullable BaseConstellation previous) {
            super(sPlayer, progress);
            this.previous = previous;
            this.attunedTo = progress.getAttunedConstellation().orElse(null);
        }

        @Nullable
        public BaseConstellation getPrevious() {
            return this.previous;
        }

        @Nullable
        public BaseConstellation getAttunedTo() {
            return this.attunedTo;
        }
    }
}
