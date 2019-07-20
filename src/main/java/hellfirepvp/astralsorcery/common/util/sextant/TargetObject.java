/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.sextant;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.resource.query.TextureQuery;
import hellfirepvp.astralsorcery.client.resource.query.TextureSubQuery;
import hellfirepvp.astralsorcery.common.data.config.entry.ToolsConfig;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.structure.types.StructureType;
import hellfirepvp.astralsorcery.common.util.StructureFinder;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TargetObject
 * Created by HellFirePvP
 * Date: 02.06.2019 / 10:51
 */
public abstract class TargetObject extends ForgeRegistryEntry<TargetObject> {

    public abstract boolean isSelectable(ItemStack stack, @Nullable PlayerProgress progress);

    @Nonnull
    @OnlyIn(Dist.CLIENT)
    public abstract AbstractRenderableTexture getRenderable();

    @OnlyIn(Dist.CLIENT)
    public abstract int getColorTheme();

    @Nullable
    public abstract BlockPos searchFor(ServerWorld world, BlockPos searchPos);

    public int getSearchRadius() {
        return ToolsConfig.CONFIG.sextantSearchRadius.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !getClass().isAssignableFrom(o.getClass())) return false;
        TargetObject that = (TargetObject) o;
        return Objects.equals(getRegistryName(), that.getRegistryName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRegistryName());
    }

    public static abstract class ASTargetObject extends TargetObject {

        private final TextureQuery query;
        private final boolean advanced;
        private final String name;
        private final int color;

        public ASTargetObject(AssetLoader.TextureLocation texLocation, String iconName, String targetName, boolean advanced, int color) {
            this(texLocation, iconName, targetName, advanced, color, 0, 0, 1, 1);
        }

        public ASTargetObject(AssetLoader.TextureLocation texLocation, String iconName, String targetName, boolean advanced, int color, double iconUOffset, double iconVOffset, double iconULength, double iconVLength) {
            this.query = new TextureSubQuery(texLocation, iconName, iconUOffset, iconVOffset, iconULength, iconVLength);
            this.advanced = advanced;
            this.name = targetName;
            this.color = color;
            this.setRegistryName(new ResourceLocation(AstralSorcery.MODID, this.name));
        }

        @Override
        public boolean isSelectable(ItemStack stack, @Nullable PlayerProgress progress) {
            if (progress == null) return false;
            return (!advanced && progress.getTierReached().isThisLaterOrEqual(ProgressionTier.BASIC_CRAFT));
            //TODO sextant
            //|| (ItemSextant.isAdvanced(stack) && progress.getTierReached().isThisLaterOrEqual(ProgressionTier.CONSTELLATION_CRAFT));
        }

        @Nonnull
        @Override
        @OnlyIn(Dist.CLIENT)
        public AbstractRenderableTexture getRenderable() {
            return query.resolve();
        }

        @Override
        public int getColorTheme() {
            return this.color;
        }

        public boolean isAdvanced() {
            return this.advanced;
        }

    }

    public static class ASStructure extends ASTargetObject {

        private final StructureType structureType;

        public ASStructure(AssetLoader.TextureLocation texLocation, String iconName, String targetName, int color, boolean advanced, StructureType type) {
            super(texLocation, iconName, targetName, advanced, color);
            this.structureType = type;
        }

        public ASStructure(AssetLoader.TextureLocation texLocation, String iconName, String targetName, int color, boolean advanced, StructureType type,
                           double iconUOffset, double iconVOffset, double iconULength, double iconVLength) {
            super(texLocation, iconName, targetName, advanced, color, iconUOffset, iconVOffset, iconULength, iconVLength);
            this.structureType = type;
        }

        @Nullable
        @Override
        public BlockPos searchFor(ServerWorld world, BlockPos searchPos) {
            return StructureFinder.tryFindClosestAstralSorceryStructure(world, searchPos, structureType, this.getSearchRadius());
        }
    }

    public static class VanillaStructure extends ASTargetObject {

        private final Structure<?> structure;

        public VanillaStructure(AssetLoader.TextureLocation texLocation, String iconName, Structure<?> structure, int color, boolean advanced) {
            super(texLocation, iconName, structure.getRegistryName().getPath(), advanced, color);
            this.structure = structure;
        }

        public VanillaStructure(AssetLoader.TextureLocation texLocation, String iconName, Structure<?> structure, int color, boolean advanced,
                         double iconUOffset, double iconVOffset, double iconULength, double iconVLength) {
            super(texLocation, iconName, structure.getRegistryName().getPath(), advanced, color, iconUOffset, iconVOffset, iconULength, iconVLength);
            this.structure = structure;
        }

        @Nullable
        @Override
        public BlockPos searchFor(ServerWorld world, BlockPos searchPos) {
            return StructureFinder.tryFindClosestVanillaStructure(world, searchPos, structure, this.getSearchRadius());
        }
    }

    public static class Biome extends ASTargetObject {

        private final BiomeDictionary.Type biomeType;

        public Biome(AssetLoader.TextureLocation texLocation, String iconName, String targetName, int color, boolean advanced, BiomeDictionary.Type biomeType) {
            super(texLocation, iconName, targetName, advanced, color);
            this.biomeType = biomeType;
        }

        public Biome(AssetLoader.TextureLocation texLocation, String iconName, String targetName, int color, boolean advanced, BiomeDictionary.Type biomeType,
                     double iconUOffset, double iconVOffset, double iconULength, double iconVLength) {
            super(texLocation, iconName, targetName, advanced, color, iconUOffset, iconVOffset, iconULength, iconVLength);
            this.biomeType = biomeType;
        }

        @Nullable
        @Override
        public BlockPos searchFor(ServerWorld world, BlockPos searchPos) {
            return StructureFinder.tryFindClosestBiomeType(world, searchPos, biomeType, this.getSearchRadius());
        }

    }

}
