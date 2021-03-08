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
import hellfirepvp.astralsorcery.datagen.data.perks.AstralPerkTreeProvider;
import hellfirepvp.astralsorcery.datagen.data.recipes.AstralRecipeProvider;
import hellfirepvp.astralsorcery.datagen.data.tags.AstralBlockTagsProvider;
import hellfirepvp.astralsorcery.datagen.data.tags.AstralItemTagsProvider;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
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
            BlockTagsProvider blockTagGen = new AstralBlockTagsProvider(gen, fileHelper);
            gen.addProvider(blockTagGen);
            gen.addProvider(new AstralItemTagsProvider(gen, blockTagGen, fileHelper));
            gen.addProvider(new AstralLootTableProvider(gen));
            gen.addProvider(new AstralRecipeProvider(gen));
            gen.addProvider(new AstralPerkTreeProvider(gen));
        }

        if (event.includeClient()) {
            gen.addProvider(new AstralBlockStateMappingProvider(gen, fileHelper));
        }
    }
}
