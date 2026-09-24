/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event;

import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.perk.type.base.PerkAttributeType;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AttributeEvent
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AttributeEvent {

    public static class PostProcessVanilla extends Event {

        private final AttributeInstance instance;
        private final double originalValue;
        private double value;

        public PostProcessVanilla(AttributeInstance instance, double value) {
            this.instance = instance;
            this.originalValue = value;
            this.value = value;
        }

        public double getOriginalValue() {
            return this.originalValue;
        }

        public double getValue() {
            return this.value;
        }

        public void setValue(double value) {
            this.value = value;
        }

        public AttributeInstance getInstance() {
            return this.instance;
        }

        public Holder<Attribute> getAttribute() {
            return this.instance.getAttribute();
        }

        public Optional<PerkAttributeType> resolveAttributeType() {
            return PerkAttributeType.fromVanillaType(this.getAttribute());
        }
    }

    public static class PostProcessModded extends Event {

        private final Player player;
        private final PerkAttributeType type;
        private final double originalValue;
        private double value;

        public PostProcessModded(double value, PerkAttributeType type, Player player) {
            this.player = player;
            this.type = type;
            this.originalValue = value;
            this.value = value;
        }

        public double getOriginalValue() {
            return this.originalValue;
        }

        public double getValue() {
            return this.value;
        }

        public void setValue(double value) {
            this.value = value;
        }

        public PerkAttributeType getType() {
            return this.type;
        }

        public Player getPlayer() {
            return this.player;
        }
    }

    public static double postProcessModded(Player player, Supplier<? extends PerkAttributeType> type, double value) {
        PostProcessModded ev = new PostProcessModded(value, type.get(), player);
        NeoForge.EVENT_BUS.post(ev);
        return ev.getValue();
    }

    public static float postProcessModded(Player player, Supplier<? extends PerkAttributeType> type, float value) {
        return (float) postProcessModded(player, type, (double) value);
    }

    public static double postProcessModded(Player player, PerkAttributeType type, double value) {
        PostProcessModded ev = new PostProcessModded(value, type, player);
        NeoForge.EVENT_BUS.post(ev);
        return ev.getValue();
    }

    public static float postProcessModded(Player player, PerkAttributeType type, float value) {
        return (float) postProcessModded(player, type, (double) value);
    }

    public static double postProcessModded(Player player, ResourceLocation key, double value) {
        PerkAttributeType pType = RegistriesAS.REGISTRY_PERK_ATTRIBUTE_TYPES.get(key);
        if (pType == null) return value;
        return postProcessModded(player, pType, value);
    }

    public static float postProcessModded(Player player, ResourceLocation key, float value) {
        return (float) postProcessModded(player, key, (double) value);
    }

    public static double postProcessVanilla(double value, AttributeInstance attribute) {
        AttributeEvent.PostProcessVanilla event = new AttributeEvent.PostProcessVanilla(attribute, value);
        NeoForge.EVENT_BUS.post(event);
        return attribute.getAttribute().value().sanitizeValue(event.getValue());
    }
}
