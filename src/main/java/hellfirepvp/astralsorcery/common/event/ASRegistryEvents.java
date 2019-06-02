/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event;

import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTree;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.Event;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ASRegistryEvents
 * Created by HellFirePvP
 * Date: 01.06.2019 / 17:37
 */
public class ASRegistryEvents {

    /**
     * Use this event to disable certain perks
     * A note will be displayed on the perk's tooltip in case it's disabled by the pack.
     */
    public static class PerkDisable extends Event {

        private boolean perkDisabled;
        private final AbstractPerk perk;
        private final EntityPlayer player;
        private final Dist dist;

        public PerkDisable(AbstractPerk perk, EntityPlayer player, Dist dist) {
            this.perk = perk;
            this.player = player;
            this.dist = dist;
        }

        public AbstractPerk getPerk() {
            return perk;
        }

        public EntityPlayer getPlayer() {
            return player;
        }

        public Dist getSide() {
            return dist;
        }

        public boolean isPerkDisabled() {
            return perkDisabled;
        }

        public void setPerkDisabled(boolean perkDisabled) {
            this.perkDisabled = perkDisabled;
        }
    }

    /**
     * Use this event to remove perks at AS' post-init
     * Removed associated connections aswell
     */
    public static class PerkPostRemove extends Event {

        private boolean removed;
        private final AbstractPerk perk;

        public PerkPostRemove(AbstractPerk perk) {
            this.perk = perk;
        }

        public AbstractPerk getPerk() {
            return perk;
        }

        public boolean isRemoved() {
            return removed;
        }

        public void setRemoved(boolean removed) {
            this.removed = removed;
        }
    }

}
