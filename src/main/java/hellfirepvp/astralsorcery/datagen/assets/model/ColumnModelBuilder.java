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
 * Class: ColumnModelBuilder
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ColumnModelBuilder {

    public static final String TEMPLATE_COLUMN = "column";
    public static final String TEMPLATE_COLUMN_BOTTOM = "column_bottom";
    public static final String TEMPLATE_COLUMN_TOP = "column_top";

    public static BlockModelBuilder makeColumnModel(BlockModelProvider provider, String name, ResourceLocation side, ResourceLocation end) {
        return provider.getBuilder(name)
                .parent(provider.getExistingFile(AstralSorcery.key(ModelProvider.BLOCK_FOLDER + "/" + TEMPLATE_COLUMN)))
                .texture("side", side.getPath())
                .texture("end", end.getPath());
    }

    public static BlockModelBuilder makeColumnBottomModel(BlockModelProvider provider, String name, ResourceLocation side, ResourceLocation end, ResourceLocation bottom) {
        return provider.getBuilder(name)
                .parent(provider.getExistingFile(AstralSorcery.key(ModelProvider.BLOCK_FOLDER + "/" + TEMPLATE_COLUMN_BOTTOM)))
                .texture("side", side.getPath())
                .texture("end", end.getPath())
                .texture("bottom", bottom.getPath());
    }

    public static BlockModelBuilder makeColumnTopModel(BlockModelProvider provider, String name, ResourceLocation side, ResourceLocation end, ResourceLocation top) {
        return provider.getBuilder(name)
                .parent(provider.getExistingFile(AstralSorcery.key(ModelProvider.BLOCK_FOLDER + "/" + TEMPLATE_COLUMN_TOP)))
                .texture("side", side.getPath())
                .texture("end", end.getPath())
                .texture("top", top.getPath());
    }

    public static BlockModelBuilder createColumnTemplate(BlockModelProvider provider) {
        BlockModelBuilder model = provider.getBuilder(TEMPLATE_COLUMN)
                .parent(provider.getExistingFile(provider.mcLoc(ModelProvider.BLOCK_FOLDER + "/block")));
        model.texture("particle", "#side").renderType("solid");
        model.element()
                .from(4, 0, 4)
                .to(12, 16, 12)
                .face(Direction.DOWN).texture("#end").cullface(Direction.DOWN).end()
                .face(Direction.UP).texture("#end").cullface(Direction.UP).end()
                .face(Direction.NORTH).texture("#side").end()
                .face(Direction.SOUTH).texture("#side").end()
                .face(Direction.WEST).texture("#side").end()
                .face(Direction.EAST).texture("#side").end();
        return model;
    }

    public static BlockModelBuilder createColumnBottomTemplate(BlockModelProvider provider) {
        BlockModelBuilder model = provider.getBuilder(TEMPLATE_COLUMN_BOTTOM)
                .parent(provider.getExistingFile(provider.mcLoc(ModelProvider.BLOCK_FOLDER + "/block")));
        model.texture("particle", "#side").renderType("solid");
        model.element()
                .from(4, 2, 4)
                .to(12, 16, 12)
                .face(Direction.UP).texture("#end").cullface(Direction.UP).end()
                .face(Direction.NORTH).texture("#side").end()
                .face(Direction.SOUTH).texture("#side").end()
                .face(Direction.WEST).texture("#side").end()
                .face(Direction.EAST).texture("#side").end();
        model.element()
                .from(2, 0, 2)
                .to(14, 2, 14)
                .face(Direction.DOWN).texture("#bottom").cullface(Direction.DOWN).end()
                .face(Direction.UP).texture("#end").end()
                .face(Direction.NORTH).texture("#side").end()
                .face(Direction.SOUTH).texture("#side").end()
                .face(Direction.WEST).texture("#side").end()
                .face(Direction.EAST).texture("#side").end();
        return model;
    }

    public static BlockModelBuilder createColumnTopTemplate(BlockModelProvider provider) {
        BlockModelBuilder model = provider.getBuilder(TEMPLATE_COLUMN_TOP)
                .parent(provider.getExistingFile(provider.mcLoc(ModelProvider.BLOCK_FOLDER + "/block")));
        model.texture("particle", "#side").renderType("solid");
        model.element()
                .from(2, 14, 2)
                .to(14, 16, 14)
                .face(Direction.DOWN).texture("#end").end()
                .face(Direction.UP).texture("#top").cullface(Direction.UP).end()
                .face(Direction.NORTH).texture("#side").end()
                .face(Direction.SOUTH).texture("#side").end()
                .face(Direction.WEST).texture("#side").end()
                .face(Direction.EAST).texture("#side").end();
        model.element()
                .from(4, 0, 4)
                .to(12, 14, 12)
                .face(Direction.DOWN).texture("#end").cullface(Direction.DOWN).end()
                .face(Direction.NORTH).texture("#side").end()
                .face(Direction.SOUTH).texture("#side").end()
                .face(Direction.WEST).texture("#side").end()
                .face(Direction.EAST).texture("#side").end();
        return model;
    }
}
