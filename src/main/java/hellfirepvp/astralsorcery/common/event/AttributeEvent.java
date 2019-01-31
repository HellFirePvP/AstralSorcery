/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event;

import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeType;
import hellfirepvp.astralsorcery.core.ASMCallHook;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
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
        private final double originalValue;
        private double value;

        public PostProcessVanilla(ModifiableAttributeInstance instance, double value) {
            this.instance = instance;
            this.originalValue = value;
            this.value = value;
        }

        public double getOriginalValue() {
            return originalValue;
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

        @Nullable
        public PerkAttributeType resolveAttributeType() {
            return AttributeTypeRegistry.findType(getAttribute());
        }

        @Nullable
        public EntityLivingBase getEntityLiving() {
            return getEntity(this.getInstance().attributeMap);
        }

        @Nullable
        public EntityPlayer getPlayer() {
            EntityLivingBase owner = getEntityLiving();
            if (owner != null && owner instanceof EntityPlayer) {
                return (EntityPlayer) owner;
            }
            return null;
        }

    }

    public static class PostProcessModded extends Event {

        private final EntityPlayer player;
        private final PerkAttributeType type;
        private final double originalValue;
        private double value;

        public PostProcessModded(double value, PerkAttributeType type, EntityPlayer player) {
            this.player = player;
            this.type = type;
            this.originalValue = value;
            this.value = value;
        }

        public double getOriginalValue() {
            return originalValue;
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }

        public PerkAttributeType getType() {
            return type;
        }

        public EntityPlayer getPlayer() {
            return player;
        }
    }

    public static double postProcessModded(EntityPlayer player, PerkAttributeType type, double value) {
        PostProcessModded ev = new PostProcessModded(value, type, player);
        MinecraftForge.EVENT_BUS.post(ev);
        return ev.getValue();
    }

    public static float postProcessModded(EntityPlayer player, PerkAttributeType type, float value) {
        return (float) postProcessModded(player, type, (double) value);
    }

    public static double postProcessModded(EntityPlayer player, String type, double value) {
        PerkAttributeType pType = AttributeTypeRegistry.getType(type);
        if (pType == null) {
            return value;
        }
        return postProcessModded(player, pType, value);
    }

    public static float postProcessModded(EntityPlayer player, String type, float value) {
        return (float) postProcessModded(player, type, (double) value);
    }

    @ASMCallHook
    public static double postProcessVanilla(double value, ModifiableAttributeInstance instance) {
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
    private static EntityLivingBase getEntity(AbstractAttributeMap map) {
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
