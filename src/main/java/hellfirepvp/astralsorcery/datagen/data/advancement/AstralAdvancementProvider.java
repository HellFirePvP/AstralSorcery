/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.advancement;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.advancement.*;
import hellfirepvp.astralsorcery.common.item.LumenCrystalItem;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.LumenAS;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static hellfirepvp.astralsorcery.AstralSorcery.key;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AstralAdvancementProvider
 * Created by HellFirePvP
 * Date: 08.10.2026 / 9:47
 */
public class AstralAdvancementProvider implements AdvancementProvider.AdvancementGenerator {

    @Override
    public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver, ExistingFileHelper fileHelper) {
        AdvancementHolder root = Advancement.Builder.advancement()
                .display(ItemsAS.TOME, title("root"), description("root"), AstralSorcery.key("textures/block/marble_raw.png"), AdvancementType.TASK, false, false, false)
                .addCriterion("has_tome", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsAS.TOME))
                .save(saver, key("root"), fileHelper);

        AdvancementHolder focalPoint = Advancement.Builder.advancement()
                .parent(root)
                .display(ItemsAS.ASTROLABE, title("focal_point"), description("focal_point"), null, AdvancementType.TASK, true, false, false)
                .addCriterion("discover_focal_point", FocalPointDiscoveryTrigger.Instance.discoverAny())
                .save(saver, key("focal_point"), fileHelper);

        AdvancementHolder rockCrystal = Advancement.Builder.advancement()
                .parent(root)
                .display(ItemsAS.ROCK_CRYSTAL, title("rock_crystal"), description("rock_crystal"), null, AdvancementType.TASK, true, false, false)
                .addCriterion("has_rock_crystal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsAS.ROCK_CRYSTAL))
                .save(saver, key("rock_crystal"), fileHelper);

        AdvancementHolder celestialCrystal = Advancement.Builder.advancement()
                .parent(rockCrystal)
                .display(ItemsAS.CELESTIAL_CRYSTAL, title("celestial_crystal"), description("celestial_crystal"), null, AdvancementType.TASK, true, false, false)
                .addCriterion("has_celestial_crystal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsAS.CELESTIAL_CRYSTAL))
                .save(saver, key("celestial_crystal"), fileHelper);

        AdvancementHolder constellation = Advancement.Builder.advancement()
                .parent(root)
                .display(ItemsAS.CONSTELLATION_PAPER, title("constellation"), description("constellation"), null, AdvancementType.TASK, true, false, false)
                .addCriterion("discover_constellation", ConstellationDiscoveryTrigger.Instance.discoverAny())
                .save(saver, key("constellation"), fileHelper);

        AdvancementHolder attunePlayer = Advancement.Builder.advancement()
                .parent(constellation)
                .display(ItemsAS.SHIFTING_STAR, title("attune_player"), description("attune_player"), null, AdvancementType.TASK, true, false, false)
                .addCriterion("attune_player", PlayerAttunementTrigger.Instance.attuneAny())
                .save(saver, key("attune_player"), fileHelper);

        AdvancementHolder attuneItem = Advancement.Builder.advancement()
                .parent(constellation)
                .display(ItemsAS.ATTUNED_ROCK_CRYSTAL, title("attune_crystal"), description("attune_crystal"), null, AdvancementType.TASK, true, false, false)
                .addCriterion("attune_crystal", ItemAttunementTrigger.Instance.attuneAnyItem())
                .save(saver, key("attune_crystal"), fileHelper);

        List<Integer> levels = Arrays.asList(10, 20, 30);
        AdvancementHolder perkParent = attunePlayer;
        for (int level : levels) {
            String name = "perk_level_" + level;
            perkParent = Advancement.Builder.advancement()
                    .parent(perkParent)
                    .display(ItemsAS.PERK_SEAL, title(name), description(name), null, AdvancementType.TASK, true, false, false)
                    .addCriterion("reach_perk_level", PerkLevelTrigger.Instance.reachLevel(level))
                    .save(saver, key(name), fileHelper);
        }

        AdvancementHolder socketGem = Advancement.Builder.advancement()
                .parent(attunePlayer)
                .display(ItemsAS.DYNAMISM_GEM_SKY, title("socket_gem"), description("socket_gem"), null, AdvancementType.TASK, true, false, false)
                .addCriterion("socket_gem", GemSocketTrigger.Instance.socketAny())
                .save(saver, key("socket_gem"), fileHelper);

        AdvancementHolder lumen = Advancement.Builder.advancement()
                .parent(focalPoint)
                .display(LumenCrystalItem.getCrystal(LumenAS.SOLYN), title("lumen"), description("lumen"), null, AdvancementType.TASK, true, false, false)
                .addCriterion("discover_lumen", LumenDiscoveryTrigger.Instance.discoverNonElementary())
                .save(saver, key("lumen"), fileHelper);

        AdvancementHolder prismaticLumen = Advancement.Builder.advancement()
                .parent(lumen)
                .display(LumenCrystalItem.getCrystal(LumenAS.PRISMATIC), title("lumen_prismatic"), description("lumen_prismatic"), null, AdvancementType.TASK, true, false, false)
                .addCriterion("discover_prismatic_lumen", LumenDiscoveryTrigger.Instance.discover(LumenAS.PRISMATIC.get()))
                .save(saver, key("lumen_prismatic"), fileHelper);
    }

    private static Component title(String name) {
        return Component.translatable("advancement.astralsorcery." + name + ".title");
    }

    private static Component description(String name) {
        return Component.translatable("advancement.astralsorcery." + name + ".description");
    }
}
