/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event;

import hellfirepvp.astralsorcery.common.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.source.ModifierSourceProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;

import java.util.function.Consumer;

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
        private final PlayerEntity player;
        private final LogicalSide side;

        public PerkDisable(AbstractPerk perk, PlayerEntity player, LogicalSide side) {
            this.perk = perk;
            this.player = player;
            this.side = side;
        }

        public AbstractPerk getPerk() {
            return perk;
        }

        public PlayerEntity getPlayer() {
            return player;
        }

        public LogicalSide getSide() {
            return side;
        }

        public boolean isPerkDisabled() {
            return perkDisabled;
        }

        public void setPerkDisabled(boolean perkDisabled) {
            this.perkDisabled = perkDisabled;
        }
    }

    /**
     * Use this event to register a new modifier source provider to the automatic gathering.
     */
    public static class ModifierSourceRegister extends Event {

        private Consumer<ModifierSourceProvider<?>> registrar;

        public ModifierSourceRegister(Consumer<ModifierSourceProvider<?>> registrar) {
            this.registrar = registrar;
        }

        public void registerSource(ModifierSourceProvider<?> sourceProvider) {
            this.registrar.accept(sourceProvider);
        }
    }

}
