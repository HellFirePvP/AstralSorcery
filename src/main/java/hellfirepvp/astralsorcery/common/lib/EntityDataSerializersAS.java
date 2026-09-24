/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.recipe.altar.ActiveAltarRecipe;
import hellfirepvp.astralsorcery.common.util.data.ResolvedDeferredHolder;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: EntityDataSerializersAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class EntityDataSerializersAS {

    public static final DeferredRegister<EntityDataSerializer<?>> DATA_SERIALIZERS_REGISTER =
            DeferredRegister.create(NeoForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, AstralSorcery.MODID);

    public static final ResolvedDeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<Vector3>> VECTOR =
            register("vector", EntityDataSerializer.forValueType(Vector3.STREAM_CODEC));
    public static final ResolvedDeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<FluidStack>> FLUID_STACK =
            register("fluid_stack", EntityDataSerializer.forValueType(FluidStack.STREAM_CODEC));

    public static final ResolvedDeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<ActiveAltarRecipe.AdditionalInput>> ALTAR_INPUT_REFERENCE =
            register("altar_input_reference", EntityDataSerializer.forValueType(ActiveAltarRecipe.AdditionalInput.STREAM_CODEC));

    private static <T> ResolvedDeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<T>> register(String name, EntityDataSerializer<T> value) {
        DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<T>> holder = DATA_SERIALIZERS_REGISTER.register(name, () -> value);
        return ResolvedDeferredHolder.of(holder, value);
    }
}
