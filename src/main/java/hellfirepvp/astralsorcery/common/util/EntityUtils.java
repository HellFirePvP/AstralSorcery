package hellfirepvp.astralsorcery.common.util;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityUtils
 * Created by HellFirePvP
 * Date: 14.09.2016 / 20:10
 */
public class EntityUtils {

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

}
