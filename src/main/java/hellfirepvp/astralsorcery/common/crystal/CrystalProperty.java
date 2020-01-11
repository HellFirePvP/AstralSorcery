/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crystal;

import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.Objects;

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

    protected ProgressionTier tierRequirement = null;

    public CrystalProperty(ResourceLocation registryName) {
        this.sortingId = counter++;
        this.setRegistryName(registryName);
    }

    public int getMaxTier() {
        return 3;
    }

    public boolean canSee(PlayerProgress progress) {
        return tierRequirement == null || progress.getTierReached().isThisLaterOrEqual(this.tierRequirement);
    }

    public abstract double modify(double value, int thisTier, CalculationContext context);

    public ITextComponent getName(int currentTier) {
        return new TranslationTextComponent(String.format("crystal.property.%s.%s.name", getRegistryName().getNamespace(), getRegistryName().getPath()))
                    .setStyle(new Style().setColor(TextFormatting.GRAY));
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
