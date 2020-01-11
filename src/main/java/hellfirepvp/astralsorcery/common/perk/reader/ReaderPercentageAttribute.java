/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.reader;

import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.event.AttributeEvent;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeLimiter;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeMap;
import hellfirepvp.astralsorcery.common.perk.type.ModifierType;
import hellfirepvp.astralsorcery.common.perk.type.PerkAttributeType;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import org.apache.commons.lang3.tuple.Pair;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ReaderPercentageAttribute
 * Created by HellFirePvP
 * Date: 25.08.2019 / 17:59
 */
public class ReaderPercentageAttribute extends PerkAttributeReader {

    private double defaultValue;

    public ReaderPercentageAttribute(PerkAttributeType type) {
        super(type);
        this.defaultValue = type.isMultiplicative() ? 1.0 : 0;
    }

    public <T extends ReaderPercentageAttribute> T setDefaultValue(float defaultValue) {
        if (!getType().isMultiplicative()) { //Percentage modifiers with a non-zero base make no sense
            this.defaultValue = defaultValue;
        }
        return (T) this;
    }

    @Override
    public double getDefaultValue(PerkAttributeMap statMap, PlayerEntity player, LogicalSide side) {
        return this.defaultValue;
    }

    @Override
    public double getModifierValueForMode(PerkAttributeMap statMap, PlayerEntity player, LogicalSide side, ModifierType mode) {
        return statMap.getModifier(player, ResearchHelper.getProgress(player, side), this.getType(), mode);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public PerkStatistic getStatistics(PerkAttributeMap statMap, PlayerEntity player) {
        String limitStr = "";
        Double limit = null;
        if (PerkAttributeLimiter.hasLimit(this.getType())) {
            Pair<Double, Double> limits = PerkAttributeLimiter.getLimit(this.getType());
            limit = limits.getRight();
            limitStr = I18n.format("perk.reader.astralsorcery.limit.percent", MathHelper.floor(limit * 100));
        }
        double value = statMap.modifyValue(player, ResearchHelper.getProgress(player, LogicalSide.CLIENT),
                this.getType(), (float) getDefaultValue(statMap, player, LogicalSide.CLIENT));

        String postProcess = "";
        double postValue = AttributeEvent.postProcessModded(player, this.getType(), value);
        if (Math.abs(value - postValue) > 1E-4 &&
                (limit == null || Math.abs(postValue - limit) > 1E-4)) {
            if (Math.abs(postValue) >= 1E-4) {
                postProcess = I18n.format("perk.reader.astralsorcery.postprocess.default", formatForDisplay(postValue));
            }
            value = postValue;
        }

        if (getType().isMultiplicative()) {
            value -= 1F;
        }

        return new PerkStatistic(this.getType(), formatForDisplay(value), limitStr, postProcess);
    }

    protected String formatForDisplay(double value) {
        value *= 100F;
        return (value >= 0 ? "+" : "") + formatDecimal(value) + "%";
    }
}
