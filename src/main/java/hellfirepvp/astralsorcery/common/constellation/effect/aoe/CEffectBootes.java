/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect.aoe;

import hellfirepvp.astralsorcery.common.base.HerdableAnimal;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.CEffectEntityCollect;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.ILocatable;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectBootes
 * Created by HellFirePvP
 * Date: 10.01.2017 / 18:34
 */
public class CEffectBootes extends CEffectEntityCollect<EntityLivingBase> {

    public static double herdChance = 0.02;
    public static double potencyMultiplier = 1.0;
    public static float herdingLuck = -5F;
    public static float dropChance = 0.01F;

    public CEffectBootes(@Nullable ILocatable origin) {
        super(origin, Constellations.bootes, "bootes", 12, EntityLivingBase.class, (e) -> HerdableAnimal.getHerdable(e) != null);
    }

    @Override
    public boolean playMainEffect(World world, BlockPos pos, float percStrength, boolean mayDoTraitEffect, @Nullable IMinorConstellation possibleTraitEffect) {
        percStrength *= potencyMultiplier;
        if(percStrength < 1) {
            if(world.rand.nextFloat() > percStrength) return false;
        }
        List<EntityLivingBase> entities = collectEntities(world, pos);
        if(!entities.isEmpty()) {
            for (EntityLivingBase e : entities) {
                HerdableAnimal herd = HerdableAnimal.getHerdable(e);
                if(herd == null) continue;
                if(rand.nextFloat() < herdChance) {
                    List<ItemStack> drops = herd.getHerdingDropsTick(e, world, rand, herdingLuck);
                    for (ItemStack stack : drops) {
                        if(rand.nextFloat() < dropChance) {
                            ItemUtils.dropItemNaturally(world, e.posX, e.posY, e.posZ, stack);
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean playTraitEffect(World world, BlockPos pos, IMinorConstellation traitType, float traitStrength) {
        return false;
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        super.loadFromConfig(cfg);

        herdingLuck = cfg.getFloat(getKey() + "HerdLuck", getConfigurationSection(), -5F, -200.0F, 200.0F, "Set the 'luck' when herding an animal for drops or related");
        herdChance = cfg.getFloat(getKey() + "HerdChance", getConfigurationSection(), 0.05F, 0.0F, 1.0F, "Set the chance that an registered animal will be 'herded' if it is close to the ritual.");
        potencyMultiplier = cfg.getFloat(getKey() + "PotencyMultiplier", getConfigurationSection(), 1.0F, 0.01F, 100F, "Set the potency multiplier for this ritual effect. Will affect all ritual effects and their efficiency.");
        dropChance = cfg.getFloat(getKey() + "OverallDropChance", getConfigurationSection(), 0.01F, 0.0F, 1.0F, "Set the chance that a drop that has been found from the entity's loot table is actually dropped.");
    }
}
