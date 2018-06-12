/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect.aoe;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.CEffectPositionListGen;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectProperties;
import hellfirepvp.astralsorcery.common.constellation.effect.GenListEntries;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.registry.RegistryPotions;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.ILocatable;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectPelotrio
 * Created by HellFirePvP
 * Date: 28.07.2017 / 20:15
 */
public class CEffectPelotrio extends CEffectPositionListGen<GenListEntries.PelotrioSpawnListEntry> {

    private static Tuple<Class<Entity>, Class<Entity>>[] swapTable = new Tuple[] {
            new Tuple(EntitySkeleton.class, EntityWitherSkeleton.class),
            new Tuple(EntityVillager.class, EntityWitch.class),
            new Tuple(EntityPig.class,      EntityPigZombie.class),
            new Tuple(EntityCow.class,      EntityZombie.class),
            new Tuple(EntityParrot.class,   EntityGhast.class),
            new Tuple(EntityChicken.class,  EntityBlaze.class),
            new Tuple(EntitySheep.class,    EntityStray.class),
            new Tuple(EntityHorse.class,    EntitySkeletonHorse.class)
    };

    private static AxisAlignedBB proximityCheckBox = new AxisAlignedBB(0, 0, 0, 0, 0, 0).grow(24);

    private static boolean enabled = true;
    private static float potencyMultiplier = 1F;
    private static int searchRange = 12;

    private static float selectChance = 0.1F;
    private static int proximityAmount = 40;

    public CEffectPelotrio(@Nullable ILocatable origin) {
        super(origin, Constellations.pelotrio, "pelotrio", 5,
                (w, pos) -> GenListEntries.PelotrioSpawnListEntry.createEntry(w, pos) != null, GenListEntries.PelotrioSpawnListEntry::new);
    }

    @Override
    public GenListEntries.PelotrioSpawnListEntry newElement(World world, BlockPos at) {
        return GenListEntries.PelotrioSpawnListEntry.createEntry(world, at);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void playClientEffect(World world, BlockPos pos, TileRitualPedestal pedestal, float percEffectVisibility, boolean extendedEffects) {
        for (int i = 0; i < 1 + rand.nextInt(3); i++) {
            Vector3 particlePos = new Vector3(
                    pos.getX() - 3 + rand.nextFloat() * 7,
                    pos.getY() + rand.nextFloat() * 2,
                    pos.getZ() - 3 + rand.nextFloat() * 7
            );
            Vector3 dir = particlePos.clone().subtract(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5).normalize().divide(-30);
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(particlePos.getX(), particlePos.getY(), particlePos.getZ());
            p.motion(dir.getX(), dir.getY(), dir.getZ()).setAlphaMultiplier(1F).setMaxAge(rand.nextInt(40) + 20);
            p.enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT).scale(0.2F + rand.nextFloat() * 0.1F).setColor(new Color(29, 123, 59));
        }
    }

    @Override
    public boolean playEffect(World world, BlockPos pos, float percStrength, ConstellationEffectProperties modified, @Nullable IMinorConstellation possibleTraitEffect) {
        if(!enabled) return false;
        percStrength *= potencyMultiplier;
        if(percStrength < 1) {
            if(world.rand.nextFloat() > percStrength) return false;
        }

        if(modified.isCorrupted()) {
            boolean did = false;
            List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(0, 0, 0, 0, 0, 0).grow(modified.getSize()).offset(pos));
            for (EntityLivingBase e : entities) {
                if(e != null && !e.isDead && rand.nextInt(350) == 0) {
                    e = trySwapEntity(world, e);
                    if(e != null) {
                        EntityLivingBase finalEntity = e;
                        e.addPotionEffect(new PotionEffect(RegistryPotions.potionDropModifier, Integer.MAX_VALUE, 3));
                        AstralSorcery.proxy.scheduleDelayed(() -> world.spawnEntity(finalEntity));
                        did = true;
                    }
                }
            }
            return did;
        }

        GenListEntries.PelotrioSpawnListEntry entry = getRandomElement(rand);
        if(entry != null) {
            entry.counter++;
            PktParticleEvent ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.CE_SPAWN_PREPARE_EFFECTS, entry.getPos());
            PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, entry.getPos(), 24));
            if(entry.counter >= 30) {
                entry.spawn(world);
                removeElement(entry);
            }
        }

        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, proximityCheckBox.offset(pos), e -> e != null && !e.isDead);
        if(entities.size() > proximityAmount) {
            return false; //Flood & lag prevention.
        }

        boolean update = false;
        if(rand.nextFloat() < selectChance) {
            if(findNewPosition(world, pos, modified)) {
                update = true;
            }
        }

        return update;
    }

    private EntityLivingBase trySwapEntity(World world, EntityLivingBase e) {
        for (Tuple<Class<Entity>, Class<Entity>> tpl : swapTable) {
            if(e.getClass().isAssignableFrom(tpl.key)) {
                NBTTagCompound cmp = new NBTTagCompound();
                e.writeToNBT(cmp);
                world.removeEntity(e);
                NBTHelper.removeUUID(cmp, "UUID");
                try {
                    EntityLivingBase ews = (EntityLivingBase) tpl.value.getConstructor(World.class).newInstance(world);
                    ews.readFromNBT(cmp);
                    return ews;
                } catch (Exception e1) {
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    public ConstellationEffectProperties provideProperties(int mirrorCount) {
        return new ConstellationEffectProperties(CEffectPelotrio.searchRange);
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        searchRange = cfg.getInt(getKey() + "Range", getConfigurationSection(), searchRange, 1, 32, "Defines the radius (in blocks) in which the ritual will search for potential spawn points for entities.");
        enabled = cfg.getBoolean(getKey() + "Enabled", getConfigurationSection(), enabled, "Set to false to disable this ConstellationEffect.");
        potencyMultiplier = cfg.getFloat(getKey() + "PotencyMultiplier", getConfigurationSection(), potencyMultiplier, 0.01F, 100F, "Set the potency multiplier for this ritual effect. Will affect all ritual effects and their efficiency.");

        selectChance = cfg.getFloat(getKey() + "SpawnSearchChance", getConfigurationSection(), selectChance, 0F, 1F, "Defines the per-tick chance that a new position for a entity-spawn will be searched for.");
        proximityAmount = cfg.getInt(getKey() + "ProximityCheck", getConfigurationSection(), proximityAmount, 1, 256, "Defines the threshold at which the ritual will stop spawning mobs. If there are more or equal amount of mobs near this ritual, the ritual will not spawn more mobs. Mainly to reduce potential server lag.");
    }

    @SideOnly(Side.CLIENT)
    public static void playSpawnPrepareEffects(PktParticleEvent pktParticleEvent) {
        Vector3 at = pktParticleEvent.getVec().add(0.5, 0, 0.5);
        for (int i = 0; i < 1; i++) {
            Vector3 dir = new Vector3(
                    rand.nextFloat() * (rand.nextBoolean() ? 1 : -1) * 0.05F,
                    rand.nextFloat() * 0.05F,
                    rand.nextFloat() * (rand.nextBoolean() ? 1 : -1) * 0.05F
            );
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(at.getX(), at.getY(), at.getZ());
            p.motion(dir.getX(), dir.getY(), dir.getZ()).setAlphaMultiplier(1F).setMaxAge(rand.nextInt(40) + 20);
            p.enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT).scale(0.2F + rand.nextFloat() * 0.1F);
            if(rand.nextBoolean()) {
                p.setColor(new Color(29, 123, 59));
            } else {
                p.setColor(new Color(78, 1, 109));
            }
        }
    }

}
