/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import com.google.common.base.Predicate;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityUtils
 * Created by HellFirePvP
 * Date: 14.09.2016 / 20:10
 */
public class EntityUtils {

    public static void applyVortexMotion(Function<Void, Vector3> positionFunc, Function<Vector3, Object> addMotion, Vector3 to, double vortexRange, double multiplier) {
        Vector3 pos = positionFunc.apply(null);
        double diffX = (to.getX() - pos.getX()) / vortexRange;
        double diffY = (to.getY() - pos.getY()) / vortexRange;
        double diffZ = (to.getZ() - pos.getZ()) / vortexRange;
        double dist = Math.sqrt(diffX * diffX + diffY * diffY + diffZ * diffZ);
        if (1.0D - dist > 0.0D) {
            double dstFactorSq = (1.0D - dist) * (1.0D - dist);
            Vector3 toAdd = new Vector3();
            toAdd.setX(diffX / dist * dstFactorSq * 0.15D * multiplier);
            toAdd.setY(diffY / dist * dstFactorSq * 0.25D * multiplier);
            toAdd.setZ(diffZ / dist * dstFactorSq * 0.15D * multiplier);
            addMotion.apply(toAdd);
        }
    }

    public static Predicate<? super Entity> selectEntities(Class<? extends Entity>... entities) {
        return new Predicate<Entity>() {
            @Override
            public boolean apply(@Nullable Entity entity) {
                if(entity == null || entity.isDead) return false;
                Class<? extends Entity> clazz = entity.getClass();
                for (Class<? extends Entity> test : entities) {
                    if(test.isAssignableFrom(clazz)) return true;
                }
                return false;
            }
        };
    }

    public static Predicate<? super Entity> selectItemClassInstaceof(Class<?> itemClass) {
        return new Predicate<Entity>() {
            @Override
            public boolean apply(@Nullable Entity entity) {
                if(entity == null || entity.isDead) return false;
                if(!(entity instanceof EntityItem)) return false;
                ItemStack i = ((EntityItem) entity).getEntityItem();
                if(i == null || i.getItem() == null) return false;
                return itemClass.isAssignableFrom(i.getItem().getClass());
            }
        };
    }

    public static Predicate<? super Entity> selectItem(Item item) {
        return new Predicate<Entity>() {
            @Override
            public boolean apply(@Nullable Entity entity) {
                if(entity == null || entity.isDead) return false;
                if(!(entity instanceof EntityItem)) return false;
                ItemStack i = ((EntityItem) entity).getEntityItem();
                if(i == null || i.getItem() == null) return false;
                return i.getItem().equals(item);
            }
        };
    }

    public static Predicate<? super Entity> selectItemStack(Function<ItemStack, Boolean> acceptor) {
        return entity -> {
            if(entity == null || entity.isDead) return false;
            if(!(entity instanceof EntityItem)) return false;
            ItemStack i = ((EntityItem) entity).getEntityItem();
            if(i == null || i.getItem() == null) return false;
            return acceptor.apply(i);
        };
    }

    @Nullable
    public static <T> T selectClosest(Collection<T> elements, Function<T, Double> dstFunc) {
        if(elements.isEmpty()) return null;

        double dstClosest = Double.MAX_VALUE;
        T closestElement = null;
        for (T element : elements) {
            double dst = dstFunc.apply(element);
            if(dst < dstClosest) {
                closestElement = element;
                dstClosest = dst;
            }
        }
        return closestElement;
    }

}
