/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity;

import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ItemEntityHighlighted
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class ItemEntityHighlighted extends ItemEntityReplacement {

    private static final EntityDataAccessor<Integer> DATA_COLOR = SynchedEntityData.defineId(ItemEntityHighlighted.class, EntityDataSerializers.INT);
    private static final int NO_COLOR = 0xFF000000;

    public ItemEntityHighlighted(EntityType<? extends ItemEntityReplacement> entityType, Level level) {
        super(entityType, level);
    }

    public ItemEntityHighlighted(Level level, double posX, double posY, double posZ, ItemStack itemStack) {
        super(level, posX, posY, posZ, itemStack);
    }

    public ItemEntityHighlighted(Level level, double posX, double posY, double posZ, ItemStack itemStack, double deltaX, double deltaY, double deltaZ) {
        super(level, posX, posY, posZ, itemStack, deltaX, deltaY, deltaZ);
    }

    public static EntityType.EntityFactory<ItemEntityHighlighted> factoryHighlighted() {
        return (type, level) -> new ItemEntityHighlighted(type, level) {};
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_COLOR, NO_COLOR);
    }

    public <T extends ItemEntityHighlighted> T setNoColor() {
        return this.setColor(null);
    }

    public <T extends ItemEntityHighlighted> T setColor(@Nullable ColorWrapper color) {
        this.getEntityData().set(DATA_COLOR, color == null ? NO_COLOR : (color.getColor() & 0x00FFFFFF));
        return MiscUtil.cast(this);
    }

    public boolean hasColor() {
        return this.getColor().map(color -> color.getColor() != NO_COLOR).orElse(false);
    }

    public Optional<ColorWrapper> getColor() {
        int color = this.getEntityData().get(DATA_COLOR);
        if (color == NO_COLOR) {
            return Optional.empty();
        }
        return Optional.of(ColorWrapper.opaque(color));
    }
}
