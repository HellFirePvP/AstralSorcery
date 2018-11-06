/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.attribute.type;

import hellfirepvp.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DynamicPlayerAttributeModifier
 * Created by HellFirePvP
 * Date: 08.07.2018 / 15:33
 */
public class DynamicPlayerAttributeModifier extends AttributeModifier {

    private EntityPlayer player;
    private Side side;
    private String type;

    public DynamicPlayerAttributeModifier(UUID idIn, String nameIn, String typeIn, PerkAttributeModifier.Mode mode, EntityPlayer player, Side side) {
        this(idIn, nameIn, typeIn, mode.getVanillaAttributeOperation(), player, side);
    }

    public DynamicPlayerAttributeModifier(UUID idIn, String nameIn, String typeIn, int operationIn, EntityPlayer player, Side side) {
        super(idIn, nameIn, 0, operationIn);
        this.setSaved(false);
        this.player = player;
        this.side = side;
        this.type = typeIn;
    }

    @Override
    public double getAmount() {
        PerkAttributeModifier.Mode mode = PerkAttributeModifier.Mode.fromVanillaAttributeOperation(getOperation());
        double modifier = PerkAttributeHelper.getOrCreateMap(player, side).getModifier(AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT);
        return (PerkAttributeHelper.getOrCreateMap(player, side).getModifier(type, mode) - 1) * modifier;
    }
}
