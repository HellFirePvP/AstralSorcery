/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.client.screen.tome.TomeResearchScreen;
import hellfirepvp.astralsorcery.client.sound.PlayableSoundInstance;
import hellfirepvp.astralsorcery.common.container.provider.ContainerTomePapersProvider;
import hellfirepvp.astralsorcery.common.item.base.ItemCustom;
import hellfirepvp.astralsorcery.common.item.base.PerkExperienceRevealer;
import hellfirepvp.astralsorcery.common.lib.SoundsAS;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TomeItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TomeItem extends ItemCustom implements PerkExperienceRevealer {

    public TomeItem() {
        super(new Properties()
                .stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (level.isClientSide() && !player.isShiftKeyDown()) {
            openTomeScreen();
        } else if (player instanceof ServerPlayer sPlayer && sPlayer.isShiftKeyDown() && usedHand == InteractionHand.MAIN_HAND) {
            ContainerTomePapersProvider.openTome(player.getInventory().selected).open(sPlayer);
        }
        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }

    @OnlyIn(Dist.CLIENT)
    public static void openTomeScreen() {
        PlayableSoundInstance.of(SoundsAS.SCREEN_TOME_PAGE).forUI().play();
        Minecraft.getInstance().setScreen(TomeResearchScreen.getOpenTome());
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockState state = level.getBlockState(context.getClickedPos());
        if (state.getBlock() instanceof LecternBlock) {
            if (LecternBlock.tryPlaceBook(context.getPlayer(), level, context.getClickedPos(), state, context.getItemInHand())) {
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }
}
