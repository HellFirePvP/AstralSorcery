/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.assets;

import com.google.common.collect.Iterables;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.block.blackmarble.BlockBlackMarblePillar;
import hellfirepvp.astralsorcery.common.block.infusedwood.BlockInfusedWoodColumn;
import hellfirepvp.astralsorcery.common.block.marble.BlockMarblePillar;
import hellfirepvp.astralsorcery.common.block.tile.BlockLens;
import hellfirepvp.astralsorcery.common.block.tile.BlockPrism;
import hellfirepvp.astralsorcery.common.block.tile.BlockStructural;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.IProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Collection;

import static hellfirepvp.astralsorcery.common.util.NameUtil.prefixPath;
import static hellfirepvp.astralsorcery.common.util.NameUtil.suffixPath;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AstralBlockStateMappingProvider
 * Created by HellFirePvP
 * Date: 09.03.2020 / 20:13
 */
public class AstralBlockStateMappingProvider extends BlockStateProvider {

    public AstralBlockStateMappingProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, AstralSorcery.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        this.simpleBlockState(BlocksAS.FLUID_LIQUID_STARLIGHT);

        this.simpleBlockState(BlocksAS.MARBLE_ARCH);
        this.simpleBlockState(BlocksAS.MARBLE_BRICKS);
        this.simpleBlockState(BlocksAS.MARBLE_CHISELED);
        this.simpleBlockState(BlocksAS.MARBLE_ENGRAVED);
        this.pillarModel(BlocksAS.MARBLE_PILLAR, BlockMarblePillar.PILLAR_TYPE,
                BlockMarblePillar.PillarType.MIDDLE,
                BlockMarblePillar.PillarType.TOP,
                BlockMarblePillar.PillarType.BOTTOM);
        this.simpleBlockState(BlocksAS.MARBLE_RAW);
        this.simpleBlockState(BlocksAS.MARBLE_RUNED);
        this.simpleSlabs(BlocksAS.MARBLE_SLAB, model(BlocksAS.MARBLE_BRICKS));
        this.simpleStairs(BlocksAS.MARBLE_STAIRS);

        this.simpleBlockState(BlocksAS.BLACK_MARBLE_ARCH);
        this.simpleBlockState(BlocksAS.BLACK_MARBLE_BRICKS);
        this.simpleBlockState(BlocksAS.BLACK_MARBLE_CHISELED);
        this.simpleBlockState(BlocksAS.BLACK_MARBLE_ENGRAVED);
        this.pillarModel(BlocksAS.BLACK_MARBLE_PILLAR, BlockBlackMarblePillar.PILLAR_TYPE,
                BlockBlackMarblePillar.PillarType.MIDDLE,
                BlockBlackMarblePillar.PillarType.TOP,
                BlockBlackMarblePillar.PillarType.BOTTOM);
        this.simpleBlockState(BlocksAS.BLACK_MARBLE_RAW);
        this.simpleBlockState(BlocksAS.BLACK_MARBLE_RUNED);
        this.simpleSlabs(BlocksAS.BLACK_MARBLE_SLAB, model(BlocksAS.BLACK_MARBLE_BRICKS));
        this.simpleStairs(BlocksAS.BLACK_MARBLE_STAIRS);

        this.simpleBlockState(BlocksAS.INFUSED_WOOD);
        this.simpleBlockState(BlocksAS.INFUSED_WOOD_ARCH);
        this.pillarModel(BlocksAS.INFUSED_WOOD_COLUMN, BlockInfusedWoodColumn.PILLAR_TYPE,
                BlockInfusedWoodColumn.PillarType.MIDDLE,
                BlockInfusedWoodColumn.PillarType.TOP,
                BlockInfusedWoodColumn.PillarType.BOTTOM);
        this.simpleBlockState(BlocksAS.INFUSED_WOOD_ENGRAVED);
        this.simpleBlockState(BlocksAS.INFUSED_WOOD_ENRICHED);
        this.simpleBlockState(BlocksAS.INFUSED_WOOD_INFUSED);
        this.simpleBlockState(BlocksAS.INFUSED_WOOD_PLANKS);
        this.simpleSlabs(BlocksAS.INFUSED_WOOD_SLAB, model(BlocksAS.INFUSED_WOOD_PLANKS));
        this.simpleStairs(BlocksAS.INFUSED_WOOD_STAIRS);

        this.multiLayerBlockState(BlocksAS.AQUAMARINE_SAND_ORE);
        this.multiLayerBlockState(BlocksAS.ROCK_CRYSTAL_ORE);
        this.multiLayerBlockState(BlocksAS.STARMETAL_ORE);
        this.simpleBlockState(BlocksAS.GLOW_FLOWER);

