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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AttributeTypeBlockChance
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AttributeTypeBlockChance extends PerkAttributeType {

    protected final Random rand = new Random();

    public AttributeTypeBlockChance() {
        super(false);
    }

    @Override
    protected void attachListeners(IEventBus eventBus) {
        super.attachListeners(eventBus);
        eventBus.addListener(this::onBlockTest);
    }

    private void onBlockTest(LivingShieldBlockEvent event) {
        if (event.getBlocked()) return;
        if (event.getDamageSource().is(DamageTypeTags.BYPASSES_SHIELD)) return;
        if (event.getDamageSource().getDirectEntity() instanceof AbstractArrow arrow && arrow.getPierceLevel() > 0) return;
        if (event.getDamageSource().getSourcePosition() == null) return;

        if (!(event.getEntity() instanceof Player player)) return;
        LogicalSide side = SidedHelper.getSide(player);
        if (!this.hasTypeApplied(player, side)) return;

        PlayerProgress progress = ResearchManager.getProgress(player, side);
        if (!progress.isValid()) return;

        float blockChance = PerkManager.getOrCreateAttributes(player)
                .modifyValue(player, progress, this, 0F);
        blockChance = AttributeEvent.postProcessModded(player, this, blockChance);
        if (blockChance >= this.rand.nextFloat()) {
            event.setBlocked(true);
        }
    }
}
