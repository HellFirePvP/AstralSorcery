/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.auxiliary;

import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FluidRarityRegistry
 * Created by HellFirePvP
 * Date: 30.10.2017 / 14:03
 */
public class FluidRarityRegistry {

    private static final ResourceLocation CAP_FLUIDENTRY_NAME = new ResourceLocation(AstralSorcery.MODID, "cap_chunk_fluid_fountain");
    public static FluidRarityRegistry EVENT_INSTANCE = new FluidRarityRegistry();

    @CapabilityInject(ChunkFluidEntry.class)
    private static Capability<ChunkFluidEntry> CAPABILITY_CHUNK_FLUID = null;

    private FluidRarityRegistry() {}

    private static List<FluidRarityEntry> rarityList = new LinkedList<>();

    public static void init() {
        rarityList.add(FluidRarityEntry.EMPTY);

        registerFluidRarity("lava", 7500, 4000_000, 1000_000);
        registerFluidRarity("astralsorcery.liquidstarlight", 4000, 2000_000, 400_000);

        //AA
        registerFluidRarity("crystaloil", 800, 600_000, 400_000);
        registerFluidRarity("empoweredoil", 200, 350_000, 150_000);

        //TE
        registerFluidRarity("redstone", 500, 120_000, 70_000);
        registerFluidRarity("glowstone", 500, 120_000, 70_000);
        registerFluidRarity("ender", 250, 140_000, 60_000);
        registerFluidRarity("pyrotheum", 200, 200_000, 120_000);
        registerFluidRarity("cryotheum", 200, 200_000, 120_000);
        registerFluidRarity("refined_oil", 600, 480_000, 400_000);
        registerFluidRarity("refined_fuel", 550, 450_000, 300_000);

        //TiC
        registerFluidRarity("iron", 900, 600_000, 350_000);
        registerFluidRarity("gold", 600, 400_000, 350_000);
        registerFluidRarity("cobalt", 80, 150_000, 150_000);
        registerFluidRarity("ardite", 80, 150_000, 150_000);
        registerFluidRarity("emerald", 30, 60_000, 90_000);

        //TR
        registerFluidRarity("fluidoil", 900, 500_000, 350_000);
        registerFluidRarity("fluidnitrodiesel", 450, 400_000, 250_000);

        //IC2
        registerFluidRarity("ic2uu_matter", 1, 600, 800);
        registerFluidRarity("ic2biomass", 600, 300_000, 200_000);
        registerFluidRarity("ic2biogas", 500, 250_000, 150_000);

        Collections.shuffle(rarityList);
    }

    public static void registerFluidRarity(String name, int rarity, int guaraneedAmt, int additionalAmt) {
        Fluid f = FluidRegistry.getFluid(name);
        if(f != null) {
            rarityList.add(new FluidRarityEntry(f, rarity, guaraneedAmt, additionalAmt));
        } else {
            AstralSorcery.log.info("[AstralSorcery] Ignoring fluid " + name + " for rarity registry - it doesn't exist in the current environment");
        }
    }

    @Nullable
    private static FluidRarityEntry selectFluidEntry(Random random) {
        FluidRarityEntry entry = WeightedRandom.getRandomItem(random, rarityList);
        if(entry == FluidRarityEntry.EMPTY || entry.fluid == null) {
            return null;
        }
        return entry;
    }

    @Nullable
    public static ChunkFluidEntry getChunkEntry(Chunk ch) {
        if(ch.hasCapability(CAPABILITY_CHUNK_FLUID, null)) {
            return ch.getCapability(CAPABILITY_CHUNK_FLUID, null);
        }
        return null;
    }

    @SubscribeEvent
    public void onChLoad(ChunkDataEvent.Load event) {
        if(event.getChunk().hasCapability(CAPABILITY_CHUNK_FLUID, null)) {
            ChunkFluidEntry entry = event.getChunk().getCapability(CAPABILITY_CHUNK_FLUID, null);
            if(entry != null && !entry.hadSomeData()) {
                World w = event.getWorld();
                long seed = w.getSeed();
                long chX = event.getChunk().x;
                long chZ = event.getChunk().z;
                seed ^= chX << 32;
                seed ^= chZ;
                Random r = new Random(seed);
                for (int i = 0; i < r.nextInt(10); i++) {
                    r.nextLong(); //Pre-bit-flush for small numbers.
                }
                FluidRarityEntry sample = selectFluidEntry(r);
                if(sample != null && sample.fluid != null) {
                    entry.generate(sample.fluid, sample.guaranteedAmount + r.nextInt(sample.additionalRandomAmount));
                } else {
                    entry.generateEmpty();
                }
            }
        }
    }

