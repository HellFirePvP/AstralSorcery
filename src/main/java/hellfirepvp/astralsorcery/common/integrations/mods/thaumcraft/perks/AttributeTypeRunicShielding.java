/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.thaumcraft.perks;

import hellfirepvp.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeType;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.event.AttributeEvent;
import hellfirepvp.astralsorcery.common.event.RunicShieldingCalculateEvent;
import hellfirepvp.astralsorcery.common.integrations.ModIntegrationThaumcraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeTypeRunicShielding
 * Created by HellFirePvP
 * Date: 18.11.2018 / 22:16
 */
public class AttributeTypeRunicShielding extends PerkAttributeType {

    public AttributeTypeRunicShielding() {
        super(ModIntegrationThaumcraft.ATTR_TYPE_RUNIC_SHIELDING);
    }

    @SubscribeEvent
    public void on(RunicShieldingCalculateEvent event) {
        float val = event.getRunicShieldingValue();
        EntityPlayer player = event.getEntityPlayer();
        Side side = player.world.isRemote ? Side.CLIENT : Side.SERVER;
        PlayerProgress prog = ResearchManager.getProgress(player, side);
        val = PerkAttributeHelper.getOrCreateMap(player, side)
                .modifyValue(player, prog, ModIntegrationThaumcraft.ATTR_TYPE_RUNIC_SHIELDING, val);

        val = AttributeEvent.postProcessModded(player, this, val);
        event.setRunicShieldingValue(Math.round(val));
    }

}
