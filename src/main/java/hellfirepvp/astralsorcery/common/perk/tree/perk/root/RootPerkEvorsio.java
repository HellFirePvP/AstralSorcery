/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.tree.perk.root;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.event.AttributeEvent;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.lib.PerksAS;
import hellfirepvp.astralsorcery.common.lib.types.PerkDataTypesAS;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeMap;
import hellfirepvp.astralsorcery.common.perk.PerkManager;
import hellfirepvp.astralsorcery.common.perk.convert.PerkAttributeConverter;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.tree.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.tree.PerkCategory;
import hellfirepvp.astralsorcery.common.perk.tree.PerkType;
import hellfirepvp.astralsorcery.common.perk.tree.perk.RootPerk;
import hellfirepvp.astralsorcery.common.perk.tree.requirement.PerkRequirement;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.DiminishingMultiplier;
import hellfirepvp.astralsorcery.common.util.event.SidedEventBus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.Collection;
import java.util.Collections;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RootPerkEvorsio
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RootPerkEvorsio extends RootPerk<AbstractPerk.Data> {

    public static final MapCodec<RootPerkEvorsio> CODEC = RecordCodecBuilder.mapCodec(inst -> perkRootFields(inst).apply(inst, RootPerkEvorsio::new));
    public static final PerkType<RootPerkEvorsio> TYPE =
            PerkType.of(RootPerkEvorsio.CODEC, PerkDataTypesAS.DEFAULT_DATA, RootPerkEvorsio::new);
    public static final Config CONFIG = new Config("root.evorsio");

    private RootPerkEvorsio(ResourceLocation key, float x, float y) {
        this(key, defaultNameKey(key), x, y, PerkCategory.ROOT, Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), ConstellationsAS.EVORSIO.get());
    }

    protected RootPerkEvorsio(ResourceLocation key, String nameKey, float x, float y, PerkCategory category, Collection<PerkRequirement> requirements, Collection<PerkAttributeConverter> converters, Collection<PerkAttributeModifier> modifiers, BaseConstellation constellation) {
        super(key, nameKey, x, y, category, requirements, converters, modifiers, constellation);
    }

    @Override
    protected Config getConfig() {
        return CONFIG;
    }

    @Override
    protected DiminishingMultiplier createMultiplier() {
        return DiminishingMultiplier.of()
                .multiplierReGainTime(30)
                .multiplierReGainRate(0.1F)
                .multiplierLossRate(0.005F)
                .minMultiplier(0.15F)
                .build();
    }

    @Override
    protected void attachEventListeners(SidedEventBus sidedEventBus) {
        super.attachEventListeners(sidedEventBus);
        sidedEventBus.addListener(BlockEvent.BreakEvent.class, SidedEventBus.blockEvent(), this::onBreak);
    }

    protected void onBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer sPlayer)) return;
        LogicalSide side = this.getSide(sPlayer);
        if (!side.isServer()) return;
        PlayerProgress progress = ResearchManager.getProgress(sPlayer, side);
        if (!progress.getPerkData().hasPerkEffect(this)) return;
        PerkAttributeMap perkMap = PerkManager.getOrCreateAttributes(sPlayer);

        float breakSpeed = event.getState().getDestroySpeed(event.getLevel(), event.getPos());
        if (breakSpeed < 0) return;
        breakSpeed = Math.max(0.005F, breakSpeed);

        float xp = breakSpeed * 5F;
        xp *= this.getExpMultiplier();
        xp *= this.getDiminishingMultiplier(sPlayer);
        xp *= perkMap.getModifier(sPlayer, progress, PerksAS.AttributeTypes.PERK_EFFECT);
        xp *= perkMap.getModifier(sPlayer, progress, PerksAS.AttributeTypes.PERK_EXPERIENCE);

        xp = AttributeEvent.postProcessModded(sPlayer, PerksAS.AttributeTypes.PERK_EXPERIENCE, xp);

        ResearchHelper.addPerkExp(sPlayer, xp);
    }

    @Override
    public PerkType<?> getType() {
        return TYPE;
    }
}
