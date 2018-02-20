/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.cape.impl;

import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.cape.CapeArmorEffect;
import hellfirepvp.astralsorcery.common.entities.EntityFlare;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CapeEffectBootes
 * Created by HellFirePvP
 * Date: 18.10.2017 / 00:56
 */
public class CapeEffectBootes extends CapeArmorEffect {

    private static int maxFlareCount = 3;
    private static float summonFlareChance = 0.005F;

    public CapeEffectBootes(NBTTagCompound cmp) {
        super(cmp, "bootes");
    }

    public void onPlayerTick(EntityPlayer pl) {
        World w = pl.getEntityWorld();
        int current = gatherEntities(w).size();
        EntityFlare flare = null;
        if(current < maxFlareCount) {
            if(rand.nextFloat() < summonFlareChance) {
                Vector3 pos = Vector3.atEntityCenter(pl);
                flare = new EntityFlare(w, pos.getX(), pos.getY(), pos.getZ());
                flare.setFollowingTarget(pl);
                w.spawnEntity(flare);
            }
        }
        updateEntityList(w, flare);
        List<Entity> fl = gatherEntities(w);
        for (Entity e : fl) {
            if(e instanceof EntityFlare && ((EntityFlare) e).getFollowingEntity() != null && pl.getDistanceToEntity(e) >= 10) {
                e.setPositionAndRotation(pl.posX, pl.posY, pl.posZ, 0, 0);
            }
        }
    }

    public void onPlayerDamagedByEntity(EntityPlayer player, EntityLivingBase attacker) {
        List<Entity> entityFlares = gatherEntities(player.getEntityWorld());
        for (Entity e : entityFlares) {
            if(e instanceof EntityFlare) {
                ((EntityFlare) e).setAttackTarget(attacker);
            }
        }
    }

    private List<Integer> updateEntityList(World currentWorld, @Nullable Entity newEntityAdded) {
        List<Integer> current = gatherEntityIds();
        List<Integer> out = new LinkedList<>();
        for (Integer id : current) {
            Entity e = currentWorld.getEntityByID(id);
            if(e != null && !e.isDead && (e instanceof EntityFlare)) {
                out.add(id);
            }
        }
        if(newEntityAdded != null && !newEntityAdded.isDead) {
            out.add(newEntityAdded.getEntityId());
        }
        pushEntityIds(out);
        return out;
    }

    private void pushEntityIds(List<Integer> out) {
        NBTTagList list = new NBTTagList();
        for (Integer id : out) {
            list.appendTag(new NBTTagInt(id));
        }
        getData().setTag("AS_Bootes_Entities", list);
    }

    private List<Entity> gatherEntities(World currentWorld) {
        List<Integer> current = gatherEntityIds();
        List<Entity> out = new LinkedList<>();
        for (Integer id : current) {
            Entity e = currentWorld.getEntityByID(id);
            if(e != null && !e.isDead && (e instanceof EntityFlare)) {
                out.add(e);
            }
        }
        return out;
    }

    private List<Integer> gatherEntityIds() {
        List<Integer> entities = new LinkedList<>();
        NBTTagList list = getData().getTagList("AS_Bootes_Entities", Constants.NBT.TAG_INT);
        for (int i = 0; i < list.tagCount(); i++) {
            entities.add(list.getIntAt(i));
        }
        return entities;
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        maxFlareCount = cfg.getInt(getKey() + "MaxFlareCount", getConfigurationSection(), maxFlareCount, 2, 8, "Defines the maximum flare count the player can draw with him.");
        summonFlareChance = cfg.getFloat(getKey() + "TickSummonChance", getConfigurationSection(), summonFlareChance, 0F, 1F, "Defines the chance per tick that a new flare following the wearer is spawned.");
    }

    @Override
    public IConstellation getAssociatedConstellation() {
        return Constellations.bootes;
    }

    @Override
    public void playActiveParticleTick(EntityPlayer pl) {
        playConstellationCapeSparkles(pl, 0.2F);
    }

}
