/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crystal;

import hellfirepvp.astralsorcery.common.crystal.source.Ritual;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.crystal.IndependentCrystalSource;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.tile.network.StarlightReceiverRitualPedestal;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import static hellfirepvp.astralsorcery.common.lib.CrystalPropertiesAS.Properties.PROPERTY_SIZE;
import static hellfirepvp.astralsorcery.common.lib.CrystalPropertiesAS.Sources.*;
import static hellfirepvp.astralsorcery.common.lib.CrystalPropertiesAS.Usages.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CrystalCalculations
 * Created by HellFirePvP
 * Date: 30.01.2019 / 08:29
 */
public class CrystalCalculations {

    private static final float TICKS_PER_HOUR = 60 * 60 * 20; // 72_000

    private CrystalCalculations() {}

    public static double calculate(double value, CrystalAttributes attributes, CalculationContext context) {
        for (CrystalAttributes.Attribute attribute : attributes.getCrystalAttributes()) {
            value = attribute.getProperty().modify(value, attribute.getTier(), context);
        }
        return value;
    }

    public static float getRitualCrystalFractureChance(int executionTimes, int nonFracturingExecutionTimes) {
        int fractureExecutions = executionTimes - nonFracturingExecutionTimes;
        if (fractureExecutions <= 0) {
            return 0F;
        }
        return Math.max(1E-6F, fractureExecutions / TICKS_PER_HOUR);
    }

    // Range: 1.0 - 5.39
    public static double getRitualEffectCapacityFactor(StarlightReceiverRitualPedestal pedestal,
                                                       CrystalAttributes attributes) {
        CalculationContext ctx = CalculationContext.Builder.newBuilder()
                .fromSource(SOURCE_RITUAL_PEDESTAL.createInstance(pedestal))
                .addUsage(USE_RITUAL_CAPACITY)
                .build();

        return calculate(1.0, attributes, ctx);
    }

    // Range: 1.0 - 2.73
    public static double getRitualEffectRangeFactor(StarlightReceiverRitualPedestal pedestal, CrystalAttributes attributes) {
        return getRitualEffectRangeFactor(SOURCE_RITUAL_PEDESTAL.createInstance(pedestal), attributes);
    }

    public static double getRitualEffectRangeFactor(TileRitualPedestal pedestal, CrystalAttributes attributes) {
        return getRitualEffectRangeFactor(SOURCE_TILE_RITUAL_PEDESTAL.createInstance(pedestal), attributes);
    }

    private static double getRitualEffectRangeFactor(Ritual pedestalSrc, CrystalAttributes attributes) {
        CalculationContext ctx = CalculationContext.Builder.newBuilder()
                .fromSource(pedestalSrc)
                .addUsage(USE_RITUAL_RANGE)
                .build();
        return calculate(1.0F, attributes, ctx);
    }

    // Range: 1.0 - 7.53375
    public static double getRitualCostReductionFactor(StarlightReceiverRitualPedestal pedestal,
                                                      CrystalAttributes attributes) {
        CalculationContext ctx = CalculationContext.Builder.newBuilder()
                .fromSource(SOURCE_RITUAL_PEDESTAL.createInstance(pedestal))
                .addUsage(USE_RITUAL_EFFECT)
                .build();

        return 1.0D / calculate(1.0, attributes, ctx);
    }

    // Range: 1 - 7
    public static int getSizeCraftingAmount(CrystalAttributes attributes) {
        int amt = 1;
        CrystalAttributes.Attribute sizeAttr = attributes.getAttribute(PROPERTY_SIZE);
        if (sizeAttr != null) {
            amt += 2 * sizeAttr.getTier();
        }
        return amt;
    }

    // Range: 1.0 - 11.0131125
    public static float getCollectorCrystalCollectionRate(IndependentCrystalSource collectorSource) {
        CalculationContext ctx = CalculationContext.Builder.newBuilder()
                .fromSource(SOURCE_COLLECTOR_CRYSTAL.createInstance(collectorSource))
                .addUsage(USE_COLLECTOR_CRYSTAL)
                .build();
        CrystalAttributes attr = collectorSource.getCrystalAttributes();
        return (float) calculate(1.0, attr, ctx);
    }

    // Range: 1.0 - 8.471625
    public static float getCrystalCollectionRate(CrystalAttributes attributes) {
        CalculationContext ctx = CalculationContext.Builder.newBuilder()
                .addUsage(USE_COLLECTOR_CRYSTAL)
                .build();
        return (float) calculate(1.0, attributes, ctx);
    }

    // Range: 0.75 - 1.0
    public static float getThroughputMultiplier(CrystalAttributes attributes) {
        CalculationContext ctx = CalculationContext.Builder.newBuilder()
                .addUsage(USE_LENS_TRANSFER)
                .build();
        return MathHelper.clamp((float) calculate(1F, attributes, ctx), 0F, 1F);
    }

    // Range: 1.0 - 11.56 (Multiplier)
    public static int getToolDurability(int durability, ItemStack tool) {
        if (tool.getItem() instanceof CrystalAttributeItem) {
            CrystalAttributes attr = ((CrystalAttributeItem) tool.getItem()).getAttributes(tool);
            if (attr != null) {
                CalculationContext ctx = CalculationContext.Builder.newBuilder()
                        .addUsage(USE_TOOL_DURABILITY)
                        .build();
                durability = (int) Math.round(durability * calculate(1.0, attr, ctx));
            }
        }
        return durability;
    }

    // Range: 1.0 - 3.895 (Multiplier)
    // Current speed value: 16 (-> 16 - 62.32)
    // Current damage value: 5.5 (-> 5.5 - 21.4225)
    public static float getToolEfficiency(float efficiency, ItemStack tool) {
        if (tool.getItem() instanceof CrystalAttributeItem) {
            CrystalAttributes attr = ((CrystalAttributeItem) tool.getItem()).getAttributes(tool);
            if (attr != null) {
                CalculationContext ctx = CalculationContext.Builder.newBuilder()
                        .addUsage(USE_TOOL_EFFECTIVENESS)
                        .build();
                efficiency *= calculate(1.0, attr, ctx);
            }
        }
        return efficiency;
    }

}
