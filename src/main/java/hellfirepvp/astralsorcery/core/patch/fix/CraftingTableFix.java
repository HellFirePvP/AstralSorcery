package hellfirepvp.astralsorcery.core.patch.fix;

import hellfirepvp.astralsorcery.core.ClassPatch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CraftingTableFix
 * Created by HellFirePvP
 * Date: 11.08.2016 / 09:59
 */
public class CraftingTableFix extends ClassPatch {

    public CraftingTableFix() {
        super("net.minecraft.block.BlockWorkbench");
    }

    @Override
    public void patch(ClassNode cn) {
        MethodNode mn = getMethod(cn, "onBlockActivated", "func_180639_a", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/EnumHand;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/EnumFacing;FFF)Z");
        AbstractInsnNode newNode = findFirstInstruction(mn, Opcodes.NEW).getPrevious();
        int index = mn.instructions.indexOf(newNode);

        for (int i = 0; i < 7; i++) {
            mn.instructions.remove(mn.instructions.get(index));
        }
        AbstractInsnNode insertPrev = mn.instructions.get(index);
        mn.instructions.insertBefore(insertPrev, new VarInsnNode(Opcodes.ALOAD, 4));
        mn.instructions.insertBefore(insertPrev, new VarInsnNode(Opcodes.ALOAD, 2));
        mn.instructions.insertBefore(insertPrev, new MethodInsnNode(Opcodes.INVOKESTATIC, "hellfirepvp/astralsorcery/common/network/packet/server/PktCraftingTableFix", "sendOpenCraftingTable", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/math/BlockPos;)V", false));
    }

}
