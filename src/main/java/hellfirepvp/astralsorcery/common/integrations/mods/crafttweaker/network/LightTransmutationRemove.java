/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.network;

import hellfirepvp.astralsorcery.common.crafting.helper.CraftingAccessManager;
import hellfirepvp.astralsorcery.common.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LightTransmutationRemove
 * Created by HellFirePvP
 * Date: 27.02.2017 / 12:18
 */
public class LightTransmutationRemove implements SerializeableRecipe {

    private ItemStack matchStack;
    private boolean matchMeta;

    LightTransmutationRemove() {}

    public LightTransmutationRemove(ItemStack matchStack, boolean matchMeta) {
        this.matchStack = matchStack;
        this.matchMeta = matchMeta;
    }

    @Override
    public CraftingType getType() {
        return CraftingType.TRANSMUTE_REMOVE;
    }

    @Override
    public void read(ByteBuf buf) {
        this.matchStack = ByteBufUtils.readItemStack(buf);
        this.matchMeta = buf.readBoolean();
    }

    @Override
    public void write(ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, this.matchStack);
        buf.writeBoolean(this.matchMeta);
    }

    @Override
    public void applyRecipe() {
        CraftingAccessManager.removeMTTransmutation(this.matchStack, this.matchMeta);
    }

}
