/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity.item;

import hellfirepvp.astralsorcery.common.lib.EntityTypesAS;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

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

    public EntityItemHighlighted(EntityType<? extends ItemEntity> type, World world) {
        super(type, world);
    }

    public EntityItemHighlighted(EntityType<? extends ItemEntity> type, World world, double x, double y, double z) {
        this(type, world);
        this.setPosition(x, y, z);
        this.rotationYaw = this.rand.nextFloat() * 360.0F;
        this.setMotion(this.rand.nextDouble() * 0.2D - 0.1D, 0.2D, this.rand.nextDouble() * 0.2D - 0.1D);
    }

    public EntityItemHighlighted(EntityType<? extends ItemEntity> type, World world, double x, double y, double z, ItemStack stack) {
        this(type, world, x, y, z);
        this.setItem(stack);
        this.lifespan = stack.isEmpty() ? 6000 : stack.getEntityLifespan(world);
    }

    public static EntityType.IFactory<EntityItemHighlighted> factoryHighlighted() {
        return (spawnEntity, world) -> new EntityItemHighlighted(EntityTypesAS.ITEM_HIGHLIGHT, world);
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
        return this.getDataManager().get(DATA_COLOR) != NO_COLOR;
    }

    @Nullable
    public Color getHighlightColor() {
        if (!hasColor()) {
            return null;
        }
        int colorInt = this.getDataManager().get(DATA_COLOR);
        return new Color(colorInt, false);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
