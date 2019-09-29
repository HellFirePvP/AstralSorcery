/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile.altar;

import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import hellfirepvp.astralsorcery.common.lib.StructureTypesAS;
import hellfirepvp.astralsorcery.common.structure.types.StructureType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AltarType
 * Created by HellFirePvP
 * Date: 12.08.2019 / 20:26
 */
public enum AltarType {

    DISCOVERY(ResearchProgression.BASIC_CRAFT, () -> StructureTypesAS.EMPTY, 6, 7, 8, 11, 12, 13, 16, 17, 18),
    ATTUNEMENT(ResearchProgression.ATTUNEMENT, () -> StructureTypesAS.EMPTY, 0, 4, 6, 7, 8, 11, 12, 13, 16, 17, 18, 20, 23),
    CONSTELLATION(ResearchProgression.CONSTELLATION, () -> StructureTypesAS.EMPTY, (slot) -> slot != 3 && slot != 10 && slot != 14 && slot != 22),
    RADIANCE(ResearchProgression.RADIANCE, () -> StructureTypesAS.EMPTY, (slot) -> true);

    private final ResearchProgression associatedTier;
    private final Supplier<StructureType> structureSupplier;
    private Predicate<Integer> slotValidator;

    AltarType(ResearchProgression progressTier, Supplier<StructureType> structureSupplier, int... validSlots) {
        this.associatedTier = progressTier;
        this.structureSupplier = structureSupplier;
        List<Integer> slots = new ArrayList<>();
        for (int slot : validSlots) {
            slots.add(slot);
        }
        this.slotValidator = slots::contains;
    }

    AltarType(ResearchProgression progressTier, Supplier<StructureType> structureSupplier, Predicate<Integer> slotValidator) {
        this.associatedTier = progressTier;
        this.structureSupplier = structureSupplier;
        this.slotValidator = slotValidator;
    }

    @Nonnull
    public ResearchProgression getAssociatedTier() {
        return associatedTier;
    }

    @Nonnull
    public StructureType getRequiredStructure() {
        return this.structureSupplier.get();
    }

    public boolean hasSlot(int slotId) {
        return this.slotValidator.test(slotId);
    }

    public int getStarlightCapacity() {
        return (int) (1000 * Math.pow(2, ordinal()));
    }

    public boolean isThisLEThan(AltarType type) {
        return this.ordinal() <= type.ordinal();
    }

}
