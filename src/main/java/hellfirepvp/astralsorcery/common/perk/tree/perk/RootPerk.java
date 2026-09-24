/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.tree.perk;

import com.mojang.datafixers.Products;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.config.ConfigEntry;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lib.types.PerkDataTypesAS;
import hellfirepvp.astralsorcery.common.perk.convert.PerkAttributeConverter;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.tree.*;
import hellfirepvp.astralsorcery.common.perk.tree.point.ConstellationPerkTreePoint;
import hellfirepvp.astralsorcery.common.perk.tree.requirement.PerkRequirement;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.util.DiminishingMultiplier;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.codec.CodecProducts;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RootPerk
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class RootPerk<D extends AbstractPerk.Data> extends AttributeModifierPerk<D> {

    protected static <T extends RootPerk<?>> Products.P9<RecordCodecBuilder.Mu<T>, ResourceLocation, String, Float, Float, PerkCategory, Set<PerkRequirement>, Set<PerkAttributeConverter>, Set<PerkAttributeModifier>, BaseConstellation> perkRootFields(RecordCodecBuilder.Instance<T> instance) {
        return CodecProducts.and(perkModifierFields(instance),
                RegistriesAS.REGISTRY_CONSTELLATIONS.byNameCodec().fieldOf("constellation").forGetter(RootPerk::getConstellation)
        );
    }

    private BaseConstellation constellation;
    private final Map<UUID, DiminishingMultiplier> diminishingMultipliers = new HashMap<>();

    protected RootPerk(ResourceLocation key,
                       String nameKey,
                       float x,
                       float y,
                       PerkCategory category,
                       Collection<PerkRequirement> requirements,
                       Collection<PerkAttributeConverter> converters,
                       Collection<PerkAttributeModifier> modifiers,
                       BaseConstellation constellation) {
        super(key, nameKey, x, y, category, requirements, converters, modifiers);
        this.constellation = constellation;
    }

    public <P extends RootPerk<?>> P setConstellation(BaseConstellation constellation) {
        AstralSorcery.assertDataGeneration();
        this.constellation = constellation;
        return MiscUtil.cast(this);
    }

    public final BaseConstellation getConstellation() {
        return this.constellation;
    }

    protected float getExpMultiplier() {
        return this.getConfig().expMultiplier.get().floatValue();
    }

    protected float getDiminishingMultiplier(ServerPlayer sPlayer) {
        return this.diminishingMultipliers.computeIfAbsent(sPlayer.getUUID(), uuid -> this.createMultiplier()).getMultiplier(sPlayer);
    }

    protected abstract Config getConfig();

    protected DiminishingMultiplier createMultiplier() {
        return DiminishingMultiplier.of()
                .build();
    }

    @Override
    public void clearCaches(LogicalSide side) {
        super.clearCaches(side);

        if (side.isServer()) {
            this.diminishingMultipliers.clear();
        }
    }

    @Override
    public MutableComponent getName(PlayerProgress progress, Player player) {
        MutableComponent name = super.getName(progress, player);
        if (progress.hasDiscoveredConstellation(this.getConstellation())) {
            return name.withColor(this.getConstellation().getConstellationColor().getColor());
        }
        return name;
    }

    @Override
    protected PerkTreePoint<?> initPerkTreePoint() {
        return new ConstellationPerkTreePoint<>(this.getOffset(), this, this.getConstellation(), ConstellationPerkTreePoint.ROOT_SPRITE_SIZE);
    }

    @Override
    public boolean mayRemovePerk(PlayerProgress progress, Player player) {
        BaseConstellation attuned = progress.getAttunedConstellation().orElse(null);
        if (attuned == null) return true; // Who am i to stop you; godspeed corrupted-savefile soldier
        if (attuned.equals(this.getConstellation())) return false;
        return super.mayRemovePerk(progress, player);
    }

    public static class Config extends ConfigEntry {

        public ModConfigSpec.DoubleValue expMultiplier;

        public Config(String section) {
            super(section);
        }

        @Override
        public void createEntries(ModConfigSpec.Builder cfgBuilder) {
            this.expMultiplier = cfgBuilder
                    .comment("Experience multiplier for any exp gained from this root perk.")
                    .translation(translationKey("expMultiplier"))
                    .defineInRange("expMultiplier", 1D, 0.01D, 20F);
        }
    }
}
