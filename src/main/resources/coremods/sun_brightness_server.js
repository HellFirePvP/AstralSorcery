function initializeCoreMod() {
    return {
        'set_player_field': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.world.World',
                'methodName': 'func_72966_v',
                'methodDesc': '()V'
            },
            'transformer': function(method) {
                print('[AstralSorcery] Adding \'sun_brightness_server\' ASM patch...');

                var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
                var putSkyLight = ASMAPI.findFirstInstructionAfter(method, Opcodes.PUTFIELD, 0);

                while (putSkyLight !== null) {
                    method.instructions.insertBefore(putSkyLight, new VarInsnNode(Opcodes.ALOAD, 0));
                    method.instructions.insertBefore(putSkyLight, ASMAPI.buildMethodCall(
                        'hellfirepvp/astralsorcery/common/util/ASMHookEndpoint',
                        'overrideSunBrightnessServer',
                        "(ILnet/minecraft/world/World;)I",
                        ASMAPI.MethodType.STATIC));

                    putSkyLight = ASMAPI.findFirstInstructionAfter(method, Opcodes.PUTFIELD, method.instructions.indexOf(putSkyLight) + 2);
                }

                print('[AstralSorcery] Added \'sun_brightness_server\' ASM patch!');
                return method;
            }
        }
    }
}