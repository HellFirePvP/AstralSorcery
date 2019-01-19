/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.reader.impl;

import hellfirepvp.astralsorcery.common.constellation.perk.PlayerAttributeMap;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeLimiter;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeType;
import hellfirepvp.astralsorcery.common.constellation.perk.reader.AttributeReader;
import hellfirepvp.astralsorcery.common.constellation.perk.reader.PerkStatistic;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.event.AttributeEvent;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: VanillaAttributeReader
 * Created by HellFirePvP
 * Date: 16.01.2019 / 15:31
 */
public class VanillaAttributeReader extends AttributeReader {

    protected IAttribute attribute;
    protected PerkAttributeType perkAttrType;

    protected boolean formatAsDecimal = false;

    public VanillaAttributeReader(IAttribute attribute) {
         this.attribute = attribute;
         this.perkAttrType = AttributeTypeRegistry.findType(this.attribute);

         if (this.perkAttrType == null) {
             throw new IllegalArgumentException("Cannot create reader for unknown vanilla type attribute!");
         }
    }

    public <T extends VanillaAttributeReader> T formatAsDecimal() {
        this.formatAsDecimal = true;
        return (T) this;
    }

    @Override
    public double getDefaultValue(PlayerAttributeMap statMap, EntityPlayer player, Side side) {
        return player.getEntityAttribute(this.attribute).getBaseValue();
    }

    @Override
    public double getModifierValueForMode(PlayerAttributeMap statMap, EntityPlayer player, Side side,
                                          PerkAttributeModifier.Mode mode) {
        return statMap.getModifier(player, ResearchManager.getProgress(player, side),
                this.perkAttrType.getTypeString(), mode);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public PerkStatistic getStatistics(PlayerAttributeMap statMap, EntityPlayer player) {
        Float limit = AttributeTypeLimiter.INSTANCE.getMaxLimit(this.perkAttrType);
        String limitStr = limit == null ? "" : I18n.format("perk.reader.limit.default", formatDecimal(limit));

        double value = getDefaultValue(statMap, player, Side.CLIENT);
        value = statMap.modifyValue(player, ResearchManager.getProgress(player, Side.CLIENT),
                this.perkAttrType.getTypeString(), (float) value);

        String postProcess = "";
        double post = AttributeEvent.postProcessVanilla(value,
                (ModifiableAttributeInstance) player.getEntityAttribute(this.attribute));
        if (Math.abs(value - post) > 1E-4 &&
                (limit == null || Math.abs(post - limit) > 1E-4)) {
            if (Math.abs(post) >= 1E-4) {
                postProcess = I18n.format("perk.reader.postprocess.default", formatForDisplay(post));
            }
            value = post;
        }

        return new PerkStatistic(this.perkAttrType, formatForDisplay(value), limitStr, postProcess);
    }

    protected String formatForDisplay(double value) {
        String valueStr;
        if (this.formatAsDecimal) {
            valueStr = formatDecimal(value);
        } else {
            valueStr = String.valueOf(MathHelper.floor(value));
        }
        return valueStr;
    }

}
