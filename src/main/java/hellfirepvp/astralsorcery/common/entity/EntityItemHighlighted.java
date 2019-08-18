/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity;

import hellfirepvp.astralsorcery.common.lib.EntityTypesAS;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityItemHighlighted
 * Created by HellFirePvP
 * Date: 18.08.2019 / 10:25
 */
public class EntityItemHighlighted extends ItemEntity {

    private static final DataParameter<Integer> DATA_COLOR = EntityDataManager.createKey(EntityItemHighlighted.class, DataSerializers.VARINT);
    private static final int NO_COLOR = 0xFF000000;

    public EntityItemHighlighted(World world) {
        super(EntityTypesAS.ITEM_HIGHLIGHT, world);
    }

    public EntityItemHighlighted(EntityType<? extends ItemEntity> type, World world) {
        super(type, world);
    }

    public EntityItemHighlighted(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public EntityItemHighlighted(World worldIn, double x, double y, double z, ItemStack stack) {
        super(worldIn, x, y, z, stack);
    }

    public static EntityType.IFactory<EntityItemHighlighted> factoryHighlighted() {
        return (spawnEntity, world) -> new EntityItemHighlighted(world);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.getDataManager().register(DATA_COLOR, NO_COLOR);
    }

    public void applyColor(@Nullable Color color) {
        this.getDataManager().set(DATA_COLOR, color == null ? NO_COLOR : (color.getRGB() & 0x00FFFFFF));
    }

    public boolean hasColor() {
        return this.getDataManager().get(DATA_COLOR) == NO_COLOR;
    }

    @Nullable
    public Color getHighlightColor() {
        if (!hasColor()) {
            return null;
        }
        int colorInt = this.getDataManager().get(DATA_COLOR);
        return new Color(colorInt, false);
    }
}
