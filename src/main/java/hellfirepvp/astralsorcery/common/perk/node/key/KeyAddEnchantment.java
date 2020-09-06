/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.node.key;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.enchantment.dynamic.DynamicEnchantment;
import hellfirepvp.astralsorcery.common.enchantment.dynamic.DynamicEnchantmentType;
import hellfirepvp.astralsorcery.common.event.DynamicEnchantmentEvent;
import hellfirepvp.astralsorcery.common.perk.node.KeyPerk;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyAddEnchantment
 * Created by HellFirePvP
 * Date: 25.08.2019 / 19:08
 */
public class KeyAddEnchantment extends KeyPerk {

    private final List<DynamicEnchantment> enchantments = Lists.newArrayList();

    public KeyAddEnchantment(ResourceLocation name, float x, float y) {
        super(name, x, y);
    }

    @Override
    public void attachListeners(IEventBus bus) {
        super.attachListeners(bus);
        bus.addListener(this::onEnchantmentAdd);
    }

    public KeyAddEnchantment addEnchantment(Enchantment ench, int level) {
        return addEnchantment(DynamicEnchantmentType.ADD_TO_SPECIFIC, ench, level);
    }

    public KeyAddEnchantment addEnchantment(DynamicEnchantmentType type, Enchantment ench, int level) {
        this.enchantments.add(new DynamicEnchantment(type, ench, level));
        return this;
    }

    public KeyAddEnchantment addAllEnchantmentIncrease(int level) {
        this.enchantments.add(new DynamicEnchantment(DynamicEnchantmentType.ADD_TO_EXISTING_ALL, level));
        return this;
    }

    private void onEnchantmentAdd(DynamicEnchantmentEvent.Add event) {
        PlayerEntity player = event.getResolvedPlayer();
        LogicalSide side = this.getSide(player);
        PlayerProgress prog = ResearchHelper.getProgress(player, side);
        if (prog.hasPerkEffect(this)) {
            List<DynamicEnchantment> listedEnchantments = event.getEnchantmentsToApply();
            for (DynamicEnchantment ench : this.enchantments) {
                DynamicEnchantment added = MiscUtils.iterativeSearch(listedEnchantments, e ->
                        (e.getEnchantment() == null ? ench.getEnchantment() == null : e.getEnchantment().equals(ench.getEnchantment())) &&
                                e.getType().equals(ench.getType()));
                if (added != null) {
                    added.setLevelAddition(added.getLevelAddition() + ench.getLevelAddition());
                } else {
                    listedEnchantments.add(ench.copy());
                }
            }
        }
    }

    @Override
    public void deserializeData(JsonObject perkData) {
        super.deserializeData(perkData);

        this.enchantments.clear();

        if (perkData.has("enchantments")) {
            JsonArray array = JSONUtils.getJsonArray(perkData, "enchantments");
            for (int i = 0; i < array.size(); i++) {
                JsonObject serializedEnchantment = JSONUtils.getJsonObject(array.get(i), "enchantments[%s]");

                String typeKey = JSONUtils.getString(serializedEnchantment, "type");
                DynamicEnchantmentType type;
                try {
                    type = DynamicEnchantmentType.valueOf(typeKey);
                } catch (Exception exc) {
                    throw new IllegalArgumentException("Unknown dynamic enchantment type: " + typeKey);
                }
                int level = JSONUtils.getInt(serializedEnchantment, "level");

                if (type.isEnchantmentSpecific()) {
                    String enchantmentKey = JSONUtils.getString(serializedEnchantment, "enchantment");
                    Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(enchantmentKey));
                    if (ench == null) {
                        throw new IllegalArgumentException("Unknown Enchantment: " + enchantmentKey);
                    }
                    this.addEnchantment(type, ench, level);
                } else {
                    this.addAllEnchantmentIncrease(level);
                }
            }
        }
    }

    @Override
    public void serializeData(JsonObject perkData) {
        super.serializeData(perkData);

        if (!this.enchantments.isEmpty()) {
            JsonArray array = new JsonArray();

            for (DynamicEnchantment enchantment : this.enchantments) {
                JsonObject serializedEnchantment = new JsonObject();

                serializedEnchantment.addProperty("type", enchantment.getType().name());
                if (enchantment.getEnchantment() != null) {
                    serializedEnchantment.addProperty("enchantment", enchantment.getEnchantment().getRegistryName().toString());
                }
                serializedEnchantment.addProperty("level", enchantment.getLevelAddition());

                array.add(serializedEnchantment);
            }

            perkData.add("enchantments", array);
        }
    }
}
