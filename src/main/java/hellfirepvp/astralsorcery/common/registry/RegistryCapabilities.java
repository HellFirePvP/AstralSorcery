/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.common.capability.ChunkFluidEntry;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

import static hellfirepvp.astralsorcery.common.lib.CapabilitiesAS.CHUNK_FLUID;
import static hellfirepvp.astralsorcery.common.lib.CapabilitiesAS.CHUNK_FLUID_KEY;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryCapabilities
 * Created by HellFirePvP
 * Date: 19.07.2019 / 13:59
 */
public class RegistryCapabilities {

    private RegistryCapabilities() {}

    public static void init(IEventBus eventBus) {
        registerDefault(ChunkFluidEntry.class, ChunkFluidEntry::new);

        eventBus.addGenericListener(Chunk.class, RegistryCapabilities::attachChunkCapability);
    }

    private static void attachChunkCapability(AttachCapabilitiesEvent<Chunk> chunkEvent) {
        chunkEvent.addCapability(CHUNK_FLUID_KEY, serializeableProvider(CHUNK_FLUID.getDefaultInstance()));
    }

    private static <T extends INBTSerializable<CompoundNBT>> void registerDefault(Class<T> capabilityClass, Supplier<T> capProvider) {
        register(capabilityClass, serializeableStorage(), capProvider);
    }

    private static <T> void register(Class<T> capabilityClass, Capability.IStorage<T> capStorage, Supplier<T> capProvider) {
        CapabilityManager.INSTANCE.register(capabilityClass, capStorage, capProvider::get);
    }

    private static <E extends INBTSerializable<CompoundNBT>> ICapabilitySerializable<CompoundNBT> serializeableProvider(E defaultInstance) {
        return new ICapabilitySerializable<CompoundNBT>() {
            @Nonnull
            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                if (cap == CHUNK_FLUID) {
                    return LazyOptional.of(() -> (T) defaultInstance);
                }
                return LazyOptional.empty();
            }

            @Override
            public CompoundNBT serializeNBT() {
                return defaultInstance.serializeNBT();
            }

            @Override
            public void deserializeNBT(CompoundNBT nbt) {
                defaultInstance.deserializeNBT(nbt);
            }
        };
    }

    private static <T extends INBTSerializable<CompoundNBT>> Capability.IStorage<T> serializeableStorage() {
        return new Capability.IStorage<T>() {
            @Nullable
            @Override
            public INBT writeNBT(Capability<T> capability, T instance, Direction side) {
                return instance.serializeNBT();
            }

            @Override
            public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt) {
                instance.deserializeNBT((CompoundNBT) nbt);
            }
        };
    }

}
