/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crystal;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.component.CrystalAttributesComponent;
import hellfirepvp.astralsorcery.common.lib.CrystalPropertiesAS;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;

import java.util.List;
import java.util.Optional;
import java.util.Properties;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CrystalPropertyGenerator
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CrystalPropertyGenerator {

    private static final RandomSource rand = RandomSource.create();

    private CrystalPropertyGenerator() {}

    public static CrystalAttributesComponent generateRandomProperties(CrystalAttributesComponent component) {
        if (!component.getAttributes().isEmpty()) return component; // Probably wrong call and/or already generated
        int toGenerate = component.getProperties().generateCount() - component.getTotalTierCount();
        if (toGenerate <= 0) return component; // Nothing to generate

        List<WeightedEntry.Wrapper<CrystalProperty>> availableProperties = getPropertiesToGenerate();
        removeMaxProperties(component, availableProperties);
        while (!availableProperties.isEmpty() && toGenerate > 0) {
            Optional<WeightedEntry.Wrapper<CrystalProperty>> randProperty = WeightedRandom.getRandomItem(rand, availableProperties);
            if (randProperty.isEmpty()) break;
            WeightedEntry.Wrapper<CrystalProperty> property = randProperty.get();
            int existing = component.getAttribute(property.data()).map(CrystalAttributesComponent.TieredAttribute::getTier).orElse(0);
            component = component.setAttributeTier(property.data(), existing + 1);
            removeMaxProperties(component, availableProperties);
            toGenerate--;
        }
        return component;
    }

    private static void removeMaxProperties(CrystalAttributesComponent component, List<WeightedEntry.Wrapper<CrystalProperty>> properties) {
        properties.removeIf(weightedEntry -> {
            CrystalProperty prop = weightedEntry.data();
            return component.getAttribute(prop)
                    .map(tieredAttr -> tieredAttr.getTier() >= prop.getMaxTier())
                    .orElse(false);
        });
    }

    private static List<WeightedEntry.Wrapper<CrystalProperty>> getPropertiesToGenerate() {
        return Lists.newArrayList(
                new WeightedEntry.Wrapper<>(CrystalPropertiesAS.SIZE.get(), Weight.of(4)),
                new WeightedEntry.Wrapper<>(CrystalPropertiesAS.PURITY.get(), Weight.of(1)),
                new WeightedEntry.Wrapper<>(CrystalPropertiesAS.CUT.get(), Weight.of(2))
        );
    }

}
