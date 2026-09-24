/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.type.base;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.perk.PerkManager;
import hellfirepvp.astralsorcery.common.perk.source.ModifierSource;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.LogicalSide;

import java.util.EnumMap;
import java.util.Locale;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: VanillaAttributeType
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class VanillaAttributeType extends PerkAttributeType implements VanillaPerkAttributeType {

    private final EnumMap<ModifierType, ResourceLocation> modifierIds = new EnumMap<>(ModifierType.class);

    protected VanillaAttributeType() {
        this(false);
    }

    protected VanillaAttributeType(boolean isOnlyMultiplicative) {
        super(isOnlyMultiplicative);
        String path = this.getAttribute().unwrapKey().map(key -> key.location().getPath()).orElseThrow();

        for (ModifierType type : ModifierType.values()) {
            String idName = String.format("dynamic_vanilla_modifier_%s_%s", path, type.name().toLowerCase(Locale.ROOT));
            this.modifierIds.put(type, AstralSorcery.key(idName));
        }
    }

    protected ResourceLocation getModifierID(ModifierType type) {
        return this.modifierIds.get(type);
    }

    @Override
    public void onApply(Player player, LogicalSide side, ModifierSource source) {
        super.onApply(player, side, source);
        this.refreshAttribute(player, side);
    }

    @Override
    public void onRemove(Player player, LogicalSide side, boolean removedCompletely, ModifierSource source) {
        super.onRemove(player, side, removedCompletely, source);
        this.refreshAttribute(player, side);
    }

    @Override
    public void refreshAttribute(Player player, LogicalSide side) {
        AttributeInstance attr = player.getAttributes().getInstance(this.getAttribute());
        if (attr == null) return;

        PlayerProgress progress = ResearchManager.getProgress(player, side);
        for (ModifierType type : ModifierType.values()) {
            attr.removeModifier(this.getModifierID(type));

            float amount = PerkManager.getOrCreateAttributes(player).getModifier(player, progress, this, type) - 1;
            if (!type.isNeutralVanillaValue(amount)) {
                attr.addTransientModifier(new AttributeModifier(this.getModifierID(type), amount, type.getVanillaAttributeOperation()));
            }
        }
    }
}
