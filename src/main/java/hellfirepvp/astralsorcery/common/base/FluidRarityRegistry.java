/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import net.minecraft.util.WeightedRandom;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FluidRarityRegistry
 * Created by HellFirePvP
 * Date: 30.10.2017 / 14:03
 */
public class FluidRarityRegistry {

    private static List<FluidEntry> rarityList = new LinkedList<>();

    public static void init() {
        rarityList.add(FluidEntry.EMPTY);

        registerFluidRarity("lava", 7500, 4000_000, 1000_000);
        registerFluidRarity("astralsorcery.liquidStarlight", 4000, 2000_000, 400_000);

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
            rarityList.add(new FluidEntry(f, rarity, guaraneedAmt, additionalAmt));
        } else {
            AstralSorcery.log.info("Ignoring fluid " + name + " for rarity registry - it doesn't exist in the current environment");
        }
    }

    @Nullable
    public static FluidEntry selectFluidEntry(Random random) {
        FluidEntry entry = WeightedRandom.getRandomItem(random, rarityList);
        if(entry == FluidEntry.EMPTY || entry.fluid == null) {
            return null;
        }
        return entry;
    }

    public static class FluidEntry extends WeightedRandom.Item {

        public static final FluidEntry EMPTY = new FluidEntry(null, 14000, 0, 0);

        public final Fluid fluid;
        public final int guaranteedAmount, additionalRandomAmount, rarity;

        public FluidEntry(Fluid fluid, int rarity, int guaranteedAmount, int additionalRandomAmount) {
            super(rarity);
            this.fluid = fluid;
            this.rarity = rarity;
            this.guaranteedAmount = guaranteedAmount;
            this.additionalRandomAmount = additionalRandomAmount;
        }

    }

}
