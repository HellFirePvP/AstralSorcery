/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event;

import hellfirepvp.astralsorcery.core.ASMCallHook;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeEvent
 * Created by HellFirePvP
 * Date: 17.11.2018 / 10:51
 */
public class AttributeEvent {

    public static class PostProcess extends Event {

        private final ModifiableAttributeInstance instance;
        private double value;

        public PostProcess(ModifiableAttributeInstance instance, double value) {
            this.instance = instance;
            this.value = value;
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }

        public ModifiableAttributeInstance getInstance() {
            return instance;
        }

        public IAttribute getAttribute() {
            return instance.getAttribute();
        }

    }

    @ASMCallHook
    public static double postProcess(double value, ModifiableAttributeInstance instance) {
        PostProcess ev = new PostProcess(instance, value);
        MinecraftForge.EVENT_BUS.post(ev);
        return ev.getAttribute().clampValue(ev.getValue()); //Cause that happened before our call already...
    }

}
