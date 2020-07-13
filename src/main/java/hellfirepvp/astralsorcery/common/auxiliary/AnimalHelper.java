/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.auxiliary;

import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.util.entity.EntityUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AnimalHelper
 * Created by HellFirePvP
 * Date: 23.11.2019 / 20:47
 */
public class AnimalHelper {

    private static LinkedList<HerdableAnimal> animalHandlers = new LinkedList<>();

    static {
        register(new Squid());

        //Generic fallback for any AnimalEntity
        register(new GenericAnimal());
    }

    //Allows for overriding existing handling by capturing specific calls first
    public static void registerFirst(HerdableAnimal handler) {
        animalHandlers.addFirst(handler);
    }

    public static void register(HerdableAnimal handler) {
        animalHandlers.add(handler);
    }

    @Nullable
    public static HerdableAnimal getHandler(LivingEntity entity) {
        for (HerdableAnimal herd : animalHandlers) {
            if (herd.handles(entity)) {
                return herd;
            }
        }
        return null;
    }

    public static interface HerdableAnimal {

        public boolean handles(@Nonnull LivingEntity entity);

        public List<ItemStack> generateDrops(@Nonnull LivingEntity entity, World world, Random rand, float luck);

    }

    public static class GenericAnimal implements HerdableAnimal {

        @Override
        public boolean handles(@Nonnull LivingEntity entity) {
            return entity instanceof AnimalEntity;
        }

        @Override
        public List<ItemStack> generateDrops(@Nonnull LivingEntity entity, World world, Random rand, float luck) {
            return EntityUtils.generateLoot(entity, rand, CommonProxy.DAMAGE_SOURCE_STELLAR, null);
        }
    }

    public static class Squid extends GenericAnimal {

        @Override
        public boolean handles(@Nonnull LivingEntity entity) {
            return entity instanceof SquidEntity;
        }
    }
}
