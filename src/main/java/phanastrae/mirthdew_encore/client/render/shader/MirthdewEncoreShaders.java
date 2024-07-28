package phanastrae.mirthdew_encore.client.render.shader;

import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.renderer.ShaderInstance;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.MirthdewEncore;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.io.IOException;

public class MirthdewEncoreShaders {
    public static final String dreamtwirlBarrierID = "rendertype_dreamtwirl_barrier";

    @Nullable
    private static ShaderInstance dreamtwirlBarrierShader;

    @Nullable
    public static ShaderInstance getDreamtwirlBarrierShader() {
        return dreamtwirlBarrierShader;
    }

    public static void registerShaders(CoreShaderRegistrationCallback.RegistrationContext registrationCallback) throws IOException {
        registrationCallback.register(MirthdewEncore.id(dreamtwirlBarrierID), DefaultVertexFormat.POSITION, shaderProgram -> MirthdewEncoreShaders.dreamtwirlBarrierShader = shaderProgram);
    }
}
