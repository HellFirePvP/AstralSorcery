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
import hellfirepvp.astralsorcery.client.init.InitItemProperties;
import hellfirepvp.astralsorcery.common.item.block.CelestialCrystalClusterBlockItem;
import hellfirepvp.astralsorcery.common.item.block.GemCrystalClusterBlockItem;
import hellfirepvp.astralsorcery.common.item.block.LumenCrystalClusterBlockItem;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.util.NameUtil;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.loaders.DynamicFluidContainerModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.List;
import java.util.function.Function;

import static hellfirepvp.astralsorcery.common.lib.ItemsAS.*;
import static hellfirepvp.astralsorcery.common.lib.BlocksAS.*;
import static hellfirepvp.astralsorcery.common.lib.FluidsAS.*;
import static hellfirepvp.astralsorcery.common.util.NameUtil.prefixPath;
import static hellfirepvp.astralsorcery.common.util.NameUtil.suffixPath;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AstralItemModelProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AstralItemModelProvider extends ItemModelProvider {

    public AstralItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, AstralSorcery.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        InitItemProperties.init();

        this.registerBlockItemModels();
        this.registerItemModels();

        this.simpleBucket(LIQUID_STARLIGHT.getBucket(), LIQUID_STARLIGHT.getSource());
    }

    private void registerBlockItemModels() {
        this.simpleBlockModel(MARBLE_ARCH);
        this.simpleBlockModel(MARBLE_BRICKS);
        this.simpleBlockModel(MARBLE_CHISELED);
        this.simpleBlockModel(MARBLE_ENGRAVED);
        this.simpleBlockModel(MARBLE_PILLAR);
        this.simpleBlockModel(MARBLE_RAW);
        this.simpleBlockModel(MARBLE_RUNED);
        this.simpleBlockModel(MARBLE_SLAB);
        this.simpleBlockModel(MARBLE_STAIRS);

        this.simpleBlockModel(SOOTY_MARBLE_ARCH);
        this.simpleBlockModel(SOOTY_MARBLE_BRICKS);
        this.simpleBlockModel(SOOTY_MARBLE_CHISELED);
        this.simpleBlockModel(SOOTY_MARBLE_ENGRAVED);
        this.simpleBlockModel(SOOTY_MARBLE_PILLAR);
        this.simpleBlockModel(SOOTY_MARBLE_RAW);
        this.simpleBlockModel(SOOTY_MARBLE_RUNED);
        this.simpleBlockModel(SOOTY_MARBLE_SLAB);
        this.simpleBlockModel(SOOTY_MARBLE_STAIRS);

        this.simpleBlockModel(INFUSED_WOOD_RAW);
        this.simpleBlockModel(INFUSED_WOOD_ARCH);
        this.simpleBlockModel(INFUSED_WOOD_COLUMN);
        this.simpleBlockModel(INFUSED_WOOD_ENGRAVED);
        this.simpleBlockModel(INFUSED_WOOD_ENRICHED);
        this.simpleBlockModel(INFUSED_WOOD_INFUSED);
        this.simpleBlockModel(INFUSED_WOOD_PLANKS);
        this.simpleBlockModel(INFUSED_WOOD_SLAB);
        this.simpleBlockModel(INFUSED_WOOD_STAIRS);

        this.simpleBlockModel(AQUAMARINE_SHALE);
        this.simpleBlockModel(ROCK_CRYSTAL_ORE);
        this.simpleBlockModel(STARMETAL_ORE);
        this.simpleBlockModel(RAW_STARMETAL_BLOCK);

        this.basicBlockItem(GLIMMER_AMARANTH);
        this.basicBlockItem(HYACINTH);
        this.basicBlockItem(IRIS);
        this.basicBlockItem(ORCHID);
        this.basicBlockItem(PROTEA);
        this.basicBlockItem(THISTLE);

        this.simpleBlockModel(ALTAR_ILLUMINATION);
        this.simpleBlockModel(ALTAR_RESONANCE);
        this.simpleBlockModel(ALTAR_LUMINANCE);
        this.simpleBlockModel(ALTAR_RADIANCE);

        this.multiLayerAllModel(FOCUS_RELAY);
        this.overridesModel(
                CELESTIAL_CRYSTAL_CLUSTER,
                MultiLayerModelBuilder.getMultiLayerModelId(CELESTIAL_CRYSTAL_CLUSTER),
                AstralSorcery.key("stage"),
                CelestialCrystalClusterBlockItem.getVariants(),
                stack -> CelestialCrystalClusterBlockItem.getStage(stack) + "_combined");
        this.overridesModel(
                GEM_CRYSTAL_CLUSTER,
                MultiLayerModelBuilder.getMultiLayerModelId(GEM_CRYSTAL_CLUSTER),
                AstralSorcery.key("stage"),
                GemCrystalClusterBlockItem.getVariants(),
                stack -> GemCrystalClusterBlockItem.getStage(stack) + "_combined");
        this.overridesModel(
                LUMEN_CRYSTAL_CLUSTER,
                MultiLayerModelBuilder.getMultiLayerModelId(LUMEN_CRYSTAL_CLUSTER),
                AstralSorcery.key("stage"),
                LumenCrystalClusterBlockItem.getStageVariants(),
                stack -> LumenCrystalClusterBlockItem.getStage(stack) + "_combined");

        this.multiLayerAllModel(LUMEN_ARRAY);
        this.multiLayerAllModel(LUMEN_ALCHEMY_ARRAY);
        this.simpleBlockModel(LUMEN_FILAMENT);
        this.simpleBlockModel(LUMEN_CRYSTALLIZER);
        this.simpleBlockModel(LIGHTWELL);
        this.multiLayerAllModel(INFUSER);
        this.simpleBlockModel(ATTUNEMENT_ALTAR);
        this.simpleBlockModel(TREE_BEACON);
        this.multiLayerAllModel(CELESTIAL_GATEWAY);

        this.multiLayerAllModel(CAVE_ILLUMINATOR);

        this.multiLayerAllModel(PRISM);
        this.multiLayerAllModel(STARLIGHT_FOCUS_ROCK_CRYSTAL);
        this.multiLayerAllModel(STARLIGHT_FOCUS_CELESTIAL_CRYSTAL);
        this.multiLayerAllModel(STELLAR_FILAMENT);
    }

    private void registerItemModels() {
        this.basicItem(TOME);
        this.basicItem(ASTROLABE);
        this.handheldItem(WAND);
        this.handheldItem(CHISEL);
        this.handheldItem(LINKING_TOOL);
        this.layeredHandheld(ILLUMINATION_WAND,
                AstralSorcery.key("illumination_wand"),
                AstralSorcery.key("illumination_wand_overlay"));
        this.handheldItem(ARCHITECT_WAND);
        this.handheldItem(EXCHANGE_WAND);
        this.handheldItem(BLINK_WAND);
        this.handheldItem(GRAPPLING_WAND);
        ResourceLocation writtenModel = NameUtil.suffixPath(KNOWLEDGE_SHARE.getId(), "_written");
        this.basicItem(writtenModel);
        this.basicItem(KNOWLEDGE_SHARE)
                .override()
                .predicate(AstralSorcery.key("written"), 1)
                .model(model(writtenModel))
                .end();
        this.layeredBasic(CONSTELLATION_PAPER,
                AstralSorcery.key("constellation_paper"),
                AstralSorcery.key("constellation_paper_overlay"));
        ItemModelBuilder artifact = this.basicItem(ARTIFACT);
        ItemModelBuilder artifactShard = this.basicItem(ARTIFACT_SHARD);
        int typeCount = RegistriesAS.REGISTRY_ARTIFACT_TYPES.size();
        RegistriesAS.REGISTRY_ARTIFACT_TYPES.forEach(type -> {
            String typePart = RegistriesAS.REGISTRY_ARTIFACT_TYPES.getKey(type).getPath();
            float part = RegistriesAS.REGISTRY_ARTIFACT_TYPES.getId(type) / (float) typeCount;

            ResourceLocation artifactPart = NameUtil.suffixPath(ARTIFACT.getId(), "/" + typePart);
            ResourceLocation artifactPrefix = NameUtil.prefixPath(artifactPart, "item/");
            artifact.override().predicate(AstralSorcery.key("artifact_type"), part).model(model(artifactPart)).end();
            getBuilder(artifactPrefix.toString()).parent(new ModelFile.UncheckedModelFile("item/generated")).texture("layer0", artifactPrefix);

            ResourceLocation artifactShardPart = NameUtil.suffixPath(ARTIFACT_SHARD.getId(), "/" + typePart);
            ResourceLocation artifactShardPrefix = NameUtil.prefixPath(artifactShardPart, "item/");
            artifactShard.override().predicate(AstralSorcery.key("artifact_type"), part).model(model(artifactShardPart)).end();
            getBuilder(artifactShardPrefix.toString()).parent(new ModelFile.UncheckedModelFile("item/generated")).texture("layer0", artifactShardPrefix);
        });

        this.basicItem(AQUAMARINE);
        this.basicItem(GLASS_LENS);
        this.basicItem(RESONATING_GEM);
        this.basicItem(PARCHMENT);
        this.basicItem(STARDUST);
        this.basicItem(STARMETAL_INGOT);
        ResourceLocation prismaticCrystal = NameUtil.suffixPath(LUMEN_CRYSTAL.getId(), "_prismatic");
        this.basicItem(prismaticCrystal);
        this.basicItem(LUMEN_CRYSTAL)
                .override().predicate(AstralSorcery.key("prismatic"), 1F).model(model(prismaticCrystal)).end();
        this.basicItem(PERK_SEAL);
        this.basicItem(PERK_NULLIFIER);
        this.basicItem(DYNAMISM_GEM_SKY);
        this.basicItem(DYNAMISM_GEM_DAY);
        this.basicItem(DYNAMISM_GEM_NIGHT);

        this.basicItem(ILLUMINATION_POWDER);
        this.basicItem(NOCTURNAL_POWDER);
        this.basicItem(VIVID_POWDER);
        this.basicItem(SHIFTING_STAR);
        this.basicItem(SHIFTING_STAR_AEVITAS);
        this.basicItem(SHIFTING_STAR_ARMARA);
        this.basicItem(SHIFTING_STAR_DISCIDIA);
        this.basicItem(SHIFTING_STAR_EVORSIO);
        this.basicItem(SHIFTING_STAR_VICIO);
        this.basicItem(AKASHIC_SINGULARITY);

        this.basicItem(RAW_STARMETAL);

        this.basicItem(ROCK_CRYSTAL);
        this.basicItem(CELESTIAL_CRYSTAL);
        this.basicItem(ATTUNED_ROCK_CRYSTAL);
        this.basicItem(ATTUNED_CELESTIAL_CRYSTAL);

        this.handheldItem(CRYSTAL_AXE);
        this.handheldItem(CRYSTAL_PICKAXE);
        this.handheldItem(CRYSTAL_SHOVEL);
        this.handheldItem(CRYSTAL_SWORD);
        this.handheldItem(IRIDESCENT_CRYSTAL_AXE);
        this.handheldItem(IRIDESCENT_CRYSTAL_PICKAXE);
        this.handheldItem(IRIDESCENT_CRYSTAL_SHOVEL);
        this.handheldItem(IRIDESCENT_CRYSTAL_SWORD);
        this.layeredBasic(ENCHANTMENT_AMULET,
                AstralSorcery.key("enchantment_amulet"),
                AstralSorcery.key("enchantment_amulet_gem"),
                AstralSorcery.key("enchantment_amulet_shine"));

        var stardewModel = this.layeredBasic(STARDEW,
                AstralSorcery.key("stardew_flask"));
        for (int i = 0; i < 5; i++) {
            ResourceLocation stardewLayer = NameUtil.suffixPath(STARDEW.getId(), "_overlay_" + i);
            this.layeredBasic(stardewLayer, stardewLayer, AstralSorcery.key("stardew_flask"));
            stardewModel.override()
                    .predicate(AstralSorcery.key("filled"), i / 4F)
                    .model(model(stardewLayer))
                    .end();
        }
    }

    private ItemModelBuilder overridesModel(ItemLike item, ResourceLocation modelId, ResourceLocation propertyKey, List<ItemStack> stacks, Function<ItemStack, String> modelNameSuffix) {
        ItemModelBuilder model = this.getBuilder(BuiltInRegistries.ITEM.getKey(item.asItem()).toString())
                .parent(new ModelFile.UncheckedModelFile("item/generated"));

        stacks.forEach(stack -> {
            ItemPropertyFunction func = ItemProperties.getProperty(stack, propertyKey);
            if (func == null) throw new IllegalArgumentException("No property function found " + propertyKey);
            float value = func.call(stack, null, null, 0);
            ResourceLocation variantKey = suffixPath(modelId, "_" + modelNameSuffix.apply(stack));
            model.override().predicate(propertyKey, value)
                    .model(new ModelFile.UncheckedModelFile(variantKey));
        });
        return model;
    }

    private ItemModelBuilder basicBlockItem(ItemLike item) {
        ResourceLocation itemKey = BuiltInRegistries.ITEM.getKey(item.asItem());
        return getBuilder(itemKey.toString())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(itemKey.getNamespace(), "block/" + itemKey.getPath()));
    }

    private ItemModelBuilder basicItem(ItemLike item) {
        return this.basicItem(item.asItem());
    }

    private ItemModelBuilder handheldItem(ItemLike item) {
        return this.handheldItem(item.asItem());
    }

    private ItemModelBuilder multiLayerAllModel(DeferredBlock<?> block) {
        return MultiLayerModelBuilder.makeCombinedItemModel(this, block);
    }

    private ItemModelBuilder simpleBlockModel(DeferredBlock<?> block) {
        return this.simpleBlockItem(block.getId());
    }

    private void simpleBucket(DeferredItem<? extends Item> item, DeferredHolder<Fluid, ? extends Fluid> fluidSource) {
        withExistingParent(item.getId().getPath(),
                ResourceLocation.fromNamespaceAndPath("neoforge", "item/bucket"))
                .customLoader(DynamicFluidContainerModelBuilder::begin)
                .fluid(fluidSource.get());
    }

    private ItemModelBuilder layeredBasic(ItemLike item, ResourceLocation... textures) {
        return this.layeredBasic(BuiltInRegistries.ITEM.getKey(item.asItem()), textures);
    }

    private ItemModelBuilder layeredBasic(ResourceLocation itemKey, ResourceLocation... textures) {
        ItemModelBuilder builder = this.getBuilder(itemKey.toString())
                .parent(new ModelFile.UncheckedModelFile("item/generated"));

        for (int i = 0; i < textures.length; i++) {
            builder.texture("layer" + i, NameUtil.prefixPath(textures[i], "item/"));
        }
        return builder;
    }

    private ItemModelBuilder layeredHandheld(ItemLike item, ResourceLocation... textures) {
        ItemModelBuilder builder = this.getBuilder(BuiltInRegistries.ITEM.getKey(item.asItem()).toString())
                .parent(new ModelFile.UncheckedModelFile("item/handheld"));

        for (int i = 0; i < textures.length; i++) {
            builder.texture("layer" + i, NameUtil.prefixPath(textures[i], "item/"));
        }
        return builder;
    }

    public static ModelFile model(DeferredHolder<?, ?> entry) {
        return model(entry.getId());
    }

    public static ModelFile model(ResourceLocation name) {
        return new ModelFile.UncheckedModelFile(prefixPath(name, "item/"));
    }
}
