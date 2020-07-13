/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import java.util.Stack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockDropCaptureAssist
 * Created by HellFirePvP
 * Date: 11.03.2017 / 22:07
 */
public class BlockDropCaptureAssist {

    public static BlockDropCaptureAssist INSTANCE = new BlockDropCaptureAssist();

    private static Stack<NonNullList<ItemStack>> capturing = new Stack<>();

    private BlockDropCaptureAssist() {}

    public void onDrop(EntityJoinWorldEvent event) {
        if (event.getWorld() instanceof ServerWorld && event.getEntity() instanceof ItemEntity) {
            ItemStack itemStack = ((ItemEntity) event.getEntity()).getItem();
            if (!capturing.isEmpty()) {
                event.setCanceled(true);
                if (!itemStack.isEmpty()) {
                    //Apparently concurrency sometimes gets us here...
                    if (!capturing.isEmpty()) {
                        capturing.peek().add(itemStack);
                    }
                }
                event.getEntity().remove();
            }
        }
    }

    public static void startCapturing() {
        capturing.push(NonNullList.create());
    }

    public static NonNullList<ItemStack> getCapturedStacksAndStop() {
        return capturing.pop();
    }

}
