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

    private static int chanceToAge = 60;
    private static int chanceToBreed = 200;

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
                }
                if(animal.getGrowingAge() > 0 && rand.nextInt(chanceToBreed) == 0) {
                    EntityAgeable child = animal.createChild(animal);
                    child.setGrowingAge(-24000);
                    child.setLocationAndAngles(animal.posX, animal.posY, animal.posZ, 0.0F, 0.0F);
                    player.getEntityWorld().spawnEntityInWorld(child);
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
        chanceToAge = cfg.getInt(getKey() + "ChanceToAge", getConfigurationSection(), 60, 10, 4000, "Sets the chance (Random.nextInt(chance) == 0) to try to see if a random animal near the player will grow into an adult");
        chanceToBreed = cfg.getInt(getKey() + "ChangeToBreed", getConfigurationSection(), 200, 10, 4000, "Sets the chance (Random.nextInt(chance) == 0) to try to see if a random animal near the player will create a child");
        float search = cfg.getFloat(getKey() + "EffectRadius", getConfigurationSection(), 6F, 1F, 40F, "Sets the radius in which this effect tries to find animals");
        boxSearch = new AxisAlignedBB(0, 0, 0, 0, 0, 0).expandXyz(search);
    }

}
