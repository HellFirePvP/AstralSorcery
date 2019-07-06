/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.reflection;

import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.GameRules;
import net.minecraftforge.fml.network.NetworkInstance;
import net.minecraftforge.fml.network.NetworkRegistry;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ReflectionHelper
 * Created by HellFirePvP
 * Date: 21.04.2019 / 19:46
 */
public class ReflectionHelper {

    private static BiFunction<Object, Object[], ?> createNetworkInstanceMethod = null,
            createGameRuleMethod = null;
    private static Function<Object[], ?> gameRuleTypeConstructor = null;

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

    public static <T extends GameRules.RuleValue<T>> GameRules.RuleKey<T> registerGameRule(String name,
                                                                                           GameRules.RuleType<T> type) {

        if (createGameRuleMethod == null) {
            createGameRuleMethod = resolveMethod(
                    GameRules.class,
                    "register",
                    String.class, GameRules.RuleType.class
            );
        }
        return (GameRules.RuleKey<T>) createGameRuleMethod.apply(null,
                new Object[] { name, type });
    }

    public static GameRules.RuleType<GameRules.BooleanValue> newBooleanType(Supplier<ArgumentType<?>> argumentSupplier,
                                                                            Function<GameRules.RuleType<GameRules.BooleanValue>, GameRules.BooleanValue> typeExtractor,
                                                                            BiConsumer<MinecraftServer, GameRules.BooleanValue> ruleAcceptor) {

        if (gameRuleTypeConstructor == null) {
            gameRuleTypeConstructor = resolveConstructor(
                    GameRules.RuleType.class,
                    Supplier.class, Function.class, BiConsumer.class
            );
        }
        return (GameRules.RuleType<GameRules.BooleanValue>) gameRuleTypeConstructor.apply(
                new Object[] { argumentSupplier, typeExtractor, ruleAcceptor });
    }

    private static Function<Object[], Object> resolveConstructor(Class<?> owningClass, Class<?>... parameters) {
        return (invokeParams) -> {
            try {
                return owningClass.getDeclaredConstructor(parameters).newInstance(invokeParams);
            } catch (Exception e) {
                throw new ReflectionException("Failed to resolve/call Constructor!", e);
            }
        };
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
