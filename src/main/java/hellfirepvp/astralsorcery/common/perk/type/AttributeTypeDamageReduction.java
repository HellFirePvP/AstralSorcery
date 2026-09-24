/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.type;

import hellfirepvp.astralsorcery.common.event.AttributeEvent;
import hellfirepvp.astralsorcery.common.perk.PerkManager;
import hellfirepvp.astralsorcery.common.perk.type.base.PerkAttributeType;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.SidedHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AttributeTypeDamageReduction
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AttributeTypeDamageReduction extends PerkAttributeType {

    public AttributeTypeDamageReduction() {
        super(true);
    }

    @Override
    protected void attachListeners(IEventBus eventBus) {
        super.attachListeners(eventBus);
        eventBus.addListener(this::onDamage);
    }

    private void onDamage(LivingDamageEvent.Pre event) {
        if (event.getEntity() instanceof ServerPlayer sPlayer) {
            LogicalSide side = SidedHelper.getSide(sPlayer);
            if (!this.hasTypeApplied(sPlayer, side)) return;

            PlayerProgress progress = ResearchManager.getProgress(sPlayer, side);
            if (!progress.isValid()) return;

            float reduction = PerkManager.getOrCreateAttributes(sPlayer)
                    .getModifier(sPlayer, progress, this);
            reduction = AttributeEvent.postProcessModded(sPlayer, this, reduction);

            reduction = Mth.clamp(1F - reduction, 0F, 1F);
            event.setNewDamage(event.getNewDamage() * reduction);
        }
    }
}
