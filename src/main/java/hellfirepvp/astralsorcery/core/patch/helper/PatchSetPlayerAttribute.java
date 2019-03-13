/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.core.patch.helper;

import hellfirepvp.astralsorcery.core.ClassPatch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatchSetPlayerAttribute
 * Created by HellFirePvP
 * Date: 19.11.2018 / 17:06
 */
public class PatchSetPlayerAttribute extends ClassPatch {

    public PatchSetPlayerAttribute() {
        super("net.minecraft.entity.EntityLivingBase");
    }

    @Override
    public void patch(ClassNode cn) {
        MethodNode mn = getMethodLazy(cn, "getAttributeMap", "func_110140_aT");
        int instr = peekFirstInstructionAfter(mn, 0, Opcodes.ARETURN);
        while (instr != -1) {
            AbstractInsnNode ain = mn.instructions.get(instr).getPrevious();
            mn.instructions.insert(ain, new MethodInsnNode(Opcodes.INVOKESTATIC,
                    "hellfirepvp/astralsorcery/common/event/AttributeEvent",
                    "markToPlayer",
                    "(Lnet/minecraft/entity/ai/attributes/AbstractAttributeMap;Lnet/minecraft/entity/EntityLivingBase;)Lnet/minecraft/entity/ai/attributes/AbstractAttributeMap;",
                    false));
            mn.instructions.insert(ain, new VarInsnNode(Opcodes.ALOAD, 0));

            instr = peekFirstInstructionAfter(mn, instr + 3, Opcodes.ARETURN);
        }
    }
}
