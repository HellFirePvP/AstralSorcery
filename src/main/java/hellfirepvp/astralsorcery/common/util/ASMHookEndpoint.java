/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.common.event.AttributeEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.MinecraftForge;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ASMHookEndpoint
 * Created by HellFirePvP
 * Date: 08.08.2019 / 06:52
 */
public class ASMHookEndpoint {

    public static AbstractAttributeMap markPlayer(AbstractAttributeMap map, LivingEntity entity) {
        AttributeEvent.setEntity(map, entity);
        return map;
    }

    public static double postProcessVanilla(double value, ModifiableAttributeInstance attributeInstance) {
        AttributeEvent.PostProcessVanilla event = new AttributeEvent.PostProcessVanilla(attributeInstance, value);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getAttribute().clampValue(event.getValue());
    }

}
