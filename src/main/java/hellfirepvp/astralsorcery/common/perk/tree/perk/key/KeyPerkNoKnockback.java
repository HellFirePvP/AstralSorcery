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
import hellfirepvp.astralsorcery.common.lib.types.PerkDataTypesAS;
import hellfirepvp.astralsorcery.common.perk.convert.PerkAttributeConverter;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.tree.PerkCategory;
import hellfirepvp.astralsorcery.common.perk.tree.PerkType;
import hellfirepvp.astralsorcery.common.perk.tree.perk.KeyPerk;
import hellfirepvp.astralsorcery.common.perk.tree.requirement.PerkRequirement;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.event.SidedEventBus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import org.jline.utils.Log;

import java.util.Collection;
import java.util.Collections;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: KeyPerkNoKnockback
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class KeyPerkNoKnockback extends KeyPerk {

    public static final MapCodec<KeyPerkNoKnockback> CODEC = RecordCodecBuilder.mapCodec(inst -> perkModifierFields(inst).apply(inst, KeyPerkNoKnockback::new));
    public static final PerkType<KeyPerkNoKnockback> TYPE =
            PerkType.of(KeyPerkNoKnockback.CODEC, PerkDataTypesAS.DEFAULT_DATA, KeyPerkNoKnockback::new);

    private KeyPerkNoKnockback(ResourceLocation key, float x, float y) {
        this(key, defaultNameKey(key), x, y, PerkCategory.MAJOR, Collections.emptySet(), Collections.emptySet(), Collections.emptySet());
    }

    protected KeyPerkNoKnockback(ResourceLocation key, String nameKey, float x, float y, PerkCategory category, Collection<PerkRequirement> requirements, Collection<PerkAttributeConverter> converters, Collection<PerkAttributeModifier> modifiers) {
        super(key, nameKey, x, y, category, requirements, converters, modifiers);
    }

    @Override
    protected void attachEventListeners(SidedEventBus sidedEventBus) {
        super.attachEventListeners(sidedEventBus);
        sidedEventBus.addListener(LivingKnockBackEvent.class, SidedEventBus.entityEvent(), this::onKnockback);
    }

    private void onKnockback(LivingKnockBackEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        LogicalSide side = this.getSide(player);
        PlayerProgress progress = ResearchManager.getProgress(player, side);
        if (progress.getPerkData().hasPerkEffect(this)) {
            event.setCanceled(true);
        }
    }

    @Override
    public PerkType<?> getType() {
        return TYPE;
    }
}
