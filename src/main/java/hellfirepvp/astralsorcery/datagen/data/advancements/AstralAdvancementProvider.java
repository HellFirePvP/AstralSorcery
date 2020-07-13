/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.advancements;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.advancement.AttuneCrystalTrigger;
import hellfirepvp.astralsorcery.common.advancement.AttuneSelfTrigger;
import hellfirepvp.astralsorcery.common.advancement.DiscoverConstellationTrigger;
import hellfirepvp.astralsorcery.common.advancement.instance.AltarRecipeInstance;
import hellfirepvp.astralsorcery.common.advancement.instance.ConstellationInstance;
import hellfirepvp.astralsorcery.common.advancement.instance.PerkLevelInstance;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.TickTrigger;
import net.minecraft.data.AdvancementProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AstralAdvancementProvider
 * Created by HellFirePvP
 * Date: 11.05.2020 / 20:11
 */
public class AstralAdvancementProvider extends AdvancementProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    private final DataGenerator generator;

    public AstralAdvancementProvider(DataGenerator generator) {
        super(generator);
        this.generator = generator;
    }

    @Override
    public void act(DirectoryCache cache) {
        Path path = this.generator.getOutputFolder();
        Set<ResourceLocation> set = Sets.newHashSet();
        Consumer<Advancement> registrar = (advancement) -> {
            if (!set.add(advancement.getId())) {
                throw new IllegalStateException("Duplicate advancement " + advancement.getId());
            } else {
                Path outPath = getPath(path, advancement);
                try {
                    IDataProvider.save(GSON, cache, advancement.copy().serialize(), outPath);
                } catch (IOException ioexception) {
                    LOGGER.error("Couldn't save advancement {}", outPath, ioexception);
                }

            }
        };

        this.registerAdvancements(registrar);
    }

    private Path getPath(Path base, Advancement advancement) {
        return base.resolve(String.format("data/%s/advancements/%s.json", advancement.getId().getNamespace(), advancement.getId().getPath()));
    }

    private TranslationTextComponent title(String key) {
        return new TranslationTextComponent(String.format("advancements.astralsorcery.%s.title", key));
    }

    private TranslationTextComponent description(String key) {
        return new TranslationTextComponent(String.format("advancements.astralsorcery.%s.desc", key));
    }

    private void registerAdvancements(Consumer<Advancement> registrar) {
        Advancement root = Advancement.Builder.builder()
                .withDisplay(ItemsAS.TOME, title("root"), description("root"),
                        AstralSorcery.key("textures/block/black_marble_raw.png"),
                        FrameType.TASK, false, false, false)
                .withCriterion("astralsorcery_present", new TickTrigger.Instance())
                .register(registrar, AstralSorcery.key("root").toString());

        Advancement foundRockCrystals = Advancement.Builder.builder()
                .withParent(root)
                .withDisplay(ItemsAS.ROCK_CRYSTAL, title("rock_crystals"), description("rock_crystals"),
                        null, FrameType.TASK, true, true, false)
                .withCriterion("rock_crystal_in_inventory", InventoryChangeTrigger.Instance.forItems(ItemsAS.ROCK_CRYSTAL))
                .register(registrar, AstralSorcery.key("rock_crystals").toString());
        Advancement foundCelestialCrystals = Advancement.Builder.builder()
                .withParent(foundRockCrystals)
                .withDisplay(ItemsAS.CELESTIAL_CRYSTAL, title("celestial_crystals"), description("celestial_crystals"),
                        null, FrameType.TASK, true, true, false)
                .withCriterion("celestial_crystal_in_inventory", InventoryChangeTrigger.Instance.forItems(ItemsAS.CELESTIAL_CRYSTAL))
                .register(registrar, AstralSorcery.key("celestial_crystals").toString());

        Advancement craftAltarT2 = Advancement.Builder.builder()
                .withParent(foundRockCrystals)
                .withDisplay(BlocksAS.ALTAR_ATTUNEMENT, title("craft_t2_altar"), description("craft_t2_altar"),
                        null, FrameType.TASK, true, true, false)
                .withCriterion("altar_craft_t2_altar", AltarRecipeInstance.withOutput(BlocksAS.ALTAR_ATTUNEMENT))
                .register(registrar, AstralSorcery.key("craft_t2_altar").toString());
        Advancement craftAltarT3 = Advancement.Builder.builder()
                .withParent(craftAltarT2)
                .withDisplay(BlocksAS.ALTAR_CONSTELLATION, title("craft_t3_altar"), description("craft_t3_altar"),
                        null, FrameType.TASK, true, true, false)
                .withCriterion("altar_craft_t3_altar", AltarRecipeInstance.withOutput(BlocksAS.ALTAR_CONSTELLATION))
                .register(registrar, AstralSorcery.key("craft_t3_altar").toString());
        Advancement craftAltarT4 = Advancement.Builder.builder()
                .withParent(craftAltarT3)
                .withDisplay(BlocksAS.ALTAR_CONSTELLATION, title("craft_t4_altar"), description("craft_t4_altar"),
                        null, FrameType.CHALLENGE, true, true, false)
                .withCriterion("altar_craft_t3_altar", AltarRecipeInstance.withOutput(BlocksAS.ALTAR_CONSTELLATION))
                .register(registrar, AstralSorcery.key("craft_t4_altar").toString());

        Advancement findAnyConstellation = Advancement.Builder.builder()
                .withParent(root)
                .withDisplay(BlocksAS.TELESCOPE, title("find_constellation"), description("find_constellation"),
                        null, FrameType.TASK, true, true, false)
                .withCriterion("any_constellation_discovered", ConstellationInstance.any(DiscoverConstellationTrigger.ID))
                .register(registrar, AstralSorcery.key("find_constellation").toString());
        Advancement findWeakConstellation = Advancement.Builder.builder()
                .withParent(findAnyConstellation)
                .withDisplay(BlocksAS.TELESCOPE, title("find_weak_constellation"), description("find_weak_constellation"),
                        null, FrameType.TASK, true, true, false)
                .withCriterion("weak_constellation_discovered", ConstellationInstance.anyWeak(DiscoverConstellationTrigger.ID))
                .register(registrar, AstralSorcery.key("find_weak_constellation").toString());
        Advancement findMinorConstellation = Advancement.Builder.builder()
                .withParent(findWeakConstellation)
                .withDisplay(BlocksAS.OBSERVATORY, title("find_minor_constellation"), description("find_minor_constellation"),
                        null, FrameType.TASK, true, true, false)
                .withCriterion("minor_constellation_discovered", ConstellationInstance.anyMinor(DiscoverConstellationTrigger.ID))
                .register(registrar, AstralSorcery.key("find_minor_constellation").toString());

        Advancement attuneSelf = Advancement.Builder.builder()
                .withParent(findAnyConstellation)
                .withDisplay(BlocksAS.ATTUNEMENT_ALTAR, title("attune_self"), description("attune_self"),
                        null, FrameType.TASK, true, true, false)
                .withCriterion("attune_self", ConstellationInstance.any(AttuneSelfTrigger.ID))
                .register(registrar, AstralSorcery.key("attune_self").toString());
        Advancement attuneCrystal = Advancement.Builder.builder()
                .withParent(attuneSelf)
                .withDisplay(BlocksAS.RITUAL_PEDESTAL, title("attune_crystal"), description("attune_crystal"),
                        null, FrameType.TASK, true, true, false)
                .withCriterion("attune_crystal", ConstellationInstance.anyWeak(AttuneCrystalTrigger.ID))
                .register(registrar, AstralSorcery.key("attune_crystal").toString());
        Advancement attuneCrystalTrait = Advancement.Builder.builder()
                .withParent(attuneCrystal)
                .withDisplay(BlocksAS.RITUAL_PEDESTAL, title("attune_trait"), description("attune_trait"),
                        null, FrameType.TASK, true, true, false)
                .withCriterion("attune_trait", ConstellationInstance.anyMinor(AttuneCrystalTrigger.ID))
                .register(registrar, AstralSorcery.key("attune_trait").toString());

        Advancement perkLevelSmall = Advancement.Builder.builder()
                .withParent(attuneSelf)
                .withDisplay(BlocksAS.SPECTRAL_RELAY, title("perk_level_small"), description("perk_level_small"),
                        null, FrameType.TASK, true, true, false)
                .withCriterion("gain_perk_level_small", PerkLevelInstance.reachLevel(10))
                .register(registrar, AstralSorcery.key("perk_level_small").toString());
        Advancement perkLevelMedium = Advancement.Builder.builder()
                .withParent(perkLevelSmall)
                .withDisplay(BlocksAS.SPECTRAL_RELAY, title("perk_level_medium"), description("perk_level_medium"),
                        null, FrameType.TASK, true, true, false)
                .withCriterion("gain_perk_level_medium", PerkLevelInstance.reachLevel(20))
                .register(registrar, AstralSorcery.key("perk_level_medium").toString());
        Advancement perkLevelLarge = Advancement.Builder.builder()
                .withParent(perkLevelMedium)
                .withDisplay(BlocksAS.SPECTRAL_RELAY, title("perk_level_large"), description("perk_level_large"),
                        null, FrameType.CHALLENGE, true, true, false)
                .withCriterion("gain_perk_level_large", PerkLevelInstance.reachLevel(30))
                .register(registrar, AstralSorcery.key("perk_level_large").toString());
    }
}
