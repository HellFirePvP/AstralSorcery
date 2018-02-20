/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.impl;

import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerk;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkCreationBreedables
 * Created by HellFirePvP
 * Date: 04.12.2016 / 15:43
 */
public class PerkCreationBreedables extends ConstellationPerk {

    private static AxisAlignedBB boxSearch = new AxisAlignedBB(0, 0, 0, 0, 0, 0).expand(6, 6, 6);

    private static int chanceToAge = 30;
    private static int chanceToBreed = 70;

    public PerkCreationBreedables() {
        super("CRE_BREEDING", Target.PLAYER_TICK);
    }

    @Override
    public void onPlayerTick(EntityPlayer player, Side side) {
        if(side == Side.SERVER) {
            List<EntityAnimal> animals = player.getEntityWorld().getEntitiesWithinAABB(EntityAnimal.class, boxSearch.offset(player.getPosition()));
            if(animals.isEmpty()) return;
            EntityAnimal animal = animals.get(rand.nextInt(animals.size()));
            if(!animal.isDead) {
                if(animal.getGrowingAge() < 0 && rand.nextInt(chanceToAge) == 0) {
                    animal.setGrowingAge(0);
                    addAlignmentCharge(player, 0.1);
                }
                if(animal.getGrowingAge() > 0 && rand.nextInt(chanceToBreed) == 0) {
                    EntityAgeable child = animal.createChild(animal);
                    if(child != null) {
                        child.setGrowingAge(-24000);
                        child.setLocationAndAngles(animal.posX, animal.posY, animal.posZ, 0.0F, 0.0F);
                        player.getEntityWorld().spawnEntity(child);
                        addAlignmentCharge(player, 0.2);
                    }
                }
            }
        }
    }

    @Override
    public boolean hasConfigEntries() {
        return true;
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        chanceToAge = cfg.getInt(getKey() + "ChanceToAge", getConfigurationSection(), 30, 10, 4000, "Sets the chance (Random.nextInt(chance) == 0) to try to see if a random animal near the player will grow into an adult");
        chanceToBreed = cfg.getInt(getKey() + "ChangeToBreed", getConfigurationSection(), 70, 10, 4000, "Sets the chance (Random.nextInt(chance) == 0) to try to see if a random animal near the player will create a child");
        float search = cfg.getFloat(getKey() + "EffectRadius", getConfigurationSection(), 6F, 1F, 40F, "Sets the radius in which this effect tries to find animals");
        boxSearch = new AxisAlignedBB(0, 0, 0, 0, 0, 0).grow(search);
    }

}
