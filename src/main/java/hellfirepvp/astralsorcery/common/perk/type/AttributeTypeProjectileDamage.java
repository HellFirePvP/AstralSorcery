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
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AttributeTypeProjectileDamage
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AttributeTypeProjectileDamage extends PerkAttributeType {

    public AttributeTypeProjectileDamage() {
        super(false);
    }

    @Override
    protected void attachListeners(IEventBus eventBus) {
        super.attachListeners(eventBus);
        eventBus.addListener(this::onProjectileHurt);
    }

    private void onProjectileHurt(LivingIncomingDamageEvent event) {
        if (event.getSource().is(DamageTypeTags.IS_PROJECTILE) && event.getSource().getEntity() instanceof Player player) {
            LogicalSide side = SidedHelper.getSide(player);
            if (!this.hasTypeApplied(player, side)) return;

            PlayerProgress progress = ResearchManager.getProgress(player, side);
            if (!progress.isValid()) return;

            float dmg = PerkManager.getOrCreateAttributes(player)
                    .modifyValue(player, progress, this, event.getAmount());
            dmg = AttributeEvent.postProcessModded(player, this, dmg);
            event.setAmount(dmg);
        }
    }
}
