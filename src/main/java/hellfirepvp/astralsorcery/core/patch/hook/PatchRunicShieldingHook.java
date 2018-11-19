/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.core.patch.hook;

import hellfirepvp.astralsorcery.core.ClassPatch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatchRunicShieldingHook
 * Created by HellFirePvP
 * Date: 18.11.2018 / 21:15
 */
public class PatchRunicShieldingHook extends ClassPatch {

    public PatchRunicShieldingHook() {
        super("thaumcraft.common.lib.events.PlayerEvents");
    }

    @Override
    public void patch(ClassNode cn) {
        MethodNode mn = getMethodLazy(cn, "handleRunicArmor", "handleRunicArmor");
        AbstractInsnNode getStaticMaxCharge = findFirstInstructionAfter(mn, 0,
                (ain) -> ain.getOpcode() == Opcodes.GETSTATIC &&
                ain instanceof FieldInsnNode &&
                ((FieldInsnNode) ain).name.equals("lastMaxCharge")).getPrevious();

        mn.instructions.insert(getStaticMaxCharge, new VarInsnNode(Opcodes.ISTORE, 1));
        mn.instructions.insert(getStaticMaxCharge, new MethodInsnNode(Opcodes.INVOKESTATIC,
                "hellfirepvp/astralsorcery/common/event/RunicShieldingCalculateEvent",
                "fire",
                "(Lnet/minecraft/entity/player/EntityPlayer;I)I",
                false));
        mn.instructions.insert(getStaticMaxCharge, new VarInsnNode(Opcodes.ILOAD, 1));
        mn.instructions.insert(getStaticMaxCharge, new VarInsnNode(Opcodes.ALOAD, 0));
    }

}
