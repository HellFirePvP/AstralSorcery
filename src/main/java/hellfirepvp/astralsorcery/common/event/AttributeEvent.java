/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event;

import hellfirepvp.astralsorcery.core.ASMCallHook;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeEvent
 * Created by HellFirePvP
 * Date: 17.11.2018 / 10:51
 */
public class AttributeEvent {

    private static final Field fAttributeMapEntity;

    public static class PostProcessVanilla extends Event {

        private final ModifiableAttributeInstance instance;
        private double value;

        public PostProcessVanilla(ModifiableAttributeInstance instance, double value) {
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
        PostProcessVanilla ev = new PostProcessVanilla(instance, value);
        MinecraftForge.EVENT_BUS.post(ev);
        return ev.getAttribute().clampValue(ev.getValue()); //Cause that happened before our call already...
    }

    @ASMCallHook
    public static AbstractAttributeMap markToPlayer(AbstractAttributeMap map, EntityLivingBase entity) {
        if (fAttributeMapEntity != null) {
            try {
                fAttributeMapEntity.set(map, entity);
            } catch (Exception ignored) {}
        }
        return map;
    }

    @Nullable
    public static EntityLivingBase getEntity(AbstractAttributeMap map) {
        if (fAttributeMapEntity != null) {
            try {
                return (EntityLivingBase) fAttributeMapEntity.get(map);
            } catch (Exception ignored) {}
        }
        return null;
    }

    static {
        Field f = null;
        try {
            f = AbstractAttributeMap.class.getDeclaredField("as_entity");
        } catch (Exception exc) {
            f = null;
        } finally {
            fAttributeMapEntity = f;
        }
    }

}
