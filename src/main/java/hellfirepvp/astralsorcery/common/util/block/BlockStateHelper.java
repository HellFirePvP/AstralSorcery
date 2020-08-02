/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.block;

import com.google.common.base.Splitter;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.IProperty;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockStateHelper
 * Created by HellFirePvP
 * Date: 21.04.2019 / 09:25
 */
public class BlockStateHelper {

    private static final Splitter PROP_SPLITTER = Splitter.on(',');
    private static final Splitter PROP_ELEMENT_SPLITTER = Splitter.on('=');

    @Nonnull
    public static String serialize(@Nonnull Block block) {
        return block.getRegistryName().toString();
    }

    @Nonnull
    public static <V extends Comparable<V>> String serialize(@Nonnull BlockState state) {
        StringBuilder name = new StringBuilder(state.getBlock().getRegistryName().toString());
        List<IProperty<?>> props = new ArrayList<>(state.getProperties());
        if (!props.isEmpty()) {
            name.append('[');
            for (int i = 0; i < props.size(); i++) {
                IProperty<V> prop = (IProperty<V>) props.get(i);
                if (i > 0) {
                    name.append(',');
                }
                name.append(prop.getName());
                name.append('=');
                name.append(prop.getName(state.get(prop)));
            }
            name.append(']');
        }
        return name.toString();
    }

    @Nonnull
    public static <V extends Comparable<V>> JsonObject serializeObject(BlockState state, boolean serializeProperties) {
        JsonObject object = new JsonObject();
        object.addProperty("block", state.getBlock().getRegistryName().toString());
        if (serializeProperties && !state.getProperties().isEmpty()) {
            JsonArray properties = new JsonArray();
            for (IProperty<?> property : state.getProperties()) {
                IProperty<V> prop = (IProperty<V>) property;

                JsonObject objProperty = new JsonObject();
                objProperty.addProperty("name", prop.getName());
                objProperty.addProperty("value", prop.getName(state.get(prop)));
                properties.add(objProperty);
            }
            object.add("properties", properties);
        }
        return object;
    }

    @Nonnull
    public static Block deserializeBlock(@Nonnull String serialized) {
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(serialized));
        return block == null ? Blocks.AIR : block;
    }

    @Nonnull
    public static <T extends Comparable<T>> BlockState deserialize(@Nonnull String serialized) {
        int propIndex = serialized.indexOf('[');
        boolean hasProperties = isMissingStateInformation(serialized);
        ResourceLocation key;
        if (hasProperties) {
            key = new ResourceLocation(serialized.substring(0, propIndex).toLowerCase());
        } else {
            key = new ResourceLocation(serialized.toLowerCase());
        }
        Block block = ForgeRegistries.BLOCKS.getValue(key);
        BlockState state = block.getDefaultState();
        if (!block.equals(Blocks.AIR) && hasProperties) {
            List<String> strProps = PROP_SPLITTER.splitToList(serialized.substring(propIndex, serialized.length() - 1));
            for (String serializedProperty : strProps) {
                List<String> propertyValues = PROP_ELEMENT_SPLITTER.splitToList(serializedProperty);
                String name = propertyValues.get(0);
                String strValue = propertyValues.get(1);
                IProperty<T> property = (IProperty<T>) MiscUtils.iterativeSearch(state.getProperties(), prop -> prop.getName().equalsIgnoreCase(name));
                if (property != null) {
                    Optional<T> value = property.parseValue(strValue);
                    if (value.isPresent()) {
                        state = state.with(property, value.get());
                    }
                }
            }
        }
        return state;
    }

    @Nonnull
    public static <T extends Comparable<T>> BlockState deserializeObject(JsonObject object) {
        String key = JSONUtils.getString(object, "block");
        Block b = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(key));
        if (b == null || b instanceof AirBlock) {
            return Blocks.AIR.getDefaultState();
        }
        BlockState state = b.getDefaultState();
        if (isMissingStateInformation(object)) {
            return state;
        }
        if (JSONUtils.hasField(object, "properties")) {
            JsonArray properties = JSONUtils.getJsonArray(object, "properties");
            for (JsonElement elemProperty : properties) {
                JsonObject objProperty = JSONUtils.getJsonObject(elemProperty, "properties[?]");
                String propName = JSONUtils.getString(objProperty, "name");
                IProperty<T> property = (IProperty<T>) MiscUtils.iterativeSearch(state.getProperties(), prop -> prop.getName().equalsIgnoreCase(propName));
                if (property != null) {
                    String propValue = JSONUtils.getString(objProperty, "value");
                    Optional<T> value = property.parseValue(propValue);
                    if (value.isPresent()) {
                        state = state.with(property, value.get());
                    }
                }
            }
        }
        return state;
    }

    public static boolean isMissingStateInformation(@Nonnull JsonObject serialized) {
        return serialized.has("properties");
    }

    public static boolean isMissingStateInformation(@Nonnull String serialized) {
        return serialized.indexOf('[') == -1;
    }

}
