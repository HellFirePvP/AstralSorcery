/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.attribute;

import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GemAttributeModifier
 * Created by HellFirePvP
 * Date: 17.11.2018 / 23:23
 */
public class GemAttributeModifier extends PerkAttributeModifier {

    private final UUID uuid;
    private PerkAttributeModifier actualModifier = null;

    public GemAttributeModifier(UUID uniqueId, String type, Mode mode, float value) {
        super(type, mode, value);
        this.uuid = uniqueId;
        this.setAbsolute();
    }

    private boolean resolveModifier() {
        if (actualModifier != null) {
            return true;
        }

        PerkAttributeType type = AttributeTypeRegistry.getType(this.attributeType);
        if (type != null) {
            actualModifier = type.createModifier(this.value, this.mode);
            actualModifier.setAbsolute();
            return true;
        }
        return false;
    }

    @Override
    public float getValue(PlayerProgress progress) {
        if (!resolveModifier()) {
            return super.getValue(progress);
        }
        return actualModifier.getValue(progress);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getValueForDisplay(PlayerProgress progress) {
        if (!resolveModifier()) {
            return super.getValueForDisplay(progress);
        }
        return actualModifier.getValueForDisplay(progress);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getAttributeDisplayFormat() {
        if (!resolveModifier()) {
            return super.getAttributeDisplayFormat();
        }
        return actualModifier.getAttributeDisplayFormat();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getUnlocalizedAttributeName() {
        if (!resolveModifier()) {
            return super.getUnlocalizedAttributeName();
        }
        return actualModifier.getUnlocalizedAttributeName();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasDisplayString() {
        if (!resolveModifier()) {
            return super.hasDisplayString();
        }
        return actualModifier.hasDisplayString();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getLocalizedAttributeValue() {
        if (!resolveModifier()) {
            return super.getLocalizedAttributeValue();
        }
        return actualModifier.getLocalizedAttributeValue();
    }

    @Nullable
    @Override
    @SideOnly(Side.CLIENT)
    public String getLocalizedDisplayString() {
        if (!resolveModifier()) {
            return super.getLocalizedDisplayString();
        }
        return actualModifier.getLocalizedDisplayString();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getLocalizedModifierName() {
        if (!resolveModifier()) {
            return super.getLocalizedModifierName();
        }
        return actualModifier.getLocalizedModifierName();
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public NBTTagCompound serialize() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setUniqueId("id", getUniqueId());
        tag.setString("type", getAttributeType());
        tag.setInteger("mode", getMode().getVanillaAttributeOperation());
        tag.setFloat("baseValue", this.value);
        return tag;
    }

    public static GemAttributeModifier deserialize(NBTTagCompound tag) {
        UUID id = tag.getUniqueId("id");
        String type = tag.getString("type");
        Mode mode = Mode.fromVanillaAttributeOperation(tag.getInteger("mode"));
        float val = tag.getFloat("baseValue");
        return new GemAttributeModifier(id, type, mode, val);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GemAttributeModifier that = (GemAttributeModifier) o;
        return this.uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return this.uuid.hashCode();
    }
}
