/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.assets.model;

import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelProvider;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PillarModelBuilder
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PillarModelBuilder {

    public static final String TEMPLATE_PILLAR = "pillar";
    public static final String TEMPLATE_PILLAR_BOTTOM = "pillar_bottom";
    public static final String TEMPLATE_PILLAR_TOP = "pillar_top";

    public static BlockModelBuilder makePillarModel(BlockModelProvider provider, String name, ResourceLocation side, ResourceLocation end) {
        return provider.getBuilder(name)
                .parent(provider.getExistingFile(AstralSorcery.key(ModelProvider.BLOCK_FOLDER + "/" + TEMPLATE_PILLAR)))
                .texture("side", side.getPath())
                .texture("end", end.getPath());
    }

    public static BlockModelBuilder makePillarBottomModel(BlockModelProvider provider, String name, ResourceLocation side, ResourceLocation end, ResourceLocation bottom) {
        return provider.getBuilder(name)
                .parent(provider.getExistingFile(AstralSorcery.key(ModelProvider.BLOCK_FOLDER + "/" + TEMPLATE_PILLAR_BOTTOM)))
                .texture("side", side.getPath())
                .texture("end", end.getPath())
                .texture("bottom", bottom.getPath());
    }

    public static BlockModelBuilder makePillarTopModel(BlockModelProvider provider, String name, ResourceLocation side, ResourceLocation end, ResourceLocation top) {
        return provider.getBuilder(name)
                .parent(provider.getExistingFile(AstralSorcery.key(ModelProvider.BLOCK_FOLDER + "/" + TEMPLATE_PILLAR_TOP)))
                .texture("side", side.getPath())
                .texture("end", end.getPath())
                .texture("top", top.getPath());
    }

    public static BlockModelBuilder createPillarTemplate(BlockModelProvider provider) {
        BlockModelBuilder model = provider.getBuilder(TEMPLATE_PILLAR)
                .parent(provider.getExistingFile(provider.mcLoc(ModelProvider.BLOCK_FOLDER + "/block")));
        model.texture("particle", "#side").renderType("solid");
        model.element()
                .from(2, 0, 2)
                .to(14, 16, 14)
                .face(Direction.DOWN).texture("#end").cullface(Direction.DOWN).end()
                .face(Direction.UP).texture("#end").cullface(Direction.UP).end()
                .face(Direction.NORTH).texture("#side").end()
                .face(Direction.SOUTH).texture("#side").end()
                .face(Direction.WEST).texture("#side").end()
                .face(Direction.EAST).texture("#side").end();
        return model;
    }

    public static BlockModelBuilder createPillarBottomTemplate(BlockModelProvider provider) {
        BlockModelBuilder model = provider.getBuilder(TEMPLATE_PILLAR_BOTTOM)
                .parent(provider.getExistingFile(provider.mcLoc(ModelProvider.BLOCK_FOLDER + "/block")));
        model.texture("particle", "#side").renderType("solid");
        model.element()
                .from(2, 4, 2)
                .to(14, 16, 14)
                .face(Direction.UP).texture("#end").cullface(Direction.UP).end()
                .face(Direction.NORTH).texture("#side").end()
                .face(Direction.SOUTH).texture("#side").end()
                .face(Direction.WEST).texture("#side").end()
                .face(Direction.EAST).texture("#side").end();
        model.element()
                .from(0, 0, 0)
                .to(16, 4, 16)
                .face(Direction.DOWN).texture("#bottom").cullface(Direction.DOWN).end()
                .face(Direction.UP).texture("#end").end()
                .face(Direction.NORTH).texture("#side").end()
                .face(Direction.SOUTH).texture("#side").end()
                .face(Direction.WEST).texture("#side").end()
                .face(Direction.EAST).texture("#side").end();
        return model;
    }

    public static BlockModelBuilder createPillarTopTemplate(BlockModelProvider provider) {
        BlockModelBuilder model = provider.getBuilder(TEMPLATE_PILLAR_TOP)
                .parent(provider.getExistingFile(provider.mcLoc(ModelProvider.BLOCK_FOLDER + "/block")));
        model.texture("particle", "#side").renderType("solid");
        model.element()
                .from(0, 12, 0)
                .to(16, 16, 16)
                .face(Direction.DOWN).texture("#end").end()
                .face(Direction.UP).texture("#top").cullface(Direction.UP).end()
                .face(Direction.NORTH).texture("#side").end()
                .face(Direction.SOUTH).texture("#side").end()
                .face(Direction.WEST).texture("#side").end()
                .face(Direction.EAST).texture("#side").end();
        model.element()
                .from(2, 0, 2)
                .to(14, 12, 14)
                .face(Direction.DOWN).texture("#end").cullface(Direction.DOWN).end()
                .face(Direction.NORTH).texture("#side").end()
                .face(Direction.SOUTH).texture("#side").end()
                .face(Direction.WEST).texture("#side").end()
                .face(Direction.EAST).texture("#side").end();
        return model;
    }
}
