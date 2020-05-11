/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.datagen.assets.AstralBlockStateMappingProvider;
import hellfirepvp.astralsorcery.datagen.data.advancements.AstralAdvancementProvider;
import hellfirepvp.astralsorcery.datagen.data.loot.AstralLootTableProvider;
import hellfirepvp.astralsorcery.datagen.data.recipe.AstralRecipeProvider;
import hellfirepvp.astralsorcery.datagen.data.tag.AstralBlockTagsProvider;
import hellfirepvp.astralsorcery.datagen.data.tag.AstralItemTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AstralDataGenerator
 * Created by HellFirePvP
 * Date: 06.03.2020 / 20:11
 */
//Annotation used to separate this code initialization cleanly from everything else.
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class AstralDataGenerator {

    @SubscribeEvent
    public static void gather(GatherDataEvent event) {
        if (!AstralSorcery.isDoingDataGeneration()) {
            return;
        }

        DataGenerator gen = event.getGenerator();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();

        if (event.includeServer()) {
            gen.addProvider(new AstralAdvancementProvider(gen));
            gen.addProvider(new AstralItemTagsProvider(gen));
            gen.addProvider(new AstralBlockTagsProvider(gen));
            gen.addProvider(new AstralLootTableProvider(gen));
            gen.addProvider(new AstralRecipeProvider(gen));
        }

        if (event.includeClient()) {
            gen.addProvider(new AstralBlockStateMappingProvider(gen, fileHelper));
        }
    }
}
