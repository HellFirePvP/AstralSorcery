/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import com.google.common.base.CaseFormat;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.fluid.BlockLiquidStarlight;
import hellfirepvp.astralsorcery.common.fluid.FluidLiquidStarlight;
import hellfirepvp.astralsorcery.common.fluid.ItemLiquidStarlightBucket;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.FluidsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.util.NameUtil;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ModelDynBucket;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static hellfirepvp.astralsorcery.common.lib.FluidsAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryFluids
 * Created by HellFirePvP
 * Date: 20.09.2019 / 21:53
 */
public class RegistryFluids {

    static final List<Block> FLUID_BLOCKS = new LinkedList<>();
    static final List<Item> FLUID_HOLDER_ITEMS = new LinkedList<>();

    private RegistryFluids() {}

    public static void registerFluids() {
        makeProperties();

        LIQUID_STARLIGHT_SOURCE = registerFluid(new FluidLiquidStarlight.Source(LIQUID_STARLIGHT_PROPERTIES));
        LIQUID_STARLIGHT_FLOWING = registerFluid(new FluidLiquidStarlight.Flowing(LIQUID_STARLIGHT_PROPERTIES));

        FLUID_BLOCKS.add(BlocksAS.FLUID_LIQUID_STARLIGHT = new BlockLiquidStarlight(() -> LIQUID_STARLIGHT_SOURCE));
        FLUID_HOLDER_ITEMS.add(ItemsAS.BUCKET_LIQUID_STARLIGHT = new ItemLiquidStarlightBucket(() -> LIQUID_STARLIGHT_SOURCE));
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerFluidBucketRender(BiConsumer<IUnbakedModel, ModelResourceLocation> modelBakery) {
        makeBucketModel(ItemsAS.BUCKET_LIQUID_STARLIGHT, LIQUID_STARLIGHT_SOURCE, modelBakery);
    }

    @OnlyIn(Dist.CLIENT)
    private static void makeBucketModel(Item bucketItem, Fluid fluid, BiConsumer<IUnbakedModel, ModelResourceLocation> modelBakery) {
        ModelResourceLocation modelName = new ModelResourceLocation(bucketItem.getRegistryName(), "inventory");
        ResourceLocation vanillaBucket = new ResourceLocation("item/bucket");
        ResourceLocation fluidOverlay = AstralSorcery.key("fluid/bucket_mask");

        ModelDynBucket bucket = new ModelDynBucket(vanillaBucket, fluidOverlay, null,
                fluid, true, false);
        modelBakery.accept(bucket, modelName);
    }

    private static void makeProperties() {
        LIQUID_STARLIGHT_PROPERTIES = makeProperties(FluidLiquidStarlight.class, FluidLiquidStarlight::addAttributes,
                () -> LIQUID_STARLIGHT_SOURCE, () -> LIQUID_STARLIGHT_FLOWING)
                .block(() -> BlocksAS.FLUID_LIQUID_STARLIGHT)
                .bucket(() -> ItemsAS.BUCKET_LIQUID_STARLIGHT);
    }

    private static ForgeFlowingFluid.Properties makeProperties(Class<? extends ForgeFlowingFluid> fluidClass,
                                                               Function<FluidAttributes.Builder, FluidAttributes.Builder> postProcess,
                                                               Supplier<ForgeFlowingFluid> stillFluidSupplier,
                                                               Supplier<ForgeFlowingFluid> flowingFluidSupplier) {
        String name = NameUtil.fromClass(fluidClass, "Fluid").getPath();
        return new ForgeFlowingFluid.Properties(
                stillFluidSupplier,
                flowingFluidSupplier,
                postProcess.apply(builderFor(name)));
    }

    private static FluidAttributes.Builder builderFor(String fluidName) {
        ResourceLocation still = AstralSorcery.key("fluid/" + fluidName + "_still");
        ResourceLocation flowing = AstralSorcery.key("fluid/" + fluidName + "_flowing");
        return FluidAttributes.builder(still, flowing);
    }

    private static <T extends Fluid> T registerFluid(T fluid) {
        return registerFluid(fluid, NameUtil.fromClass(fluid, "Fluid", "Source"));
    }

    private static <T extends Fluid> T registerFluid(T fluid, ResourceLocation name) {
        fluid.setRegistryName(name);
        AstralSorcery.getProxy().getRegistryPrimer().register(fluid);
        return fluid;
    }
}
