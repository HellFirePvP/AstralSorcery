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
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeType;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.type.AttributeBreakSpeed;
import hellfirepvp.astralsorcery.common.constellation.perk.reader.PerkStatistic;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.event.AttributeEvent;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BreakSpeedAttributeReader
 * Created by HellFirePvP
 * Date: 19.01.2019 / 13:05
 */
public class BreakSpeedAttributeReader extends FlatAttributeReader {

    public BreakSpeedAttributeReader(PerkAttributeType attribute) {
        super(attribute, 1F);
    }

    @Override
    public double getDefaultValue(PlayerAttributeMap statMap, EntityPlayer player, Side side) {
        AttributeBreakSpeed.evaluateBreakSpeedWithoutPerks = true;
        float speed;
        try {
            speed = player.getDigSpeed(Blocks.COBBLESTONE.getDefaultState(), BlockPos.ORIGIN);
        } finally {
            AttributeBreakSpeed.evaluateBreakSpeedWithoutPerks = false;
        }
        return speed;
    }

    @Override
    public PerkStatistic getStatistics(PlayerAttributeMap statMap, EntityPlayer player) {
        Float limit = AttributeTypeLimiter.INSTANCE.getMaxLimit(this.attribute);
        String limitStr = limit == null ? "" :
                I18n.format("perk.reader.limit.percent", MathHelper.floor(limit * 100F));

        double value = player.getDigSpeed(Blocks.COBBLESTONE.getDefaultState(), BlockPos.ORIGIN);

        String postProcess = "";
        double post = AttributeEvent.postProcessModded(player, this.attribute, value);
        if (Math.abs(value - post) > 1E-4 &&
                (limit == null || Math.abs(post - limit) > 1E-4)) {
            if (Math.abs(post) >= 1E-4) {
                postProcess = I18n.format("perk.reader.postprocess.default",
                        (post >= 0 ? "+" : "") + formatDecimal(post));
            }
            value = post;
        }

        String strOut = (value >= 0 ? "+" : "") + formatDecimal(value);
        return new PerkStatistic(this.attribute, strOut, limitStr, postProcess);
    }
}
