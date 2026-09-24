/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.type;

import hellfirepvp.astralsorcery.common.event.AttributeEvent;
import hellfirepvp.astralsorcery.common.lib.PerksAS;
import hellfirepvp.astralsorcery.common.perk.PerkManager;
import hellfirepvp.astralsorcery.common.perk.type.base.PerkAttributeType;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.SidedHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AttributeTypeLifeRecovery
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AttributeTypeLifeRecovery extends PerkAttributeType {

    public AttributeTypeLifeRecovery() {
        super(true);
    }

    @Override
    protected void attachListeners(IEventBus eventBus) {
        super.attachListeners(eventBus);
        eventBus.addListener(EventPriority.LOW, this::onHeal);
    }

    private void onHeal(LivingHealEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        LogicalSide side = SidedHelper.getSide(player);
        if (!this.hasTypeApplied(player, side)) return;

        float heal = event.getAmount();
        heal = PerkManager.getOrCreateAttributes(player)
                .modifyValue(player, ResearchManager.getProgress(player, side), this, heal);
        heal = AttributeEvent.postProcessModded(player, this, heal);
        if (heal <= 0) {
            event.setCanceled(true);
        } else {
            event.setAmount(heal);
        }
    }
}
