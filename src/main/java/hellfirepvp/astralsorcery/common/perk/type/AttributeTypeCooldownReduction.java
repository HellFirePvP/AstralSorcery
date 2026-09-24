/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.type;

import hellfirepvp.astralsorcery.common.event.AttributeEvent;
import hellfirepvp.astralsorcery.common.event.ItemCooldownEvent;
import hellfirepvp.astralsorcery.common.perk.PerkManager;
import hellfirepvp.astralsorcery.common.perk.type.base.PerkAttributeType;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AttributeTypeCooldownReduction
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AttributeTypeCooldownReduction extends PerkAttributeType {

    public AttributeTypeCooldownReduction() {
        super(false);
    }

    @Override
    protected void attachListeners(IEventBus eventBus) {
        super.attachListeners(eventBus);
        eventBus.addListener(this::onItemCooldown);
    }

    private void onItemCooldown(ItemCooldownEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer sPlayer)) return;
        if (!this.hasTypeApplied(sPlayer, LogicalSide.SERVER)) return;

        float cooldown = event.getCooldown();
        cooldown = PerkManager.getOrCreateAttributes(sPlayer)
                .modifyValue(sPlayer, ResearchManager.getProgress(sPlayer, LogicalSide.SERVER), this, cooldown);
        cooldown = AttributeEvent.postProcessModded(sPlayer, this, cooldown);
        event.setCooldown(Math.max(1, Math.round(cooldown)));
    }
}
