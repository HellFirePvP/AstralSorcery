/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event;

import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.perk.type.PerkAttributeType;
import hellfirepvp.astralsorcery.common.perk.type.PerkAttributeTypeHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeEvent
 * Created by HellFirePvP
 * Date: 08.08.2019 / 06:57
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
            return PerkAttributeTypeHelper.findVanillaType(getAttribute());
        }

        @Nullable
        public LivingEntity getEntityLiving() {
            return getEntity(this.getInstance().attributeMap);
        }

        @Nullable
        public PlayerEntity getPlayer() {
            LivingEntity owner = getEntityLiving();
            if (owner instanceof PlayerEntity) {
                return (PlayerEntity) owner;
            }
            return null;
        }

    }

    public static class PostProcessModded extends Event {

        private final PlayerEntity player;
        private final PerkAttributeType type;
        private final double originalValue;
        private double value;

        public PostProcessModded(double value, PerkAttributeType type, PlayerEntity player) {
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

        public PlayerEntity getPlayer() {
            return player;
        }
    }

    public static double postProcessModded(PlayerEntity player, PerkAttributeType type, double value) {
        PostProcessModded ev = new PostProcessModded(value, type, player);
        MinecraftForge.EVENT_BUS.post(ev);
        return ev.getValue();
    }

    public static float postProcessModded(PlayerEntity player, PerkAttributeType type, float value) {
        return (float) postProcessModded(player, type, (double) value);
    }

    public static double postProcessModded(PlayerEntity player, ResourceLocation key, double value) {
        PerkAttributeType pType = RegistriesAS.REGISTRY_PERK_ATTRIBUTE_TYPES.getValue(key);
        if (pType == null) {
            return value;
        }
        return postProcessModded(player, pType, value);
    }

    public static float postProcessModded(PlayerEntity player, ResourceLocation key, float value) {
        return (float) postProcessModded(player, key, (double) value);
    }

    public static double postProcessVanilla(double value, ModifiableAttributeInstance attribute) {
        AttributeEvent.PostProcessVanilla event = new AttributeEvent.PostProcessVanilla(attribute, value);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getAttribute().clampValue(event.getValue());
    }

    @Nullable
    private static LivingEntity getEntity(AbstractAttributeMap map) {
        if (fAttributeMapEntity != null) {
            try {
                return (LivingEntity) fAttributeMapEntity.get(map);
            } catch (Exception ignored) {}
        }
        return null;
    }

    public static void setEntity(AbstractAttributeMap map, LivingEntity entity) {
        try {
            fAttributeMapEntity.set(map, entity);
        } catch (Exception ignored) {}
    }

    static {
        Field f = null;
        try {
            //Added via ASM
            f = AbstractAttributeMap.class.getDeclaredField("as_entity");
        } catch (Exception exc) {
            f = null;
        } finally {
            fAttributeMapEntity = f;
        }
    }
}
