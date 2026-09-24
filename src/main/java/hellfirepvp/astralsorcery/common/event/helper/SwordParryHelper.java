/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.helper;

import hellfirepvp.astralsorcery.common.item.tool.IridescentCrystalSwordItem;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.util.CelestialStrike;
import hellfirepvp.astralsorcery.common.util.ServerSoundHelper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.tick.TimeoutList;
import hellfirepvp.astralsorcery.common.visual.type.LightningEffect;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;

import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: SwordParryHelper
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class SwordParryHelper {

    private static final int EMPOWERED_TIMEOUT = 100;

    private static final TimeoutList<UUID> empoweredCharges = new TimeoutList<>();

    private SwordParryHelper() {}

    public static void attachListeners(IEventBus bus) {
        bus.addListener(empoweredCharges::onServerTick);
        bus.addListener(SwordParryHelper::onProjectileImpact);
    }

    public static void clearServer() {
        empoweredCharges.clear();
    }

    public static boolean consumeEmpoweredCharge(Player player) {
        return empoweredCharges.remove(player.getUUID());
    }

    private static void onProjectileImpact(ProjectileImpactEvent event) {
        Projectile projectile = event.getProjectile();
        if (!(projectile.level() instanceof ServerLevel sLevel)) {
            return;
        }
        HitResult result = event.getRayTraceResult();
        if (!(result instanceof EntityHitResult entityHit) || !(entityHit.getEntity() instanceof ServerPlayer sPlayer)) {
            return;
        }
        ItemStack active = sPlayer.getUseItem();
        if (!sPlayer.isUsingItem() || active.isEmpty() || !(active.getItem() instanceof IridescentCrystalSwordItem)) {
            return;
        }

        Vector3 reflected = new Vector3(projectile.getDeltaMovement()).multiply(-1);
        float speed = Math.max((float) reflected.length() * 1.2F, 0.6F);
        projectile.setOwner(sPlayer);
        projectile.shoot(reflected.getX(), reflected.getY(), reflected.getZ(), speed, 0F);
        event.setCanceled(true);

        Vector3 at = new Vector3(projectile).addY(projectile.getBbHeight() / 2F);
        ServerSoundHelper.playSoundAround(SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, sLevel, at, 0.8F, 1.4F);

        int chargeTicks = active.getUseDuration(sPlayer) - sPlayer.getUseItemRemainingTicks();
        if (chargeTicks <= IridescentCrystalSwordItem.PARRY_WINDOW_TICKS && !empoweredCharges.contains(sPlayer.getUUID())) {
            empoweredCharges.add(EMPOWERED_TIMEOUT, sPlayer.getUUID());

            ServerSoundHelper.playSoundAround(SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, sLevel, at, 0.6F, 1.8F);
            LightningEffect.make(Vector3.atCenter(sPlayer), at, ColorsAS.CELESTIAL_STRIKE_LIGHT)
                    .sendToNearby(sLevel, at.toBlockPos());
        }
    }
}
