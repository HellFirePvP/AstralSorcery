/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.core.patch.helper;

import hellfirepvp.astralsorcery.core.ASMTransformationException;
import hellfirepvp.astralsorcery.core.ClassPatch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatchSetBlock
 * Created by HellFirePvP
 * Date: 04.08.2016 / 00:39
 */
public class PatchBlockModify extends ClassPatch {

    public PatchBlockModify() {
        super("net.minecraft.world.chunk.Chunk");
    }

    @Override
    public void patch(ClassNode cn) {
        MethodNode mn = getMethod(cn, "setBlockState", "func_177436_a", "(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/block/state/IBlockState;");
        for (int i = 0; i < mn.instructions.size(); i++) {
            AbstractInsnNode aNode = mn.instructions.get(i);
            if (aNode.getOpcode() == Opcodes.ARETURN) {
                AbstractInsnNode prev = aNode.getPrevious();
                if(prev instanceof VarInsnNode && prev.getOpcode() == Opcodes.ALOAD && ((VarInsnNode) prev).var == 8) {
                    mn.instructions.insertBefore(prev, new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lnet/minecraftforge/fml/common/eventhandler/EventBus;"));
                    mn.instructions.insertBefore(prev, new TypeInsnNode(Opcodes.NEW, "hellfirepvp/astralsorcery/common/event/BlockModifyEvent"));
                    mn.instructions.insertBefore(prev, new InsnNode(Opcodes.DUP));
                    mn.instructions.insertBefore(prev, new VarInsnNode(Opcodes.ALOAD, 0)); //Chunk
                    mn.instructions.insertBefore(prev, new VarInsnNode(Opcodes.ALOAD, 1)); //Pos
                    mn.instructions.insertBefore(prev, new VarInsnNode(Opcodes.ALOAD, 8)); //OldState
                    mn.instructions.insertBefore(prev, new VarInsnNode(Opcodes.ALOAD, 2)); //NewState
                    mn.instructions.insertBefore(prev, new MethodInsnNode(Opcodes.INVOKESPECIAL, "hellfirepvp/astralsorcery/common/event/BlockModifyEvent", "<init>", "(Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/block/state/IBlockState;)V", false));
                    mn.instructions.insertBefore(prev, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/fml/common/eventhandler/EventBus", "post", "(Lnet/minecraftforge/fml/common/eventhandler/Event;)Z", false));
                    mn.instructions.insertBefore(prev, new InsnNode(Opcodes.POP));
                    return;
                }
            }
        }
        throw new ASMTransformationException("Could not find the expected return statement in the setBlockState method!");
    }

}
