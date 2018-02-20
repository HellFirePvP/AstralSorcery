/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.config.ConfigDataAdapter;
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
import java.util.*;
import java.util.concurrent.Callable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FluidRarityRegistry
 * Created by HellFirePvP
 * Date: 30.10.2017 / 14:03
 */
public class FluidRarityRegistry implements ConfigDataAdapter<FluidRarityRegistry.FluidRarityEntry> {

    private static final ResourceLocation CAP_FLUIDENTRY_NAME = new ResourceLocation(AstralSorcery.MODID, "cap_chunk_fluid_fountain");
    public static FluidRarityRegistry INSTANCE = new FluidRarityRegistry();

    @CapabilityInject(ChunkFluidEntry.class)
    private static Capability<ChunkFluidEntry> CAPABILITY_CHUNK_FLUID = null;

    private FluidRarityRegistry() {}

    private static List<FluidRarityEntry> rarityList = new LinkedList<>();

    @Override
    public Iterable<FluidRarityRegistry.FluidRarityEntry> getDefaultDataSets() {
        List<FluidRarityEntry> entries = new LinkedList<>();

        tryAddEntry("water", 14000, Integer.MAX_VALUE, Integer.MAX_VALUE, entries);
        tryAddEntry("lava", 7500, 4000_000, 1000_000, entries);

        //AA
        tryAddEntry("crystaloil", 800, 600_000, 400_000, entries);
        tryAddEntry("empoweredoil", 200, 350_000, 150_000, entries);

        //TE
        tryAddEntry("redstone", 500, 120_000, 70_000, entries);
        tryAddEntry("glowstone", 500, 120_000, 70_000, entries);
        tryAddEntry("ender", 250, 140_000, 60_000, entries);
        tryAddEntry("pyrotheum", 200, 200_000, 120_000, entries);
        tryAddEntry("cryotheum", 200, 200_000, 120_000, entries);
        tryAddEntry("refined_oil", 600, 480_000, 400_000, entries);
        tryAddEntry("refined_fuel", 550, 450_000, 300_000, entries);

        //TiC
        tryAddEntry("iron", 900, 600_000, 350_000, entries);
        tryAddEntry("gold", 600, 400_000, 350_000, entries);
        tryAddEntry("cobalt", 80, 150_000, 150_000, entries);
        tryAddEntry("ardite", 80, 150_000, 150_000, entries);
        tryAddEntry("emerald", 30, 60_000, 90_000, entries);

        //TR
        tryAddEntry("fluidoil", 900, 500_000, 350_000, entries);
        tryAddEntry("fluidnitrodiesel", 450, 400_000, 250_000, entries);

        //IC2
        tryAddEntry("ic2uu_matter", 1, 600, 800, entries);
        tryAddEntry("ic2biomass", 600, 300_000, 200_000, entries);
        tryAddEntry("ic2biogas", 500, 250_000, 150_000, entries);

        //Wizardry (King Steve it up)
        tryAddEntry("mana", 1500, 550_000, 120_000, entries);
        tryAddEntry("nacre", 250, 150_000, 250_000, entries);

        return entries;
    }

    private void tryAddEntry(String fluidName, int rarity, int guaranteedAmt, int addRand, List<FluidRarityEntry> out) {
        out.add(new FluidRarityEntry(fluidName, rarity, guaranteedAmt, addRand));
    }

    @Override
    public String getDescription() {
        return "Defines fluid-rarities and amounts for the evershifting fountain's neromantic prime. The lower the relative rarity, the more rare the fluid. " +
                "Format: <FluidName>;<guaranteedMbAmount>;<additionalRandomMbAmount>;<rarity>";
    }

    @Override
    public String getDataFileName() {
        return "fluid_rarities";
    }

    @Nullable
    @Override
    public Optional<FluidRarityEntry> appendDataSet(String str) {
        FluidRarityEntry entry = FluidRarityEntry.deserialize(str);
        if(entry == null) {
            return Optional.empty();
        }
        rarityList.add(entry);
        return Optional.of(entry);
    }

    @Nullable
    private static FluidRarityEntry selectFluidEntry(Random random) {
        FluidRarityEntry entry = WeightedRandom.getRandomItem(random, rarityList);
        if(entry.fluid == null || entry.fluid.equals(FluidRegistry.WATER)) {
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

    public static class FluidRarityEntry extends WeightedRandom.Item implements ConfigDataAdapter.DataSet {

        private final String fluidNameTmp;
        public final Fluid fluid;
        public final int guaranteedAmount, additionalRandomAmount, rarity;

        private FluidRarityEntry(String fluidNameTmp, int rarity, int guaranteedAmount, int additionalRandomAmount) {
            super(rarity);
            this.fluidNameTmp = fluidNameTmp;
            this.fluid = null;
            this.rarity = rarity;
            this.guaranteedAmount = guaranteedAmount;
            this.additionalRandomAmount = additionalRandomAmount;
        }

        private FluidRarityEntry(Fluid fluid, int rarity, int guaranteedAmount, int additionalRandomAmount) {
            super(rarity);
            this.fluidNameTmp = null;
            this.fluid = fluid;
            this.rarity = rarity;
            this.guaranteedAmount = guaranteedAmount;
            this.additionalRandomAmount = additionalRandomAmount;
        }

        @Nonnull
        @Override
        public String serialize() {
            StringBuilder sb = new StringBuilder();
            if(fluid == null) {
                if(fluidNameTmp != null) {
                    sb.append(fluidNameTmp);
                } else {
                    sb.append("water");
                }
            } else {
                sb.append(fluid.getName());
            }
            sb.append(";").append(guaranteedAmount).append(";").append(additionalRandomAmount).append(";").append(rarity);
            return sb.toString();
        }

        @Nullable
        public static FluidRarityEntry deserialize(String str) {
            String[] split = str.split(";");
            if(split.length != 4) {
                return null;
            }
            String fluidName = split[0];
            Fluid f = FluidRegistry.getFluid(fluidName);
            if(f == null) {
                AstralSorcery.log.info("[AstralSorcery] Ignoring fluid " + fluidName + " for rarity registry - it doesn't exist in the current environment");
                return null;
            }
            String strGAmount = split[1];
            String strRAmount = split[2];
            String strRarity = split[3];
            int guaranteed, randomAmt, rarity;
            try {
                guaranteed = Integer.parseInt(strGAmount);
                randomAmt = Integer.parseInt(strRAmount);
                rarity = Integer.parseInt(strRarity);
            } catch (NumberFormatException exc) {
                return null;
            }
            return new FluidRarityEntry(f, rarity, guaranteed, randomAmt);
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

        public int getMbRemaining() {
            if(!isValid()) {
                return 0;
            }
            return mbRemaining;
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
