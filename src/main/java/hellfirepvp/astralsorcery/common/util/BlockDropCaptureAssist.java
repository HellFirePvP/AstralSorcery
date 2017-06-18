/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockDropCaptureAssist
 * Created by HellFirePvP
 * Date: 11.03.2017 / 22:07
 */
public class BlockDropCaptureAssist {

    public static BlockDropCaptureAssist instance = new BlockDropCaptureAssist();

    private static NonNullList<ItemStack> capturedStacks = NonNullList.create();
    private static boolean capturing = false, expectCaptureStone = false;

    private BlockDropCaptureAssist() {}

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onDrop(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityItem && capturing) {
            ItemStack stack = ((EntityItem) event.getEntity()).getItem();
            event.setCanceled(true);
            if(!stack.isEmpty()) {
                if(!expectCaptureStone) {
                    if(stack.getItem() instanceof ItemBlock && ((ItemBlock) stack.getItem()).getBlock().equals(Blocks.STONE)) {
                        event.getEntity().setDead();
                        return;
                    }
                }
                capturedStacks.add(stack);
            }
            event.getEntity().setDead();
        }
    }

    public static void startCapturing(boolean expectStone) {
        if(capturing) {
            throw new IllegalStateException("Tried to start capturing stacks while already capturing itemstacks... ?");
        }
        capturing = true;
        expectCaptureStone = expectStone;
    }

    public static NonNullList<ItemStack> getCapturedStacksAndStop() {
        capturing = false;
        expectCaptureStone = false;
        NonNullList<ItemStack> captured = capturedStacks;
        capturedStacks = NonNullList.create();
        return captured;
    }

}
