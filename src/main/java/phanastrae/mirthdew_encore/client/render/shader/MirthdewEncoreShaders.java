package phanastrae.mirthdew_encore.client.render.shader;

import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.VertexFormats;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.MirthdewEncore;

import java.io.IOException;

public class MirthdewEncoreShaders {
    public static final String dreamtwirlBarrierID = "rendertype_dreamtwirl_barrier";

    @Nullable
    private static ShaderProgram dreamtwirlBarrierShader;

    @Nullable
    public static ShaderProgram getDreamtwirlBarrierShader() {
        return dreamtwirlBarrierShader;
    }

    public static void registerShaders(CoreShaderRegistrationCallback.RegistrationContext registrationCallback) throws IOException {
        registrationCallback.register(MirthdewEncore.id(dreamtwirlBarrierID), VertexFormats.POSITION, shaderProgram -> MirthdewEncoreShaders.dreamtwirlBarrierShader = shaderProgram);
    }
}
