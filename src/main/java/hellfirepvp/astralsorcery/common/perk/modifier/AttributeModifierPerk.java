/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.modifier;

import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.perk.data.PerkTypeHandler;
import hellfirepvp.astralsorcery.common.perk.source.AttributeModifierProvider;
import hellfirepvp.astralsorcery.common.perk.type.ModifierType;
import hellfirepvp.astralsorcery.common.perk.type.PerkAttributeType;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeModifierPerk
 * Created by HellFirePvP
 * Date: 08.08.2019 / 18:11
 */
//Usable for most/many cases. Handles also all basic stuff around modifiers and converters
public class AttributeModifierPerk extends AttributeConverterPerk implements AttributeModifierProvider {

    private final Set<PerkAttributeModifier> modifiers = Sets.newHashSet();

    public AttributeModifierPerk(ResourceLocation name, float x, float y) {
        super(name, x, y);
    }

    @Nonnull
    public <V extends AttributeModifierPerk> V addModifier(float modifier, ModifierType mode, PerkAttributeType type) {
        return (V) this.addModifier(type.createModifier(modifier, mode));
    }

    @Nonnull
    public <T extends PerkAttributeModifier, V extends AttributeModifierPerk> V addModifier(T modifier) {
        this.modifiers.add(modifier);
        return (V) this;
    }

    @Override
    protected void applyEffectMultiplier(float multiplier) {
        super.applyEffectMultiplier(multiplier);

        this.modifiers.forEach(t -> t.multiplyValue(multiplier));
    }

    @Override
    public Collection<PerkAttributeModifier> getModifiers(PlayerEntity player, LogicalSide side, boolean ignoreRequirements) {
        if (modifiersDisabled(player, side)) {
            return Collections.emptyList();
        }
        if (!ignoreRequirements && ResearchHelper.getProgress(player, side).isPerkSealed(this)) {
            return Collections.emptyList();
        }

        return new ArrayList<>(this.modifiers);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean addLocalizedTooltip(Collection<ITextComponent> tooltip) {
        Collection<PerkAttributeModifier> modifiers = this.getModifiers(Minecraft.getInstance().player, LogicalSide.CLIENT, true);
        boolean addEmptyLine = !modifiers.isEmpty();

        if (canSeeClient()) {
            for (PerkAttributeModifier modifier : modifiers) {
                String modifierDisplay = modifier.getLocalizedDisplayString();
                if (modifierDisplay != null) {
                    tooltip.add(new StringTextComponent(modifierDisplay));
                } else {
                    addEmptyLine = false;
                }
            }
        }

        return addEmptyLine;
    }

    @Override
    public void deserializeData(JsonObject perkData) {
        super.deserializeData(perkData);

        this.modifiers.clear();

        if (JSONUtils.hasField(perkData, "modifiers")) {
            JsonArray array = JSONUtils.getJsonArray(perkData, "modifiers");
            for (int i = 0; i < array.size(); i++) {
                JsonObject serializedModifier = JSONUtils.getJsonObject(array.get(i), "modifiers[%s]");

                if (serializedModifier.has("custom")) {
                    String customKey = JSONUtils.getString(serializedModifier, "custom");
                    PerkAttributeModifier customModifier = RegistriesAS.REGISTRY_PERK_CUSTOM_MODIFIERS.getValue(new ResourceLocation(customKey));
                    if (customModifier == null) {
                        throw new IllegalArgumentException("Unknown specified modifier: " + customKey);
                    }
                    this.addModifier(customModifier);
                } else {
                    String typeKey = JSONUtils.getString(serializedModifier, "type");
                    PerkAttributeType type = RegistriesAS.REGISTRY_PERK_ATTRIBUTE_TYPES.getValue(new ResourceLocation(typeKey));
                    if (type == null) {
                        throw new IllegalArgumentException("Unknown modifier type: " + typeKey);
                    }
                    String modeKey = JSONUtils.getString(serializedModifier, "mode");
                    ModifierType mode;
                    try {
                        mode = ModifierType.valueOf(modeKey);
                    } catch (Exception exc) {
                        throw new IllegalArgumentException("Unknown mode: " + modeKey);
                    }
                    float value = JSONUtils.getFloat(serializedModifier, "value");

                    this.addModifier(value, mode, type);
                }
            }
        }
    }

    @Override
    public void serializeData(JsonObject perkData) {
        super.serializeData(perkData);

        if (!this.modifiers.isEmpty()) {
            JsonArray array = new JsonArray();
            for (PerkAttributeModifier modifier : this.modifiers) {
                if (modifier instanceof DynamicAttributeModifier) {
                    continue;
                }

                JsonObject serializedModifier = new JsonObject();
                if (modifier.getRegistryName() != null) {
                    serializedModifier.addProperty("custom", modifier.getRegistryName().toString());
                } else {
                    serializedModifier.addProperty("type", modifier.getAttributeType().getRegistryName().toString());
                    serializedModifier.addProperty("mode", modifier.getMode().name());
                    serializedModifier.addProperty("value", modifier.getRawValue());
                }
                array.add(serializedModifier);
            }
            perkData.add("modifiers", array);
        }
    }
}

