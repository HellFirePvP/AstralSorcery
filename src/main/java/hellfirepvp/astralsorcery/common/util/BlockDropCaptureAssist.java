package hellfirepvp.astralsorcery.common.util;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
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
 * Date: 11.03.2017 / 19:46
 */
public class BlockDropCaptureAssist {

    public static BlockDropCaptureAssist instance = new BlockDropCaptureAssist();

    private static List<ItemStack> capturedStacks = new LinkedList<>();
    private static boolean capturing = false, expectCaptureStone = false;

    private BlockDropCaptureAssist() {}

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onDrop(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityItem && capturing) {
            ItemStack stack = ((EntityItem) event.getEntity()).getEntityItem();
            event.setCanceled(true);
            if(!expectCaptureStone) {
                if(stack.getItem() instanceof ItemBlock && ((ItemBlock) stack.getItem()).getBlock().equals(Blocks.STONE)) {
                    event.getEntity().setDead();
                    return;
                }
            }
            capturedStacks.add(stack);
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

    public static List<ItemStack> getCapturedStacksAndStop() {
        capturing = false;
        expectCaptureStone = false;
        List<ItemStack> captured = capturedStacks;
        capturedStacks = new LinkedList<>();
        return captured;
    }

}
