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
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.SidedHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AttributeTypeCriticalHitChance
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AttributeTypeCriticalHitChance extends PerkAttributeType {

    protected final Random rand = new Random();

    public AttributeTypeCriticalHitChance() {
        super(false);
    }

    @Override
    protected void attachListeners(IEventBus eventBus) {
        super.attachListeners(eventBus);
        eventBus.addListener(EventPriority.HIGH, this::onCritHit);
        eventBus.addListener(EventPriority.HIGH, this::onArrowSpawn);
    }

    private void onCritHit(CriticalHitEvent event) {
        if (event.isCriticalHit()) return;

        Player attacker = event.getEntity();
        LogicalSide side = SidedHelper.getSide(attacker);
        if (!this.hasTypeApplied(attacker, side)) return;

        PlayerProgress progress = ResearchManager.getProgress(attacker, side);
        if (!progress.isValid()) return;

        float critChance = PerkManager.getOrCreateAttributes(attacker)
                .modifyValue(attacker, progress, this, 0);
        critChance = AttributeEvent.postProcessModded(attacker, this, critChance);
        if (critChance >= this.rand.nextFloat()) {
            event.setCriticalHit(true);
        }
    }

    private void onArrowSpawn(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof Arrow arrow)) return;
        if (!(arrow.getOwner() instanceof Player player)) return;
        LogicalSide side = SidedHelper.getSide(player);
        if (!this.hasTypeApplied(player, side)) return;

        PlayerProgress progress = ResearchManager.getProgress(player, side);
        if (!progress.isValid()) return;

        float critChance = PerkManager.getOrCreateAttributes(player)
                .modifyValue(player, progress, this, 0);
        critChance = AttributeEvent.postProcessModded(player, this, critChance);
        if (critChance >= this.rand.nextFloat()) {
            arrow.setCritArrow(true);
        }
    }
}
