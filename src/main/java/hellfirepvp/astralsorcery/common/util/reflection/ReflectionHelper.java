/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.reflection;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkInstance;
import net.minecraftforge.fml.network.NetworkRegistry;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ReflectionHelper
 * Created by HellFirePvP
 * Date: 21.04.2019 / 19:46
 */
public class ReflectionHelper {

    private static BiFunction<Object, Object[], ?> createNetworkInstanceMethod = null;

    public static NetworkInstance createInstance(ResourceLocation name,
                                                 Supplier<String> networkProtocolVersion,
                                                 Predicate<String> clientAcceptedVersions,
                                                 Predicate<String> serverAcceptedVersions) {

        if (createNetworkInstanceMethod == null) {
            createNetworkInstanceMethod = resolveMethod(
                    NetworkRegistry.class,
                    "createInstance",
                    ResourceLocation.class, Supplier.class, Predicate.class, Predicate.class
            );
        }
        return (NetworkInstance) createNetworkInstanceMethod.apply(null,
                new Object[] { name, networkProtocolVersion, clientAcceptedVersions, serverAcceptedVersions });
    }

    private static BiFunction<Object, Object[], Object> resolveMethod(Class<?> owningClass, String methodName, Class<?>... parameters) {
        return (owningObject, invokeParams) -> {
            try {
                return owningClass.getDeclaredMethod(methodName, parameters).invoke(owningObject, invokeParams);
            } catch (Exception e) {
                throw new ReflectionException("Failed to resolve/call Method!", e);
            }
        };
    }

}
