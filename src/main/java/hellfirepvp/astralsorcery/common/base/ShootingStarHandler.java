/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import hellfirepvp.astralsorcery.common.auxiliary.tick.ITickHandler;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.config.entry.ConfigEntry;
import hellfirepvp.astralsorcery.common.entities.EntityShootingStar;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ShootingStarHandler
 * Created by HellFirePvP
 * Date: 13.10.2018 / 15:25
 */
public class ShootingStarHandler implements ITickHandler {

    private static final ShootingStarHandler instance = new ShootingStarHandler();
    private static final Random rand = new Random();

    // Yes this cache is not at all "safe" or whatever, but since the
    private Map<UUID, List<Integer>> fleetingServerCache = Maps.newHashMap();

    private ShootingStarHandler() {}

    public static ShootingStarHandler getInstance() {
        return instance;
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        Side side = (Side) context[1];
        if (side == Side.SERVER) {
            EntityPlayer player = (EntityPlayer) context[0];
            World w = player.getEntityWorld();
            if (w.provider.isNether() ||
                    !w.provider.hasSkyLight() ||
                    !w.provider.isSurfaceWorld()) {
                return;
            }

            int midnight = Math.round(Config.dayLength * 0.75F);
            int tfHalf = Config.dayLength / 12;
            int ch = Config.dayLength / 8;
            int dayTime = (int) (player.getEntityWorld().getWorldTime() % Config.dayLength);
            if (dayTime >= (midnight - tfHalf) && dayTime <= (midnight + tfHalf)) {
                if (rand.nextInt(ch) == 0) {
                    List<Integer> handledDays = fleetingServerCache.getOrDefault(player.getUniqueID(), Lists.newArrayList());
                    int day = (int) (player.getEntityWorld().getWorldTime() / Config.dayLength);
                    if (!handledDays.contains(day)) {
                        Vector3 movement = Vector3.positiveYRandom().setY(0).normalize().multiply(0.2);
                        EntityShootingStar star = new EntityShootingStar(player.getEntityWorld(), player.posX, 560, player.posZ, movement, player);
                        player.getEntityWorld().spawnEntity(star);
                        handledDays.add(day);
                        fleetingServerCache.put(player.getUniqueID(), handledDays);
                    }
                }
            }
        }
    }

    public void clearServerCache() {
        this.fleetingServerCache.clear();
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.PLAYER);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return StarConfigEntry.enabled && phase == TickEvent.Phase.END;
    }

    @Override
    public String getName() {
        return "ShootingStar Handler";
    }

    public static class StarConfigEntry extends ConfigEntry {

        public static boolean enabled = true;
        public static boolean doExplosion = false;

        public StarConfigEntry() {
            super(Section.GENERAL, "shooting_stars");
        }

        @Override
        public void loadFromConfig(Configuration cfg) {
            enabled = cfg.getBoolean("enabled", this.getConfigurationSection(), enabled, "Set to false to disable shooting stars from spawning");
            doExplosion = cfg.getBoolean("doExplosion", this.getConfigurationSection(), doExplosion, "Set to true to make shooting stars do a little explosion where they land");
        }
    }
}
