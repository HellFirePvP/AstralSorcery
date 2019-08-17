function initializeCoreMod() {
    return {
        'post_process_vanilla': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.entity.ai.attributes.ModifiableAttributeInstance',
                'methodName': 'func_111129_g',
                'methodDesc': '()D'
            },
            'transformer': function(method) {
                print('[AstralSorcery] Adding \'post_process_vanilla\' ASM patch...');

                var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
                var aReturn = ASMAPI.findFirstInstructionAfter(method, Opcodes.DRETURN, 0);

                while (aReturn !== null) {
                    var prev = aReturn.getPrevious();

                    method.instructions.insert(prev, ASMAPI.buildMethodCall(
                        'hellfirepvp/astralsorcery/common/util/ASMHookEndpoint',
                        'postProcessVanilla',
                        '(DLnet/minecraft/entity/ai/attributes/ModifiableAttributeInstance;)D',
                        ASMAPI.MethodType.STATIC));
                    method.instructions.insert(prev, new VarInsnNode(Opcodes.ALOAD, 0));

                    aReturn = ASMAPI.findFirstInstructionAfter(method, Opcodes.DRETURN, method.instructions.indexOf(aReturn) + 3);
                }

                print('[AstralSorcery] Added \'post_process_vanilla\' ASM patch!');
                return method;
            }
        }
    }
}