    @SubscribeEvent
    public void attachChunkCap(AttachCapabilitiesEvent<Chunk> chunkEvent) {
        chunkEvent.addCapability(CAP_FLUIDENTRY_NAME, new ChunkFluidEntryProvider());
    }

    public static class FluidRarityEntry extends WeightedRandom.Item {

        public static final FluidRarityEntry EMPTY = new FluidRarityEntry(null, 14000, 0, 0);

        public final Fluid fluid;
        public final int guaranteedAmount, additionalRandomAmount, rarity;

        private FluidRarityEntry(Fluid fluid, int rarity, int guaranteedAmount, int additionalRandomAmount) {
            super(rarity);
            this.fluid = fluid;
            this.rarity = rarity;
            this.guaranteedAmount = guaranteedAmount;
            this.additionalRandomAmount = additionalRandomAmount;
        }
    }

    public static class ChunkFluidEntry implements INBTSerializable<NBTTagCompound> {

        private Fluid chunkFluid;
        private int mbRemaining;
        private boolean hadSomeData = false;

        public final boolean isValid() {
            return chunkFluid != null;
        }

        public final boolean hadSomeData() {
            return hadSomeData;
        }

        private void generate(Fluid fluid, int mbAmount) {
            this.chunkFluid = fluid;
            this.mbRemaining = mbAmount;
            this.hadSomeData = true;
            if(this.mbRemaining <= 0) {
                setEmpty();
            }
        }

        private void generateEmpty() {
            this.hadSomeData = true;
            setEmpty();
        }

        @Nullable
        public FluidStack tryDrain(int mbRequested, boolean consume) {
            if(!isValid()) {
                return null;
            }
            int drained = Math.min(mbRequested, mbRemaining);
            FluidStack generated = new FluidStack(this.chunkFluid, drained);
            if(consume) {
                this.mbRemaining -= drained;
                if(this.mbRemaining <= 0) {
                    setEmpty();
                }
            }
            return generated;
        }

        private void setEmpty() {
            this.chunkFluid = null;
            this.mbRemaining = 0;
        }

        @Override
        public NBTTagCompound serializeNBT() {
            NBTTagCompound cmp = new NBTTagCompound();
            cmp.setBoolean("fluid_had", this.hadSomeData);
            if(chunkFluid != null) {
                cmp.setString("fluid_name", this.chunkFluid.getName());
                cmp.setInteger("fluid_amt", this.mbRemaining);
            }
            return cmp;
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            this.hadSomeData = nbt.getBoolean("fluid_had");
            if(nbt.hasKey("fluid_name")) {
                String fluidTest = nbt.getString("fluid_name");
                Fluid f = FluidRegistry.getFluid(fluidTest);
                if(f != null) {
                    this.chunkFluid = f;
                    this.mbRemaining = nbt.getInteger("fluid_amt");
                } else {
                    setEmpty();
                }
            } else {
                setEmpty();
            }
        }
    }

    public static class ChunkFluidEntryProvider implements ICapabilitySerializable<NBTTagCompound> {

        private final ChunkFluidEntry defaultImpl = new ChunkFluidEntry();

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == CAPABILITY_CHUNK_FLUID;
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            return hasCapability(capability, facing) ? CAPABILITY_CHUNK_FLUID.cast(defaultImpl) : null;
        }

        @Override
        public NBTTagCompound serializeNBT() {
            return defaultImpl.serializeNBT();
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            defaultImpl.deserializeNBT(nbt);
        }
    }

    public static class ChunkFluidEntryFactory implements Callable<ChunkFluidEntry> {
        @Override
        public ChunkFluidEntry call() throws Exception {
            return new ChunkFluidEntry();
        }
    }

}
