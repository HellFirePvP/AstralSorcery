/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.assets;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.datagen.MultiLayerModelBuilder;
import hellfirepvp.astralsorcery.client.datagen.RotatableMultiLayerModelBuilder;
import hellfirepvp.astralsorcery.client.datagen.StateMultiLayerModelBuilder;
import hellfirepvp.astralsorcery.common.block.ColumnBlock;
import hellfirepvp.astralsorcery.common.block.PillarBlock;
import hellfirepvp.astralsorcery.common.block.tile.*;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.FluidsAS;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;

import static hellfirepvp.astralsorcery.common.util.NameUtil.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AstralBlockStateProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AstralBlockStateProvider extends BlockStateProvider {

    public AstralBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, AstralSorcery.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        this.simpleBlockState(BlocksAS.MARBLE_ARCH);
        this.simpleBlockState(BlocksAS.MARBLE_BRICKS);
        this.simpleBlockState(BlocksAS.MARBLE_CHISELED);
        this.simpleBlockState(BlocksAS.MARBLE_ENGRAVED);
        this.pillarModel(BlocksAS.MARBLE_PILLAR);
        this.simpleBlockState(BlocksAS.MARBLE_RAW);
        this.simpleBlockState(BlocksAS.MARBLE_RUNED);
        this.simpleSlabs(BlocksAS.MARBLE_SLAB, model(BlocksAS.MARBLE_BRICKS));
        this.simpleStairs(BlocksAS.MARBLE_STAIRS);
        this.simpleBlockState(FluidsAS.LIQUID_STARLIGHT.getFluidBlock());

        this.simpleBlockState(BlocksAS.SOOTY_MARBLE_ARCH);
        this.simpleBlockState(BlocksAS.SOOTY_MARBLE_BRICKS);
        this.simpleBlockState(BlocksAS.SOOTY_MARBLE_CHISELED);
        this.simpleBlockState(BlocksAS.SOOTY_MARBLE_ENGRAVED);
        this.pillarModel(BlocksAS.SOOTY_MARBLE_PILLAR);
        this.simpleBlockState(BlocksAS.SOOTY_MARBLE_RAW);
        this.simpleBlockState(BlocksAS.SOOTY_MARBLE_RUNED);
        this.simpleSlabs(BlocksAS.SOOTY_MARBLE_SLAB, model(BlocksAS.SOOTY_MARBLE_BRICKS));
        this.simpleStairs(BlocksAS.SOOTY_MARBLE_STAIRS);

        this.simpleBlockState(BlocksAS.INFUSED_WOOD_RAW);
        this.simpleBlockState(BlocksAS.INFUSED_WOOD_ARCH);
        this.columnModel(BlocksAS.INFUSED_WOOD_COLUMN);
        this.simpleBlockState(BlocksAS.INFUSED_WOOD_PLANKS);
        this.simpleBlockState(BlocksAS.INFUSED_WOOD_ENGRAVED);
        this.simpleBlockState(BlocksAS.INFUSED_WOOD_ENRICHED);
        this.simpleBlockState(BlocksAS.INFUSED_WOOD_INFUSED);
        this.simpleSlabs(BlocksAS.INFUSED_WOOD_SLAB, model(BlocksAS.INFUSED_WOOD_PLANKS));
        this.simpleStairs(BlocksAS.INFUSED_WOOD_STAIRS);

        this.simpleBlockState(BlocksAS.AQUAMARINE_SHALE);
        this.simpleBlockState(BlocksAS.ROCK_CRYSTAL_ORE);
        this.simpleBlockState(BlocksAS.STARMETAL_ORE);
        this.simpleBlockState(BlocksAS.RAW_STARMETAL_BLOCK);

        this.simpleBlockState(BlocksAS.GLIMMER_AMARANTH);
        this.simpleBlockState(BlocksAS.HYACINTH);
        this.simpleBlockState(BlocksAS.IRIS);
        this.simpleBlockState(BlocksAS.ORCHID);
        this.simpleBlockState(BlocksAS.PROTEA);
        this.simpleBlockState(BlocksAS.THISTLE);
        this.simpleBlockState(BlocksAS.POTTED_GLIMMER_AMARANTH);
        this.simpleBlockState(BlocksAS.POTTED_HYACINTH);
        this.simpleBlockState(BlocksAS.POTTED_IRIS);
        this.simpleBlockState(BlocksAS.POTTED_ORCHID);
        this.simpleBlockState(BlocksAS.POTTED_PROTEA);
        this.simpleBlockState(BlocksAS.POTTED_THISTLE);

        this.simpleBlockState(BlocksAS.ALTAR_ILLUMINATION);
        this.simpleBlockState(BlocksAS.ALTAR_RESONANCE);
        this.simpleBlockState(BlocksAS.ALTAR_LUMINANCE);
        this.simpleBlockState(BlocksAS.ALTAR_RADIANCE);

        this.multiLayer(BlocksAS.FOCUS_RELAY, RenderType.solid(), RenderType.translucent());
        this.stateMultiLayer(BlocksAS.CELESTIAL_CRYSTAL_CLUSTER, CelestialCrystalClusterBlock.STAGE, RenderType.solid(), RenderType.translucent());
        this.stateMultiLayer(BlocksAS.GEM_CRYSTAL_CLUSTER, GemCrystalClusterBlock.STAGE, RenderType.solid(), RenderType.translucent());
        this.horizontalStateMultiLayer(BlocksAS.LUMEN_CRYSTAL_CLUSTER, LumenCrystalClusterBlock.FACING, LumenCrystalClusterBlock.STAGE, RenderType.solid(), RenderType.translucent());

        this.multiLayer(BlocksAS.LUMEN_ARRAY, RenderType.solid(), RenderType.translucent());
        this.multiLayer(BlocksAS.LUMEN_ALCHEMY_ARRAY, RenderType.solid(), RenderType.translucent());
        this.simpleBlockState(BlocksAS.LUMEN_FILAMENT);
        this.simpleBlockState(BlocksAS.LUMEN_CRYSTALLIZER);
        this.simpleBlockState(BlocksAS.LIGHTWELL);
        this.multiLayer(BlocksAS.INFUSER, RenderType.solid(), RenderType.translucent());
        this.simpleBlockState(BlocksAS.CHALICE);
        this.simpleBlockState(BlocksAS.ATTUNEMENT_ALTAR);
        this.emptyModel(BlocksAS.TRANSLUCENT_BLOCK);
        this.emptyModel(BlocksAS.TRANSLUCENT_TREE);
        this.emptyModel(BlocksAS.FLARE_LIGHT);
        this.simpleBlockState(BlocksAS.TREE_BEACON);
        this.multiLayer(BlocksAS.CELESTIAL_GATEWAY, RenderType.solid(), RenderType.translucent());

        this.getVariantBuilder(BlocksAS.LENS.get())
                .partialState().with(LensBlock.PLACED_AGAINST, Direction.UP)
                .addModels(new ConfiguredModel(model(AstralSorcery.key("lens")), 180, 0, false))
                .partialState().with(LensBlock.PLACED_AGAINST, Direction.DOWN)
                .addModels(new ConfiguredModel(model(AstralSorcery.key("lens")), 0, 0, false))
                .partialState().with(LensBlock.PLACED_AGAINST, Direction.NORTH)
                .addModels(new ConfiguredModel(model(AstralSorcery.key("lens")), 90, 180, false))
                .partialState().with(LensBlock.PLACED_AGAINST, Direction.SOUTH)
                .addModels(new ConfiguredModel(model(AstralSorcery.key("lens")), 90, 0, false))
                .partialState().with(LensBlock.PLACED_AGAINST, Direction.EAST)
                .addModels(new ConfiguredModel(model(AstralSorcery.key("lens")), 90, 270, false))
                .partialState().with(LensBlock.PLACED_AGAINST, Direction.WEST)
                .addModels(new ConfiguredModel(model(AstralSorcery.key("lens")), 90, 90, false));

        this.getMultipartBuilder(BlocksAS.PRISM.get())
                .part().modelFile(multilayerModel(AstralSorcery.key("prism"), RenderType.solid())).rotationX(180).addModel().condition(PrismBlock.PLACED_AGAINST, Direction.UP).end()
                .part().modelFile(multilayerModel(AstralSorcery.key("prism"), RenderType.translucent())).rotationX(180).addModel().condition(PrismBlock.PLACED_AGAINST, Direction.UP).end()
                .part().modelFile(multilayerModel(AstralSorcery.key("prism"), RenderType.solid())).addModel().condition(PrismBlock.PLACED_AGAINST, Direction.DOWN).end()
                .part().modelFile(multilayerModel(AstralSorcery.key("prism"), RenderType.translucent())).addModel().condition(PrismBlock.PLACED_AGAINST, Direction.DOWN).end()
                .part().modelFile(multilayerModel(AstralSorcery.key("prism"), RenderType.solid())).rotationX(90).rotationY(180).addModel().condition(PrismBlock.PLACED_AGAINST, Direction.NORTH).end()
                .part().modelFile(multilayerModel(AstralSorcery.key("prism"), RenderType.translucent())).rotationX(90).rotationY(180).addModel().condition(PrismBlock.PLACED_AGAINST, Direction.NORTH).end()
                .part().modelFile(multilayerModel(AstralSorcery.key("prism"), RenderType.solid())).rotationX(90).addModel().condition(PrismBlock.PLACED_AGAINST, Direction.SOUTH).end()
                .part().modelFile(multilayerModel(AstralSorcery.key("prism"), RenderType.translucent())).rotationX(90).addModel().condition(PrismBlock.PLACED_AGAINST, Direction.SOUTH).end()
                .part().modelFile(multilayerModel(AstralSorcery.key("prism"), RenderType.solid())).rotationX(90).rotationY(270).addModel().condition(PrismBlock.PLACED_AGAINST, Direction.EAST).end()
                .part().modelFile(multilayerModel(AstralSorcery.key("prism"), RenderType.translucent())).rotationX(90).rotationY(270).addModel().condition(PrismBlock.PLACED_AGAINST, Direction.EAST).end()
                .part().modelFile(multilayerModel(AstralSorcery.key("prism"), RenderType.solid())).rotationX(90).rotationY(90).addModel().condition(PrismBlock.PLACED_AGAINST, Direction.WEST).end()
                .part().modelFile(multilayerModel(AstralSorcery.key("prism"), RenderType.translucent())).rotationX(90).rotationY(90).addModel().condition(PrismBlock.PLACED_AGAINST, Direction.WEST).end();


        this.multiLayer(BlocksAS.STARLIGHT_FOCUS_ROCK_CRYSTAL, RenderType.solid(), RenderType.translucent());
        this.multiLayer(BlocksAS.STARLIGHT_FOCUS_CELESTIAL_CRYSTAL, RenderType.solid(), RenderType.translucent());
        this.multiLayer(BlocksAS.STELLAR_FILAMENT, RenderType.solid(), RenderType.translucent());

        this.multiLayer(BlocksAS.CAVE_ILLUMINATOR, RenderType.solid(), RenderType.translucent());
    }

    private void pillarModel(DeferredBlock<? extends PillarBlock> b) {
        ResourceLocation key = b.getId();
        this.getVariantBuilder(b.get())
                .partialState().with(PillarBlock.PILLAR_TYPE, PillarBlock.PillarType.MIDDLE)
                .addModels(new ConfiguredModel(model(key)))
                .partialState().with(PillarBlock.PILLAR_TYPE, PillarBlock.PillarType.TOP)
                .addModels(new ConfiguredModel(model(suffixPath(key, "_top"))))
                .partialState().with(PillarBlock.PILLAR_TYPE, PillarBlock.PillarType.BOTTOM)
                .addModels(new ConfiguredModel(model(suffixPath(key, "_bottom"))));
    }

    private void columnModel(DeferredBlock<? extends ColumnBlock> b) {
        ResourceLocation key = b.getId();
        this.getVariantBuilder(b.get())
                .partialState().with(ColumnBlock.COLUMN_TYPE, ColumnBlock.ColumnType.MIDDLE)
                .addModels(new ConfiguredModel(model(key)))
                .partialState().with(ColumnBlock.COLUMN_TYPE, ColumnBlock.ColumnType.TOP)
                .addModels(new ConfiguredModel(model(suffixPath(key, "_top"))))
                .partialState().with(ColumnBlock.COLUMN_TYPE, ColumnBlock.ColumnType.BOTTOM)
                .addModels(new ConfiguredModel(model(suffixPath(key, "_bottom"))));
    }

    private void simpleSlabs(DeferredBlock<? extends SlabBlock> b, ModelFile doubleSlabModel) {
        ResourceLocation key = b.getId();
        this.slabBlock(b.get(), model(key), model(suffixPath(key, "_top")), doubleSlabModel);
    }

    private void simpleStairs(DeferredBlock<? extends StairBlock> b) {
        ResourceLocation key = b.getId();
        this.stairsBlock(b.get(), model(key), model(suffixPath(key, "_inner")), model(suffixPath(key, "_outer")));
    }

    private void simpleBlockState(DeferredBlock<?> b) {
        this.simpleBlockState(b.get(), model(b));
    }

    private void simpleBlockState(Block b, ModelFile targetModel) {
        getVariantBuilder(b).partialState().addModels(new ConfiguredModel(targetModel));
    }

    private void multiLayer(DeferredBlock<?> b, RenderType... layers) {
        MultiLayerModelBuilder builder = MultiLayerModelBuilder.makeState(this, b);
        for (RenderType layer : layers) {
            builder.addLayer(layer);
        }
    }

    private void stateMultiLayer(DeferredBlock<?> b, Property<?> blockProperty, RenderType... layers) {
        StateMultiLayerModelBuilder<?> stateBuilder = StateMultiLayerModelBuilder.makeState(this, b, blockProperty);
        for (RenderType layer : layers) {
            stateBuilder.addLayer(layer);
        }
    }

    private void horizontalStateMultiLayer(DeferredBlock<?> b, DirectionProperty dirProperty, Property<?> blockProperty, RenderType... layers) {
        RotatableMultiLayerModelBuilder<?> stateBuilder = RotatableMultiLayerModelBuilder.makeState(this, b, dirProperty, blockProperty);
        for (RenderType layer : layers) {
            stateBuilder.addLayer(layer);
        }
    }

    private void emptyModel(DeferredBlock<?> b) {
        this.getVariantBuilder(b.get()).partialState().addModels(new ConfiguredModel(modelNothing()));
    }

    public static ModelFile modelNothing() {
        return model(AstralSorcery.key("_custom/nothing"));
    }

    public static ModelFile model(DeferredHolder<?, ?> entry) {
        return model(entry.getId());
    }

    public static ModelFile model(ResourceLocation name) {
        return new ModelFile.UncheckedModelFile(prefixPath(name, "block/"));
    }

    public static ModelFile multilayerModel(DeferredHolder<?, ?> entry, RenderType type) {
        return multilayerModel(entry.getId(), type);
    }

    public static ModelFile multilayerModel(ResourceLocation name, RenderType type) {
        return new ModelFile.UncheckedModelFile(suffixPath(prefixPath(prefixPath(name, name.getPath() + "/"), "block/"), "_" + type.name));
    }
}
