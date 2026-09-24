/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.datagen.assets.*;
import hellfirepvp.astralsorcery.datagen.data.AstralCuriosProvider;
import hellfirepvp.astralsorcery.datagen.data.AstralRegistriesDataProvider;
import hellfirepvp.astralsorcery.datagen.data.artifact.AstralArtifactConditionProvider;
import hellfirepvp.astralsorcery.datagen.data.artifact.AstralArtifactEffectProvider;
import hellfirepvp.astralsorcery.datagen.data.artifact.AstralArtifactPenaltyProvider;
import hellfirepvp.astralsorcery.datagen.data.damage.AstralDamageTypeTagProvider;
import hellfirepvp.astralsorcery.datagen.data.datamap.AstralDataMapProvider;
import hellfirepvp.astralsorcery.datagen.data.loot.AstralGlobalLootModifierProvider;
import hellfirepvp.astralsorcery.datagen.data.loot.AstralLootTableProvider;
import hellfirepvp.astralsorcery.datagen.data.lumen.AstralLumenBindingDataProvider;
import hellfirepvp.astralsorcery.datagen.data.perks.AstralPerkTreeProvider;
import hellfirepvp.astralsorcery.datagen.data.perks.DebugPerkTreeProvider;
import hellfirepvp.astralsorcery.datagen.data.recipes.AstralRecipeProvider;
import hellfirepvp.astralsorcery.datagen.data.research.AstralResearchNodeProvider;
import hellfirepvp.astralsorcery.datagen.data.tags.AstralBlockTagsProvider;
import hellfirepvp.astralsorcery.datagen.data.tags.AstralConstellationTagsProvider;
import hellfirepvp.astralsorcery.datagen.data.tags.AstralEntityTagsProvider;
import hellfirepvp.astralsorcery.datagen.data.tags.AstralItemTagsProvider;
import hellfirepvp.astralsorcery.datagen.data.world.AstralWorldGenProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AstralDataGenerator
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
@EventBusSubscriber(modid = AstralSorcery.MODID)
public class AstralDataGenerator {

    @SubscribeEvent
    public static void gather(GatherDataEvent event) {
        if (!AstralSorcery.isDoingDataGeneration()) {
            return;
        }
        event.createDatapackRegistryObjects(AstralRegistriesDataProvider.getRegistryBuilder());

        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        if (event.includeClient()) {
            gen.addProvider(true, new AstralCustomSpriteProvider(output, lookupProvider, fileHelper));
            gen.addProvider(true, new AstralBlockModelProvider(output, fileHelper));
            gen.addProvider(true, new AstralItemModelProvider(output, fileHelper));
            gen.addProvider(true, new AstralBlockStateProvider(output, fileHelper));
            gen.addProvider(true, new AstralConstellationPositionProvider(output));
            gen.addProvider(true, new AstralLumenDisplayPositionProvider(output));
            gen.addProvider(true, new AstralSoundsProvider(output, fileHelper));
        }
        if (event.includeServer()) {
            AstralBlockTagsProvider blockTags = new AstralBlockTagsProvider(output, lookupProvider, fileHelper);
            gen.addProvider(true, blockTags);
            gen.addProvider(true, new AstralItemTagsProvider(output, lookupProvider, blockTags.contentsGetter(), fileHelper));
            gen.addProvider(true, new AstralEntityTagsProvider(output, lookupProvider, fileHelper));
            gen.addProvider(true, new AstralConstellationTagsProvider(output, lookupProvider, fileHelper));
            gen.addProvider(true, new AstralRecipeProvider(output, lookupProvider));
            gen.addProvider(true, new AstralResearchNodeProvider(output, lookupProvider));
            gen.addProvider(true, new AstralDataMapProvider(output, lookupProvider));
            gen.addProvider(true, new AstralLootTableProvider(output, lookupProvider));
            gen.addProvider(true, new AstralGlobalLootModifierProvider(output, lookupProvider));
            gen.addProvider(true, new AstralArtifactConditionProvider(output, lookupProvider));
            gen.addProvider(true, new AstralArtifactEffectProvider(output, lookupProvider));
            gen.addProvider(true, new AstralArtifactPenaltyProvider(output, lookupProvider));
            gen.addProvider(true, new AstralDamageTypeTagProvider(output, lookupProvider, fileHelper));
            gen.addProvider(true, new AstralCuriosProvider(output, fileHelper, lookupProvider));
            gen.addProvider(true, new AstralLumenBindingDataProvider(output, lookupProvider));
            //gen.addProvider(true, new DebugPerkTreeProvider(output, lookupProvider));
            gen.addProvider(true, new AstralPerkTreeProvider(output, lookupProvider));
        }
    }
}
