/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crystal;

import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CrystalProperty
 * Created by HellFirePvP
 * Date: 29.01.2019 / 21:23
 */
public abstract class CrystalProperty extends ForgeRegistryEntry<CrystalProperty> implements Comparable<CrystalProperty> {

    private static int counter = 0;
    private final int sortingId;

    private ResearchProgression requiredResearch = null;
    private List<CrystalPropertyModifierFunction> modifiers = new ArrayList<>();
    private Predicate<CalculationContext> usageTests = (ctx) -> false;

    public CrystalProperty(ResourceLocation registryName) {
        this.sortingId = counter++;
        this.setRegistryName(registryName);
    }

    /**
     * Sets/Overwrites the research the player needs to obtain to see/understand this property.
     *
     * @param requiredResearch the research needed to be reached.
     * @return self
     */
    public CrystalProperty setRequiredResearch(ResearchProgression requiredResearch) {
        this.requiredResearch = requiredResearch;
        return this;
    }

    /**
     * Add a modifier to this property, influencing certain calculations depending on its source or usage-hints.
     *
     * @param modifierFunction the new modifier function
     * @return self
     */
    public CrystalProperty addModifier(CrystalPropertyModifierFunction modifierFunction) {
        this.modifiers.add(modifierFunction);
        return this;
    }

    /**
     * Add a usage hint that 'this property influences calculations matching the passed context'
     *
     * @param usage the case to add
     * @return self
     */
    public CrystalProperty addUsage(Predicate<CalculationContext> usage) {
        this.usageTests = this.usageTests.or(usage);
        return this;
    }

    public int getMaxTier() {
        return 3;
    }

    public boolean canSee(PlayerProgress progress) {
        return this.requiredResearch == null || progress.getResearchProgression().contains(this.requiredResearch);
    }

    public boolean hasUsageFor(CalculationContext ctx) {
        return this.usageTests.test(ctx);
    }

    public double modify(double value, int tier, CalculationContext context) {
        double originalValue = value;
        for (CrystalPropertyModifierFunction fn : modifiers) {
            value = fn.modify(value, originalValue, tier, context);
        }
        return value;
    }

    public ITextComponent getName(int currentTier) {
        return new TranslationTextComponent(String.format("crystal.property.%s.%s.name", getRegistryName().getNamespace(), getRegistryName().getPath()));
    }

    @Override
    public int compareTo(CrystalProperty other) {
        return this.sortingId - other.sortingId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CrystalProperty that = (CrystalProperty) o;
        return Objects.equals(this.getRegistryName(), that.getRegistryName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getRegistryName());
    }
}
