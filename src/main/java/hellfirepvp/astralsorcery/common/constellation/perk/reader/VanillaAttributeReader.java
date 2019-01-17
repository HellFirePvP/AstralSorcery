/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.reader;

import hellfirepvp.astralsorcery.common.constellation.perk.PlayerAttributeMap;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeLimiter;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeType;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.ai.attributes.IAttribute;
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

    private IAttribute attribute;
    private PerkAttributeType perkAttrType;

    private boolean formatAsDecimal = false;

    public VanillaAttributeReader(IAttribute attribute) {
         this.attribute = attribute;
         this.perkAttrType = AttributeTypeRegistry.findType(this.attribute);
    }

    public <T extends VanillaAttributeReader> T formatAsDecimal() {
        this.formatAsDecimal = true;
        return (T) this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getStatString(PlayerAttributeMap statMap, EntityPlayer player, Side side) {
        if (this.perkAttrType == null) {
            return "";
        }

        String format = I18n.format("perk.reader.display.default");

        Float limit = AttributeTypeLimiter.INSTANCE.getMaxLimit(this.perkAttrType);
        String limitStr = limit == null ? "" : I18n.format("perk.reader.limit.default", limit.toString());

        double value = player.getEntityAttribute(attribute).getAttributeValue();
        String valueStr;
        if (this.formatAsDecimal) {
            valueStr = formatDecimal(value);
        } else {
            valueStr = String.valueOf(MathHelper.floor(value));
        }
        return String.format(format,
                I18n.format(this.perkAttrType.getUnlocalizedName()),
                valueStr,
                limitStr);
    }

}
