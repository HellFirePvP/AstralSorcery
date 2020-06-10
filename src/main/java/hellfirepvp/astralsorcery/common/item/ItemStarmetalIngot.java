/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.entity.item.EntityStarmetal;
import hellfirepvp.astralsorcery.common.lib.EntityTypesAS;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemStarmetalIngot
 * Created by HellFirePvP
 * Date: 21.07.2019 / 12:24
 */
public class ItemStarmetalIngot extends Item {

    public ItemStarmetalIngot() {
        super(new Properties()
                .group(CommonProxy.ITEM_GROUP_AS));
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Nullable
    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        EntityStarmetal res = new EntityStarmetal(EntityTypesAS.ITEM_STARMETAL_INGOT, world, location.getPosX(), location.getPosY(), location.getPosZ(), itemstack);
        res.setPickupDelay(20);
        res.setMotion(location.getMotion());
        if (location instanceof ItemEntity) {
            res.setThrowerId(((ItemEntity) location).getThrowerId());
            res.setOwnerId(((ItemEntity) location).getOwnerId());
        }
        return res;
    }
}
