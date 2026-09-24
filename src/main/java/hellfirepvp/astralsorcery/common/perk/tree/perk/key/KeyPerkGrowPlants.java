/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.tree.perk.key;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.config.ConfigEntry;
import hellfirepvp.astralsorcery.common.lib.PerksAS;
import hellfirepvp.astralsorcery.common.lib.types.PerkDataTypesAS;
import hellfirepvp.astralsorcery.common.perk.PerkManager;
import hellfirepvp.astralsorcery.common.perk.convert.PerkAttributeConverter;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.tick.TickablePerk;
import hellfirepvp.astralsorcery.common.perk.tree.PerkCategory;
import hellfirepvp.astralsorcery.common.perk.tree.PerkType;
import hellfirepvp.astralsorcery.common.perk.tree.perk.KeyPerk;
import hellfirepvp.astralsorcery.common.perk.tree.requirement.PerkRequirement;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.CropUtil;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.visual.type.SinglePlantGrowthEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Collection;
import java.util.Collections;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: KeyPerkGrowPlants
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class KeyPerkGrowPlants extends KeyPerk implements TickablePerk {

    public static final MapCodec<KeyPerkGrowPlants> CODEC = RecordCodecBuilder.mapCodec(inst -> perkModifierFields(inst).apply(inst, KeyPerkGrowPlants::new));
    public static final PerkType<KeyPerkGrowPlants> TYPE =
            PerkType.of(KeyPerkGrowPlants.CODEC, PerkDataTypesAS.DEFAULT_DATA, KeyPerkGrowPlants::new);
    public static final Config CONFIG = new Config();

    private KeyPerkGrowPlants(ResourceLocation key, float x, float y) {
        this(key, defaultNameKey(key), x, y, PerkCategory.MAJOR, Collections.emptySet(), Collections.emptySet(), Collections.emptySet());
    }

    protected KeyPerkGrowPlants(ResourceLocation key, String nameKey, float x, float y, PerkCategory category, Collection<PerkRequirement> requirements, Collection<PerkAttributeConverter> converters, Collection<PerkAttributeModifier> modifiers) {
        super(key, nameKey, x, y, category, requirements, converters, modifiers);
    }

    @Override
    public void tick(Player player, LogicalSide side) {
        if (!side.isServer()) return;
        if (!(player.level() instanceof ServerLevel sLevel)) return;
        PlayerProgress progress = ResearchManager.getProgress(player, side);
        if (!progress.getPerkData().hasPerkEffect(this)) return;

        float chance = PerkManager.getOrCreateAttributes(player)
                .modifyValue(player, progress, PerksAS.AttributeTypes.PERK_EFFECT, CONFIG.chance.get().floatValue());
        int chancedCount = MiscUtil.roundChanced(chance, this.rand);
        if (chancedCount <= 0) return;

        for (int i = 0; i < chancedCount; i++) {
            int radius = CONFIG.radius.getAsInt();
            BlockPos randPos = player.blockPosition().offset(
                    this.rand.nextInt(radius * 2 + 1) - radius,
                    this.rand.nextInt(radius * 2 + 1) - radius,
                    this.rand.nextInt(radius * 2 + 1) - radius);
            if (!sLevel.isInWorldBounds(randPos)) continue;

            CropUtil.Plant plant = CropUtil.wrapPlant(sLevel, randPos).orElse(null);
            if (plant != null) {
                if (plant.tryGrow(sLevel, this.rand)) {
                    SinglePlantGrowthEffect.at(randPos).sendToNearby(sLevel);
                }
            } else if (sLevel.getBlockState(randPos).is(Blocks.DIRT) && sLevel.isEmptyBlock(randPos.above())) {
                if (sLevel.setBlockAndUpdate(randPos, Blocks.GRASS_BLOCK.defaultBlockState())) {
                    SinglePlantGrowthEffect.at(randPos).sendToNearby(sLevel);
                }
            }
        }
    }

    @Override
    public PerkType<?> getType() {
        return TYPE;
    }

    public static class Config extends ConfigEntry {

        public ModConfigSpec.DoubleValue chance;
        public ModConfigSpec.IntValue radius;

        public Config() {
            super("key_grow_plants");
        }

        @Override
        public void createEntries(ModConfigSpec.Builder cfgBuilder) {
            this.chance = cfgBuilder
                    .comment("Chance per tick to try to run growth tick on a nearby plant. Can surpass 100% with perk effect for potentially multiple ticks.")
                    .translation(translationKey("chance"))
                    .defineInRange("chance", 1F, 0, 50F);
            this.radius = cfgBuilder
                    .comment("Radius in which to try to grow nearby plants.")
                    .translation(translationKey("radius"))
                    .defineInRange("radius", 3, 1, 16);
        }
    }
}
