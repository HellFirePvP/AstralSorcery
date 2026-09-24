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
import hellfirepvp.astralsorcery.common.lib.MobEffectsAS;
import hellfirepvp.astralsorcery.common.lib.types.PerkDataTypesAS;
import hellfirepvp.astralsorcery.common.perk.convert.PerkAttributeConverter;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.tree.PerkCategory;
import hellfirepvp.astralsorcery.common.perk.tree.PerkType;
import hellfirepvp.astralsorcery.common.perk.tree.perk.KeyPerk;
import hellfirepvp.astralsorcery.common.perk.tree.requirement.PerkRequirement;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.MobEffectUtil;
import hellfirepvp.astralsorcery.common.util.event.SidedEventBus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.Collection;
import java.util.Collections;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: KeyPerkRampage
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class KeyPerkRampage extends KeyPerk {

    public static final MapCodec<KeyPerkRampage> CODEC = RecordCodecBuilder.mapCodec(inst -> perkModifierFields(inst).apply(inst, KeyPerkRampage::new));
    public static final PerkType<KeyPerkRampage> TYPE =
            PerkType.of(KeyPerkRampage.CODEC, PerkDataTypesAS.DEFAULT_DATA, KeyPerkRampage::new);

    private KeyPerkRampage(ResourceLocation key, float x, float y) {
        this(key, defaultNameKey(key), x, y, PerkCategory.MAJOR, Collections.emptySet(), Collections.emptySet(), Collections.emptySet());
    }

    protected KeyPerkRampage(ResourceLocation key, String nameKey, float x, float y, PerkCategory category, Collection<PerkRequirement> requirements, Collection<PerkAttributeConverter> converters, Collection<PerkAttributeModifier> modifiers) {
        super(key, nameKey, x, y, category, requirements, converters, modifiers);
    }

    @Override
    protected void attachEventListeners(SidedEventBus sidedEventBus) {
        super.attachEventListeners(sidedEventBus);
        sidedEventBus.addListener(LivingDamageEvent.Post.class, SidedEventBus.entityEvent(), this::onPostHit);
    }

    private void onPostHit(LivingDamageEvent.Post event) {
        Entity source = event.getSource().getEntity();
        if (!(source instanceof ServerPlayer sPlayer)) return;
        LogicalSide side = this.getSide(sPlayer);
        if (!side.isServer()) return;
        PlayerProgress progress = ResearchManager.getProgress(sPlayer, side);
        if (!progress.getPerkData().hasPerkEffect(this)) return;

        MobEffectInstance rampageInst = sPlayer.getEffect(MobEffectsAS.RAMPAGE);
        if (rampageInst != null) {
            rampageInst = MobEffectUtil.newAmplifier(rampageInst, rampageInst.getAmplifier() + 1);
            rampageInst = MobEffectUtil.newDuration(rampageInst, Math.max(rampageInst.getDuration(), 60));
        } else {
            rampageInst = new MobEffectInstance(MobEffectsAS.RAMPAGE, 60, 0, false, false, true);
        }
        sPlayer.addEffect(rampageInst);
    }

    @Override
    public PerkType<?> getType() {
        return TYPE;
    }
}