        this.multiLayerBlockState(BlocksAS.SPECTRAL_RELAY);
        this.simpleBlockState(BlocksAS.ALTAR_DISCOVERY);
        this.simpleBlockState(BlocksAS.ALTAR_ATTUNEMENT);
        this.simpleBlockState(BlocksAS.ALTAR_CONSTELLATION);
        this.simpleBlockState(BlocksAS.ALTAR_RADIANCE);
        this.simpleBlockState(BlocksAS.ATTUNEMENT_ALTAR);
        this.allStateSuffixMultiLayerModel(BlocksAS.CELESTIAL_CRYSTAL_CLUSTER);
        this.allStateSuffixMultiLayerModel(BlocksAS.GEM_CRYSTAL_CLUSTER);
        this.multiLayerBlockState(BlocksAS.ROCK_COLLECTOR_CRYSTAL);
        this.multiLayerBlockState(BlocksAS.CELESTIAL_COLLECTOR_CRYSTAL);
        this.getVariantBuilder(BlocksAS.LENS)
                .partialState().with(BlockLens.PLACED_AGAINST, Direction.UP)
                    .addModels(new ConfiguredModel(model(AstralSorcery.key("lens_base")), 180, 0, false))
                .partialState().with(BlockLens.PLACED_AGAINST, Direction.DOWN)
                    .addModels(new ConfiguredModel(model(AstralSorcery.key("lens_base")), 0, 0, false))
                .partialState().with(BlockLens.PLACED_AGAINST, Direction.NORTH)
                    .addModels(new ConfiguredModel(model(AstralSorcery.key("lens_base")), 90, 180, false))
                .partialState().with(BlockLens.PLACED_AGAINST, Direction.SOUTH)
                    .addModels(new ConfiguredModel(model(AstralSorcery.key("lens_base")), 90, 0, false))
                .partialState().with(BlockLens.PLACED_AGAINST, Direction.EAST)
                    .addModels(new ConfiguredModel(model(AstralSorcery.key("lens_base")), 90, 270, false))
                .partialState().with(BlockLens.PLACED_AGAINST, Direction.WEST)
                    .addModels(new ConfiguredModel(model(AstralSorcery.key("lens_base")), 90, 90, false));

        ResourceLocation prism = BlocksAS.PRISM.getRegistryName();
        ResourceLocation prismColored = suffixPath(prism, "_colored");
        this.getMultipartBuilder(BlocksAS.PRISM)
                .part().modelFile(multiLayerModel(prism)).rotationX(180).addModel().condition(BlockPrism.PLACED_AGAINST, Direction.UP).end()
                .part().modelFile(multiLayerModel(prismColored)).rotationX(180).addModel().condition(BlockPrism.PLACED_AGAINST, Direction.UP).condition(BlockPrism.HAS_COLORED_LENS, true).end()
                .part().modelFile(multiLayerModel(prism)).addModel().condition(BlockPrism.PLACED_AGAINST, Direction.DOWN).end()
                .part().modelFile(multiLayerModel(prismColored)).addModel().condition(BlockPrism.PLACED_AGAINST, Direction.DOWN).condition(BlockPrism.HAS_COLORED_LENS, true).end()
                .part().modelFile(multiLayerModel(prism)).rotationX(90).rotationY(180).addModel().condition(BlockPrism.PLACED_AGAINST, Direction.NORTH).end()
                .part().modelFile(multiLayerModel(prismColored)).rotationX(90).rotationY(180).addModel().condition(BlockPrism.PLACED_AGAINST, Direction.NORTH).condition(BlockPrism.HAS_COLORED_LENS, true).end()
                .part().modelFile(multiLayerModel(prism)).rotationX(90).addModel().condition(BlockPrism.PLACED_AGAINST, Direction.SOUTH).end()
                .part().modelFile(multiLayerModel(prismColored)).rotationX(90).addModel().condition(BlockPrism.PLACED_AGAINST, Direction.SOUTH).condition(BlockPrism.HAS_COLORED_LENS, true).end()
                .part().modelFile(multiLayerModel(prism)).rotationX(90).rotationY(270).addModel().condition(BlockPrism.PLACED_AGAINST, Direction.EAST).end()
                .part().modelFile(multiLayerModel(prismColored)).rotationX(90).rotationY(270).addModel().condition(BlockPrism.PLACED_AGAINST, Direction.EAST).condition(BlockPrism.HAS_COLORED_LENS, true).end()
                .part().modelFile(multiLayerModel(prism)).rotationX(90).rotationY(90).addModel().condition(BlockPrism.PLACED_AGAINST, Direction.WEST).end()
                .part().modelFile(multiLayerModel(prismColored)).rotationX(90).rotationY(90).addModel().condition(BlockPrism.PLACED_AGAINST, Direction.WEST).condition(BlockPrism.HAS_COLORED_LENS, true).end();

