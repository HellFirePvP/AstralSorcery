/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.impl;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerk;
import hellfirepvp.astralsorcery.common.util.EntityUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.config.Configuration;

import java.awt.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkDestructionDamageArc
 * Created by HellFirePvP
 * Date: 28.07.2017 / 17:59
 */
public class PerkDestructionDamageArc extends ConstellationPerk {

    private static AxisAlignedBB searchBox = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

    private static float arcChance = 0.75F;
    private static float arcPercent = 1.75F;
    private static float distanceSearch = 4F;

    private static boolean chaining = false;

    public PerkDestructionDamageArc() {
        super("DTR_DAMAGEARC", Target.ENTITY_ATTACK);
    }

    @Override
    public float onEntityAttack(EntityPlayer attacker, EntityLivingBase attacked, float dmgIn) {
        if(!attacked.getEntityWorld().isRemote && attacked.getEntityWorld() instanceof WorldServer && !chaining) {
            if(rand.nextFloat() < arcChance) {
                List<EntityLivingBase> entities = attacked.getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, searchBox.offset(attacked.getPositionVector()), EntityUtils.selectEntities(EntityLivingBase.class));
                entities.remove(attacked);
                entities.remove(attacker);
                if(!attacker.getEntityWorld().getMinecraftServer().isPVPEnabled()) {
                    entities.removeIf(e -> e instanceof EntityPlayer);
                }
                if(!entities.isEmpty()) {
                    EntityLivingBase closest = EntityUtils.selectClosest(entities, (e) -> (double) e.getDistanceToEntity(attacked));
                    if(closest != null && !closest.isDead) {
                        chaining = true;
                        AstralSorcery.proxy.fireLightning(attacked.getEntityWorld(), Vector3.atEntityCorner(attacked), Vector3.atEntityCorner(closest), Color.WHITE);
                        attacked.attackEntityFrom(CommonProxy.dmgSourceStellar.setSource(attacker), arcPercent * dmgIn);
                        closest.attackEntityFrom(CommonProxy.dmgSourceStellar.setSource(attacker), arcPercent * dmgIn);
                        chaining = false;
                    }
                }
            }
        }
        return dmgIn;
    }

    @Override
    public boolean hasConfigEntries() {
        return true;
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        arcChance = cfg.getFloat(getKey() + "ArcChance", getConfigurationSection(), arcChance, 0F, 1F, "Defines the chance for an arc to happen at all");
        arcPercent = cfg.getFloat(getKey() + "ArcDamagePercent", getConfigurationSection(), arcPercent, 0F, 32F, "Defines, if an damage-arc happens, how much damage it'll deal to both entities. Multiplicative off of the damage of the original attack.");
        float search = cfg.getFloat(getKey() + "ArcDistance", getConfigurationSection(), distanceSearch, 1F, 16F, "Defines the radius in which the arc-logic will search for an entity to arc damage towards.");

        searchBox = new AxisAlignedBB(-search, -search, -search, search, search, search);
    }

}
