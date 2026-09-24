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
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Collection;
import java.util.Collections;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: KeyPerkMendArmor
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class KeyPerkMendArmor extends KeyPerk implements TickablePerk {

    public static final MapCodec<KeyPerkMendArmor> CODEC = RecordCodecBuilder.mapCodec(inst -> perkModifierFields(inst).apply(inst, KeyPerkMendArmor::new));
    public static final PerkType<KeyPerkMendArmor> TYPE =
            PerkType.of(KeyPerkMendArmor.CODEC, PerkDataTypesAS.DEFAULT_DATA, KeyPerkMendArmor::new);
    public static final Config CONFIG = new Config();

    private KeyPerkMendArmor(ResourceLocation key, float x, float y) {
        this(key, defaultNameKey(key), x, y, PerkCategory.MAJOR, Collections.emptySet(), Collections.emptySet(), Collections.emptySet());
    }

    protected KeyPerkMendArmor(ResourceLocation key, String nameKey, float x, float y, PerkCategory category, Collection<PerkRequirement> requirements, Collection<PerkAttributeConverter> converters, Collection<PerkAttributeModifier> modifiers) {
        super(key, nameKey, x, y, category, requirements, converters, modifiers);
    }

    @Override
    public void tick(Player player, LogicalSide side) {
        if (!side.isServer()) return;
        if (!(player instanceof ServerPlayer sPlayer)) return;
        PlayerProgress progress = ResearchManager.getProgress(sPlayer, side);
        if (!progress.getPerkData().hasPerkEffect(this)) return;

        float repairChance = CONFIG.chanceToRepair.getAsInt();
        repairChance /= PerkManager.getOrCreateAttributes(sPlayer).getModifier(sPlayer, progress, PerksAS.AttributeTypes.PERK_EFFECT);
        int repairChanceInt = Math.max(MiscUtil.roundChanced(repairChance, this.rand), 1);
        for (ItemStack stack : sPlayer.getArmorSlots()) {
            if (this.rand.nextInt(repairChanceInt) != 0) continue;

            if (!stack.isEmpty() && stack.isDamageableItem() && stack.isDamaged()) {
                stack.setDamageValue(stack.getDamageValue() - 1);
            }
        }
    }

    @Override
    public PerkType<?> getType() {
        return TYPE;
    }

    public static class Config extends ConfigEntry {

        public ModConfigSpec.IntValue chanceToRepair;

        private Config() {
            super("key_mend_armor");
        }

        @Override
        public void createEntries(ModConfigSpec.Builder cfgBuilder) {
            this.chanceToRepair = cfgBuilder
                    .comment("The chance (1 in x) to repair an armor piece per tick. This is modified by the perk effect increase modifiers.")
                    .translation(translationKey("chanceToRepair"))
                    .defineInRange("chanceToRepair", 800, 1, Integer.MAX_VALUE);
        }
    }
}