        this.simpleBlockState(BlocksAS.REFRACTION_TABLE, this.modelAS("refraction_table_particle"));
        this.multiLayerBlockState(BlocksAS.RITUAL_LINK);
        this.multiLayerBlockState(BlocksAS.RITUAL_PEDESTAL);
        this.multiLayerBlockState(BlocksAS.ILLUMINATOR);
        this.multiLayerBlockState(BlocksAS.INFUSER);
        this.simpleBlockState(BlocksAS.CHALICE);
        this.simpleBlockState(BlocksAS.TELESCOPE);
        this.simpleBlockState(BlocksAS.OBSERVATORY);
        this.simpleBlockState(BlocksAS.WELL);

        this.getVariantBuilder(BlocksAS.FLARE_LIGHT).forAllStates(state -> ArrayUtils.toArray(new ConfiguredModel(this.modelNothing())));
        this.simpleBlockState(BlocksAS.TRANSLUCENT_BLOCK, this.modelNothing());
        this.simpleBlockState(BlocksAS.VANISHING, this.modelNothing());
        this.getVariantBuilder(BlocksAS.STRUCTURAL)
                .partialState().with(BlockStructural.BLOCK_TYPE, BlockStructural.BlockType.TELESCOPE)
                .addModels(new ConfiguredModel(model(BlocksAS.TELESCOPE)))
                .partialState().with(BlockStructural.BLOCK_TYPE, BlockStructural.BlockType.DUMMY)
                .addModels(new ConfiguredModel(modelNothing()));
    }

    private <T extends Comparable<T>> void pillarModel(Block b, IProperty<T> pillarType, T middle, T top, T bottom) {
        ResourceLocation key = b.getRegistryName();
        this.getVariantBuilder(b)
                .partialState().with(pillarType, middle)
                .addModels(new ConfiguredModel(model(key)))
                .partialState().with(pillarType, top)
                .addModels(new ConfiguredModel(model(suffixPath(key, "_top"))))
                .partialState().with(pillarType, bottom)
                .addModels(new ConfiguredModel(model(suffixPath(key, "_bottom"))));
    }

    private <T extends Comparable<T>> void allStateSuffixModel(Block b) {
        Collection<IProperty<?>> properties = b.getStateContainer().getProperties();
        if (properties.size() != 1) {
            throw new IllegalArgumentException("Can only make path-suffix enumeration for blockstates with exactly 1 property!");
        }

        ResourceLocation key = b.getRegistryName();
        IProperty<T> property = (IProperty<T>) Iterables.getFirst(properties, null);
        VariantBlockStateBuilder builder = this.getVariantBuilder(b);
        for (T value : property.getAllowedValues()) {
            builder.partialState().with(property, value)
                    .addModels(new ConfiguredModel(model(suffixPath(key, "_" + value.toString()))));
        }
    }

    private <T extends Comparable<T>> void allStateSuffixMultiLayerModel(Block b) {
        Collection<IProperty<?>> properties = b.getStateContainer().getProperties();
        if (properties.size() != 1) {
            throw new IllegalArgumentException("Can only make path-suffix enumeration for blockstates with exactly 1 property!");
        }

        ResourceLocation key = b.getRegistryName();
        IProperty<T> property = (IProperty<T>) Iterables.getFirst(properties, null);
        VariantBlockStateBuilder builder = this.getVariantBuilder(b);
        for (T value : property.getAllowedValues()) {
            builder.partialState().with(property, value)
                    .addModels(new ConfiguredModel(multiLayerModel(suffixPath(key, "_" + value.toString()))));
        }
    }

    private void simpleSlabs(SlabBlock b, ModelFile doubleSlabModel) {
        ResourceLocation key = b.getRegistryName();
        this.slabBlock(b, model(key), model(suffixPath(key, "_top")), doubleSlabModel);
    }

    private void simpleStairs(StairsBlock b) {
        ResourceLocation key = b.getRegistryName();
        this.stairsBlock(b, model(key), model(suffixPath(key, "_inner")), model(suffixPath(key, "_outer")));
    }

    private void multiLayerBlockState(Block b) {
        this.simpleBlockState(b, multiLayerModel(b.getRegistryName()));
    }

    private void simpleBlockState(Block b) {
        this.simpleBlockState(b, model(b.getRegistryName()));
    }

    private void simpleBlockState(Block b, ModelFile targetModel) {
        getVariantBuilder(b).partialState().addModels(new ConfiguredModel(targetModel));
    }

    private ModelFile modelNothing() {
        return modelAS("base/nothing");
    }

    private ModelFile modelAS(String name) {
        return model(AstralSorcery.key(name));
    }

    private ModelFile model(IForgeRegistryEntry<?> entry) {
        return model(entry.getRegistryName());
    }

    private ModelFile model(ResourceLocation name) {
        return new ModelFile.UncheckedModelFile(prefixPath(name, "block/"));
    }

    private ModelFile multiLayerModel(ResourceLocation name) {
        return new ModelFile.UncheckedModelFile(prefixPath(name, "block/multilayer/"));
    }
}
