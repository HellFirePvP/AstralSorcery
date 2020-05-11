/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile.altar;

import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.StructureTypesAS;
import hellfirepvp.astralsorcery.common.structure.types.StructureType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
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

    DISCOVERY    (ResearchProgression.BASIC_CRAFT, 100,
            () -> BlocksAS.ALTAR_DISCOVERY.asItem(),
            () -> StructureTypesAS.EMPTY,
            6, 7, 8, 11, 12, 13, 16, 17, 18),
    ATTUNEMENT   (ResearchProgression.ATTUNEMENT, 200,
            () -> BlocksAS.ALTAR_ATTUNEMENT.asItem(),
            () -> StructureTypesAS.PTYPE_ALTAR_ATTUNEMENT,
            0, 4, 6, 7, 8, 11, 12, 13, 16, 17, 18, 20, 24),
    CONSTELLATION(ResearchProgression.CONSTELLATION, 400,
            () -> BlocksAS.ALTAR_CONSTELLATION.asItem(),
            () -> StructureTypesAS.PTYPE_ALTAR_CONSTELLATION,
            (slot) -> slot != 2 && slot != 10 && slot != 14 && slot != 22),
    RADIANCE     (ResearchProgression.RADIANCE, 600,
            () -> BlocksAS.ALTAR_RADIANCE.asItem(),
            () -> StructureTypesAS.PTYPE_ALTAR_TRAIT,
            (slot) -> true);

    private final ResearchProgression associatedTier;
    private final int defaultAltarCraftingDuration;
    private final Supplier<Item> altarItemSupplier;
    private final Supplier<StructureType> structureSupplier;
    private Predicate<Integer> slotValidator;

    AltarType(ResearchProgression progressTier, int defaultAltarCraftingDuration, Supplier<Item> altarItemSupplier, Supplier<StructureType> structureSupplier, int... validSlots) {
        this.associatedTier = progressTier;
        this.defaultAltarCraftingDuration = defaultAltarCraftingDuration;
        this.altarItemSupplier = altarItemSupplier;
        this.structureSupplier = structureSupplier;
        List<Integer> slots = new ArrayList<>();
        for (int slot : validSlots) {
            slots.add(slot);
        }
        this.slotValidator = slots::contains;
    }

    AltarType(ResearchProgression progressTier, int defaultAltarCraftingDuration, Supplier<Item> altarItemSupplier, Supplier<StructureType> structureSupplier, Predicate<Integer> slotValidator) {
        this.associatedTier = progressTier;
        this.defaultAltarCraftingDuration = defaultAltarCraftingDuration;
        this.altarItemSupplier = altarItemSupplier;
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

    @Nonnull
    public ItemStack getAltarItemRepresentation() {
        return new ItemStack(this.altarItemSupplier.get());
    }

    public boolean hasSlot(int slotId) {
        return this.slotValidator.test(slotId);
    }

    public int getStarlightCapacity() {
        return (int) (1000 * Math.pow(2, ordinal()));
    }

    public int getDefaultAltarCraftingDuration() {
        return defaultAltarCraftingDuration;
    }

    public boolean isThisLEThan(AltarType type) {
        return this.ordinal() <= type.ordinal();
    }

    public boolean isThisGEThan(AltarType type) {
        return this.ordinal() >= type.ordinal();
    }

}
