/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.cape.impl;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingDepthParticle;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.cape.CapeArmorEffect;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CapeEffectLucerna
 * Created by HellFirePvP
 * Date: 16.10.2017 / 23:09
 */
public class CapeEffectLucerna extends CapeArmorEffect {

    private static boolean findSpawners = true;
    private static float range = 36F;

    public CapeEffectLucerna(NBTTagCompound cmp) {
        super(cmp, "lucerna");
    }

    @Override
    public IConstellation getAssociatedConstellation() {
        return Constellations.lucerna;
    }

    @SideOnly(Side.CLIENT)
    public void playClientHighlightTick(EntityPlayer player) {
        World w = player.getEntityWorld();
        List<EntityLivingBase> entities = w.getEntities(EntityLivingBase.class, EntitySelectors.withinRange(player.posX, player.posY, player.posZ, range));
        for (EntityLivingBase entity : entities) {
            if(entity != null && !entity.isDead && !entity.equals(player)) {
                if(rand.nextFloat() > 0.4) continue;
                Vector3 at = Vector3.atEntityCorner(entity);
                if(at.distance(player.getPosition()) < 6) continue;

                at.add(entity.width * rand.nextFloat(), entity.height * rand.nextFloat(), entity.width * rand.nextFloat());
                EntityFXFacingDepthParticle p = EffectHelper.genericDepthIgnoringFlareParticle(at.getX(), at.getY(), at.getZ());
                p.setColor(Constellations.lucerna.getConstellationColor())
                        .setAlphaMultiplier(1F)
                        .scale(0.6F * rand.nextFloat() + 0.6F)
                        .gravity(0.004)
                        .setMaxAge(30 + rand.nextInt(20));
                if(rand.nextInt(3) == 0) {
                    p.setColor(IConstellation.weak);
                }

                if(rand.nextFloat() < 0.8F) {
                    p = EffectHelper.genericDepthIgnoringFlareParticle(at.getX(), at.getY(), at.getZ());
                    p.setColor(Color.WHITE).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
                    p.scale(rand.nextFloat() * 0.3F + 0.3F).gravity(0.004);
                    p.setMaxAge(20 + rand.nextInt(10));
                }
            }
        }
        List<TileEntityMobSpawner> list = Lists.newArrayList();
        int minX = MathHelper.floor((player.posX - range) / 16.0D);
        int maxX = MathHelper.floor((player.posX + range) / 16.0D);
        int minZ = MathHelper.floor((player.posZ - range) / 16.0D);
        int maxZ = MathHelper.floor((player.posZ + range) / 16.0D);

        for (int xx = minX; xx <= maxX; ++xx) {
            for (int zz = minZ; zz <= maxZ; ++zz) {
                Chunk ch = w.getChunkFromChunkCoords(xx, zz);
                if(!ch.isEmpty()) {
                    Map<BlockPos, TileEntity> map = ch.getTileEntityMap();
                    for (Map.Entry<BlockPos, TileEntity> teEntry : map.entrySet()) {
                        TileEntity te = teEntry.getValue();
                        if(te != null && te instanceof TileEntityMobSpawner) {
                            if(rand.nextFloat() > 0.4) continue;
                            list.add((TileEntityMobSpawner) te);
                        }
                    }
                }
            }
        }

        for (TileEntityMobSpawner spawner : list) {
            Vector3 at = new Vector3(spawner.getPos());
            at.add(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());

            EntityFXFacingDepthParticle p = EffectHelper.genericDepthIgnoringFlareParticle(at.getX(), at.getY(), at.getZ());
            p.setColor(new Color(0x9C0100))
                    .setAlphaMultiplier(1F)
                    .scale(0.6F * rand.nextFloat() + 0.6F)
                    .gravity(0.004)
                    .setMaxAge(30 + rand.nextInt(20));
            if(rand.nextInt(3) == 0) {
                p.setColor(IConstellation.weak);
            }

            if(rand.nextFloat() < 0.8F) {
                p = EffectHelper.genericDepthIgnoringFlareParticle(at.getX(), at.getY(), at.getZ());
                p.setColor(Color.WHITE).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
                p.scale(rand.nextFloat() * 0.3F + 0.3F).gravity(0.004);
                p.setMaxAge(20 + rand.nextInt(10));
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void playActiveParticleTick(EntityPlayer pl) {
        playConstellationCapeSparkles(pl, 0.14F);
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        findSpawners = cfg.getBoolean(getKey() + "FindSpawners", getConfigurationSection(), findSpawners, "If this is set to true, particles spawned by the lucerna cape effect will also highlight spawners nearby.");
        range = cfg.getFloat(getKey() + "Range", getConfigurationSection(), range, 12, 512, "Sets the maximum range of where the lucerna cape effect will get entities (and potentially spawners given the config option is enabled) to highlight.");
    }

}
