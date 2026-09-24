/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.lib;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.model.builtin.ModelAttunementAltar;
import hellfirepvp.astralsorcery.client.model.builtin.ModelLens;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ModelLayersAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ModelLayersAS {

    public static final PreparedModelLayer ATTUNEMENT_ALTAR = create("attunement_altar", ModelAttunementAltar::createLayer);
    public static final PreparedModelLayer LENS             = create("lens", ModelLens::createLayer);

    public static void registerModelLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> registrar = event::registerLayerDefinition;

        ATTUNEMENT_ALTAR.register(registrar);
        LENS.register(registrar);
    }

    private static PreparedModelLayer create(String name, Supplier<LayerDefinition> layerDefinition) {
        return new PreparedModelLayer(new ModelLayerLocation(AstralSorcery.key(name), "main"), layerDefinition);
    }

    public record PreparedModelLayer(ModelLayerLocation layerLocation, Supplier<LayerDefinition> layerDefinition) {

        private void register(BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> registrar) {
            registrar.accept(this.layerLocation(), this.layerDefinition());
        }
    }
}
