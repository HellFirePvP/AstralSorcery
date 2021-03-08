/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.node.key;

import hellfirepvp.astralsorcery.common.auxiliary.charge.AlignmentChargeHandler;
import hellfirepvp.astralsorcery.common.data.config.base.ConfigEntry;
import hellfirepvp.astralsorcery.common.data.config.registry.OreBlockRarityRegistry;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.perk.node.KeyPerk;
import hellfirepvp.astralsorcery.common.perk.tick.PlayerTickPerk;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyStoneEnrichment
 * Created by HellFirePvP
 * Date: 01.09.2019 / 00:19
 */
public class KeyStoneEnrichment extends KeyPerk implements PlayerTickPerk {

    private static final int defaultEnrichmentRadius = 3;
    private static final int defaultChanceToEnrich = 55;
    private static final int defaultChargeCost = 150;

    public static final Config CONFIG = new Config("key.stone_enrichment");

    public KeyStoneEnrichment(ResourceLocation name, float x, float y) {
        super(name, x, y);
    }

    @Override
    public void onPlayerTick(PlayerEntity player, LogicalSide side) {
        if (side.isServer()) {
            PlayerProgress prog = ResearchHelper.getProgress(player, side);
            float modChance = (float) CONFIG.chanceToEnrich.get();
            modChance /= PerkAttributeHelper.getOrCreateMap(player, side)
                    .getModifier(player, prog, PerkAttributeTypesAS.ATTR_TYPE_INC_PERK_EFFECT);
            if (rand.nextInt(Math.round(Math.max(modChance, 1))) == 0 &&
                    AlignmentChargeHandler.INSTANCE.drainCharge(player, LogicalSide.SERVER, CONFIG.chargeCost.get(), true)) {
                float radius = (float) CONFIG.enrichmentRadius.get();
                radius *= PerkAttributeHelper.getOrCreateMap(player, side)
                        .getModifier(player, prog, PerkAttributeTypesAS.ATTR_TYPE_INC_PERK_EFFECT);

                Vector3 vec = Vector3.atEntityCenter(player).add(
                        (rand.nextFloat() * radius * 2) - radius,
                        (rand.nextFloat() * radius * 2) - radius,
                        (rand.nextFloat() * radius * 2) - radius);
                World world = player.getEntityWorld();
                BlockPos pos = vec.toBlockPos();
                if (BlockTags.BASE_STONE_OVERWORLD.contains(world.getBlockState(pos).getBlock())) {
                    Block block = OreBlockRarityRegistry.STONE_ENRICHMENT.getRandomBlock(rand);
                    if (block != null) {
                        if (world.setBlockState(pos, block.getDefaultState(), Constants.BlockFlags.DEFAULT_AND_RERENDER)) {
                            AlignmentChargeHandler.INSTANCE.drainCharge(player, LogicalSide.SERVER, CONFIG.chargeCost.get(), false);
                        }
                    }
                }
            }
        }
    }

    public static class Config extends ConfigEntry {

        private ForgeConfigSpec.IntValue enrichmentRadius;
        private ForgeConfigSpec.IntValue chanceToEnrich;
        private ForgeConfigSpec.IntValue chargeCost;

        private Config(String section) {
            super(section);
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            this.enrichmentRadius = cfgBuilder
                    .comment("Defines the radius where a random position to generate a ore at is checked for")
                    .translation(translationKey("enrichmentRadius"))
                    .defineInRange("enrichmentRadius", defaultEnrichmentRadius, 1, 15);
            this.chanceToEnrich = cfgBuilder
                    .comment("Sets the chance (Random.nextInt(chance) == 0) to try to see if a random stone next to the player should get turned into an ore; the lower the more likely")
                    .translation(translationKey("chanceToEnrich"))
                    .defineInRange("chanceToEnrich", defaultChanceToEnrich, 2, 512);
            this.chargeCost = cfgBuilder
                    .comment("Defines the amount of starlight charge consumed per created ore.")
                    .translation(translationKey("chargeCost"))
                    .defineInRange("chargeCost", defaultChargeCost, 1, 500);
        }
    }
}
