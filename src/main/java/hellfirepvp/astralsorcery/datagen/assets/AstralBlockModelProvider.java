/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.assets;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.util.NameUtil;
import hellfirepvp.astralsorcery.datagen.assets.model.ColumnModelBuilder;
import hellfirepvp.astralsorcery.datagen.assets.model.PillarModelBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.client.model.generators.BlockModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

import static hellfirepvp.astralsorcery.common.lib.BlocksAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AstralBlockModelProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AstralBlockModelProvider extends BlockModelProvider {

    public AstralBlockModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, AstralSorcery.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        this.registerModelTemplates();

        this.cubeBottomTop(modelName(MARBLE_ARCH), blockTextureName(MARBLE_ARCH), blockTextureName(MARBLE_RAW), blockTextureName(MARBLE_RAW));
        this.cubeAll(modelName(MARBLE_BRICKS), blockTextureName(MARBLE_BRICKS));
        this.cubeAll(modelName(MARBLE_CHISELED), blockTextureName(MARBLE_CHISELED));
        this.cubeAll(modelName(MARBLE_ENGRAVED), blockTextureName(MARBLE_ENGRAVED));
        this.pillar(modelName(MARBLE_PILLAR), blockTextureName(MARBLE_PILLAR));
        this.cubeAll(modelName(MARBLE_RAW), blockTextureName(MARBLE_RAW));
        this.cubeBottomTop(modelName(MARBLE_RUNED), blockTextureName(MARBLE_RUNED), blockTextureName(MARBLE_RAW), blockTextureName(MARBLE_RAW));
        this.slabAll(modelName(MARBLE_SLAB), blockTextureName(MARBLE_BRICKS));
        this.stairsAll(modelName(MARBLE_STAIRS), blockTextureName(MARBLE_BRICKS));

        this.cubeBottomTop(modelName(SOOTY_MARBLE_ARCH), blockTextureName(SOOTY_MARBLE_ARCH), blockTextureName(SOOTY_MARBLE_RAW), blockTextureName(SOOTY_MARBLE_RAW));
        this.cubeAll(modelName(SOOTY_MARBLE_BRICKS), blockTextureName(SOOTY_MARBLE_BRICKS));
        this.cubeAll(modelName(SOOTY_MARBLE_CHISELED), blockTextureName(SOOTY_MARBLE_CHISELED));
        this.cubeAll(modelName(SOOTY_MARBLE_ENGRAVED), blockTextureName(SOOTY_MARBLE_ENGRAVED));
        this.pillar(modelName(SOOTY_MARBLE_PILLAR), blockTextureName(SOOTY_MARBLE_PILLAR));
        this.cubeAll(modelName(SOOTY_MARBLE_RAW), blockTextureName(SOOTY_MARBLE_RAW));
        this.cubeBottomTop(modelName(SOOTY_MARBLE_RUNED), blockTextureName(SOOTY_MARBLE_RUNED), blockTextureName(SOOTY_MARBLE_RAW), blockTextureName(SOOTY_MARBLE_RAW));
        this.slabAll(modelName(SOOTY_MARBLE_SLAB), blockTextureName(SOOTY_MARBLE_BRICKS));
        this.stairsAll(modelName(SOOTY_MARBLE_STAIRS), blockTextureName(SOOTY_MARBLE_BRICKS));

        this.cubeAll(modelName(INFUSED_WOOD_RAW), blockTextureName(INFUSED_WOOD_RAW));
        this.cubeBottomTop(modelName(INFUSED_WOOD_ARCH), blockTextureName(INFUSED_WOOD_ARCH), blockTextureName(INFUSED_WOOD_RAW), blockTextureName(INFUSED_WOOD_RAW));
        this.column(modelName(INFUSED_WOOD_COLUMN), blockTextureName(INFUSED_WOOD_COLUMN));
        this.cubeAll(modelName(INFUSED_WOOD_ENGRAVED), blockTextureName(INFUSED_WOOD_ENGRAVED));
        this.cubeAll(modelName(INFUSED_WOOD_ENRICHED), blockTextureName(INFUSED_WOOD_ENRICHED));
        this.cubeAll(modelName(INFUSED_WOOD_INFUSED), blockTextureName(INFUSED_WOOD_INFUSED));
        this.cubeAll(modelName(INFUSED_WOOD_PLANKS), blockTextureName(INFUSED_WOOD_PLANKS));
        this.slabAll(modelName(INFUSED_WOOD_SLAB), blockTextureName(INFUSED_WOOD_PLANKS));
        this.stairsAll(modelName(INFUSED_WOOD_STAIRS), blockTextureName(INFUSED_WOOD_PLANKS));

        this.cubeAll(modelName(AQUAMARINE_SHALE), blockTextureName(AQUAMARINE_SHALE));
        this.cubeAll(modelName(ROCK_CRYSTAL_ORE), blockTextureName(ROCK_CRYSTAL_ORE));
        this.cubeAll(modelName(STARMETAL_ORE), blockTextureName(STARMETAL_ORE));
        this.cubeAll(modelName(RAW_STARMETAL_BLOCK), blockTextureName(RAW_STARMETAL_BLOCK));

        this.createPlant(GLIMMER_AMARANTH, POTTED_GLIMMER_AMARANTH);
        this.createPlant(HYACINTH, POTTED_HYACINTH);
        this.createPlant(IRIS, POTTED_IRIS);
        this.createPlant(ORCHID, POTTED_ORCHID);
        this.createPlant(PROTEA, POTTED_PROTEA);
        this.createPlant(THISTLE, POTTED_THISTLE);
    }

    protected void registerModelTemplates() {
        PillarModelBuilder.createPillarTemplate(this);
        PillarModelBuilder.createPillarTopTemplate(this);
        PillarModelBuilder.createPillarBottomTemplate(this);

        ColumnModelBuilder.createColumnTemplate(this);
        ColumnModelBuilder.createColumnTopTemplate(this);
        ColumnModelBuilder.createColumnBottomTemplate(this);
    }

    protected void pillar(String name, ResourceLocation pillarTexture) {
        ResourceLocation pillarInner = NameUtil.suffixPath(pillarTexture, "_inner");
        ResourceLocation pillarUpDown = NameUtil.suffixPath(pillarTexture, "_updown");
        ResourceLocation pillarTop = NameUtil.suffixPath(pillarTexture, "_top");
        ResourceLocation pillarBottom = NameUtil.suffixPath(pillarTexture, "_bottom");

        PillarModelBuilder.makePillarModel(this, name, pillarTexture, pillarInner);
        PillarModelBuilder.makePillarBottomModel(this, name + "_bottom", pillarBottom, pillarInner, pillarUpDown);
        PillarModelBuilder.makePillarTopModel(this, name + "_top", pillarTop, pillarInner, pillarUpDown);
    }

    protected void column(String name, ResourceLocation pillarTexture) {
        ResourceLocation pillarInner = NameUtil.suffixPath(pillarTexture, "_inner");
        ResourceLocation pillarUpDown = NameUtil.suffixPath(pillarTexture, "_updown");
        ResourceLocation pillarTop = NameUtil.suffixPath(pillarTexture, "_top");
        ResourceLocation pillarBottom = NameUtil.suffixPath(pillarTexture, "_bottom");

        ColumnModelBuilder.makeColumnModel(this, name, pillarTexture, pillarInner);
        ColumnModelBuilder.makeColumnBottomModel(this, name + "_bottom", pillarBottom, pillarInner, pillarUpDown);
        ColumnModelBuilder.makeColumnTopModel(this, name + "_top", pillarTop, pillarInner, pillarUpDown);
    }

    private void createPlant(DeferredBlock<?> plantBlock, DeferredBlock<?> pottedPlantBlock) {
        this.cross(modelName(plantBlock), blockTextureName(plantBlock)).renderType("cutout");
        this.withExistingParent(modelName(pottedPlantBlock), BLOCK_FOLDER + "/flower_pot_cross")
                .texture("plant", blockTextureName(plantBlock))
                .renderType("cutout");
    }

    protected void slabAll(String name, ResourceLocation texture) {
        this.slab(name, texture, texture, texture);
        this.slabTop(name + "_top", texture, texture, texture);
    }

    protected void stairsAll(String name, ResourceLocation texture) {
        this.stairs(name, texture, texture, texture);
        this.stairsInner(name + "_inner", texture, texture, texture);
        this.stairsOuter(name + "_outer", texture, texture, texture);
    }

    protected void empty(String name) {
        this.getBuilder(name).toJson();
    }

    private static String modelName(DeferredBlock<?> block) {
        return block.getId().getPath();
    }

    private static ResourceLocation blockTextureName(DeferredBlock<?> block) {
        return textureName(block, "block/");
    }

    private static ResourceLocation textureName(DeferredBlock<?> block, String prefix) {
        return NameUtil.prefixPath(block.getId(), prefix);
    }
}
