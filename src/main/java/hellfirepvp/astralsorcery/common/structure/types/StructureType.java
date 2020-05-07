/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.structure.types;

import hellfirepvp.observerlib.api.ChangeSubscriber;
import hellfirepvp.observerlib.api.ObserverHelper;
import hellfirepvp.observerlib.api.util.BlockArray;
import hellfirepvp.observerlib.common.change.ChangeObserverStructure;
import hellfirepvp.observerlib.common.change.ObserverProviderStructure;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureType
 * Created by HellFirePvP
 * Date: 30.05.2019 / 15:07
 */
public class StructureType implements IForgeRegistryEntry<StructureType> {

    private final ResourceLocation name;
    private final Supplier<BlockArray> structureSupplier;

    public StructureType(ResourceLocation name, Supplier<BlockArray> structureSupplier) {
        this.name = name;
        this.structureSupplier = structureSupplier;
    }

    public BlockArray getStructure() {
        return this.structureSupplier.get();
    }

    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(String.format("structure.%s.%s.name", name.getNamespace(), name.getPath()));
    }

    public ChangeSubscriber<ChangeObserverStructure> observe(World world, BlockPos pos) {
        return ObserverHelper.getHelper().observeArea(world, pos, new ObserverProviderStructure(getRegistryName()));
    }

    @Override
    public final StructureType setRegistryName(ResourceLocation name) {
        return this;
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return this.name;
    }

    @Override
    public Class<StructureType> getRegistryType() {
        return StructureType.class;
    }
}
