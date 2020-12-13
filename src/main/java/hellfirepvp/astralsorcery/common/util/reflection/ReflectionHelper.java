/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.reflection;

import net.minecraft.entity.item.ItemEntity;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ReflectionHelper
 * Created by HellFirePvP
 * Date: 21.04.2019 / 19:46
 */
public class ReflectionHelper {

    private static Field itemEntitySkipPhysicRenderer;

    public static void setSkipItemPhysicsRender(ItemEntity entity) {
        if (itemEntitySkipPhysicRenderer == null) {
            itemEntitySkipPhysicRenderer = ObfuscationReflectionHelper.findField(ItemEntity.class, "skipPhysicRenderer");
        }

        try {
            itemEntitySkipPhysicRenderer.setBoolean(entity, true);
        } catch (IllegalAccessException e) {}
    }

    private static Function<Object[], Object> resolveConstructor(Class<?> owningClass, Class<?>... parameters) {
        return (invokeParams) -> {
            try {
                Constructor<?> ctor = owningClass.getDeclaredConstructor(parameters);
                ctor.setAccessible(true);
                return ctor.newInstance(invokeParams);
            } catch (Exception e) {
                throw new ReflectionException("Failed to resolve/call Constructor!", e);
            }
        };
    }

    private static BiFunction<Object, Object[], Object> resolveMethod(Class<?> owningClass, String methodName, Class<?>... parameters) {
        return (owningObject, invokeParams) -> {
            try {
                Method m = owningClass.getDeclaredMethod(methodName, parameters);
                m.setAccessible(true);
                return m.invoke(owningObject, invokeParams);
            } catch (Exception e) {
                throw new ReflectionException("Failed to resolve/call Method!", e);
            }
        };
    }
}
