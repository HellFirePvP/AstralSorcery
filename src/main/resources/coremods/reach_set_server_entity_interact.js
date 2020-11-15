function initializeCoreMod() {
    return {
        'reach_set_server_entity_interact': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.network.play.ServerPlayNetHandler',
                'methodName': 'func_147340_a',
                'methodDesc': '(Lnet/minecraft/network/play/client/CUseEntityPacket;)V'
            },
            'transformer': function(method) {
                var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');

                ASMAPI.log('INFO', 'Adding \'reach_set_server_entity_interact\' ASM patch...');

                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var LdcInsnNode = Java.type('org.objectweb.asm.tree.LdcInsnNode');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
                var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');

                var loadSqReach = ASMAPI.findFirstInstruction(method, Opcodes.LDC);
                while (loadSqReach !== null) {
                    if (typeof loadSqReach.cst.doubleValue === 'function') {
                        if (loadSqReach.cst.doubleValue() == 36.0) {
                            method.instructions.insertBefore(loadSqReach, new VarInsnNode(Opcodes.ALOAD, 0));
                            method.instructions.insert(loadSqReach, ASMAPI.buildMethodCall(
                                                'hellfirepvp/astralsorcery/common/util/ASMHookEndpoint',
                                                'getOverriddenSeenEntityReachMaximum',
                                                '(Lnet/minecraft/network/play/ServerPlayNetHandler;D)D',
                                                ASMAPI.MethodType.STATIC));
                        }
                        loadSqReach = ASMAPI.findFirstInstructionAfter(method, Opcodes.LDC, method.instructions.indexOf(loadSqReach) + 3);
                    } else {
                        loadSqReach = ASMAPI.findFirstInstructionAfter(method, Opcodes.LDC, method.instructions.indexOf(loadSqReach) + 1);
                    }
                }

                ASMAPI.log('INFO', 'Added \'reach_set_server_entity_interact\' ASM patch!');
                return method;
            }
        }
    }
}