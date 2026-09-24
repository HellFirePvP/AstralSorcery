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
import hellfirepvp.astralsorcery.common.util.VectorUtil;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AttributeTypeProjectileSpeed
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AttributeTypeProjectileSpeed extends PerkAttributeType {

    public AttributeTypeProjectileSpeed() {
        super(true);
    }

    @Override
    protected void attachListeners(IEventBus eventBus) {
        super.attachListeners(eventBus);
        eventBus.addListener(this::onProjectileSpawn);
    }

    private void onProjectileSpawn(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof Projectile projectile)) return;
        if (!(projectile.getOwner() instanceof Player player)) return;
        LogicalSide side = SidedHelper.getSide(player);
        if (!this.hasTypeApplied(player, side)) return;

        PlayerProgress progress = ResearchManager.getProgress(player, side);
        if (!progress.isValid()) return;


        float speedMultiplier = PerkManager.getOrCreateAttributes(player)
                .modifyValue(player, progress, this, 1F);
        speedMultiplier = AttributeEvent.postProcessModded(player, this, speedMultiplier);

        Vector3 motion = new Vector3(projectile.getDeltaMovement());
        motion = VectorUtil.limitVelocityToMinecraftLimit(motion.multiply(speedMultiplier));

        projectile.setDeltaMovement(motion.toVector3d());
    }
}
