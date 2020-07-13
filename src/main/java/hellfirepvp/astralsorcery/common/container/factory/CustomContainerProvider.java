/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.container.factory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CustomContainerProvider
 * Created by HellFirePvP
 * Date: 10.08.2019 / 09:11
 */
public abstract class CustomContainerProvider<C extends Container> implements INamedContainerProvider {

    private final ContainerType<C> type;

    public CustomContainerProvider(ContainerType<C> type) {
        this.type = type;
    }

    @Override
    public ITextComponent getDisplayName() {
        ResourceLocation key = this.type.getRegistryName();
        return new TranslationTextComponent("screen.%s.%s", key.getNamespace(), key.getPath());
    }

    @Nonnull
    @Override
    public abstract C createMenu(int id, PlayerInventory plInventory, PlayerEntity player);

    protected abstract void writeExtraData(PacketBuffer buf);

    public void openFor(ServerPlayerEntity player) {
        NetworkHooks.openGui(player, this, this::writeExtraData);
    }
}
