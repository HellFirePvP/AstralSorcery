/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.datagen;

import hellfirepvp.astralsorcery.datagen.assets.AstralBlockStateProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;
import net.neoforged.neoforge.registries.DeferredBlock;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: StateMultiLayerModelBuilder
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class StateMultiLayerModelBuilder<T extends Comparable<T>> {

    private final DeferredBlock<?> block;
    private final Property<T> blockProperty;
    private final MultiPartBlockStateBuilder builder;

    protected StateMultiLayerModelBuilder(BlockStateProvider provider, DeferredBlock<?> block, Property<T> blockProperty) {
        this.block = block;
        this.blockProperty = blockProperty;
        this.builder = provider.getMultipartBuilder(this.block.get());
    }

    public static <T extends Comparable<T>> StateMultiLayerModelBuilder<T> makeState(BlockStateProvider provider, DeferredBlock<?> block, Property<T> blockProperty) {
        return new StateMultiLayerModelBuilder<>(provider, block, blockProperty);
    }

    public void addLayer(RenderType layer) {
        ResourceLocation id = this.block.getId();
        for (T value : this.blockProperty.getPossibleValues()) {
            ResourceLocation modelPath = id.withSuffix("/" + id.getPath() + "_" + value + "_" + layer.name);
            this.builder.part()
                    .modelFile(AstralBlockStateProvider.model(modelPath))
                    .addModel()
                    .condition(this.blockProperty, value);

        }
    }
}
