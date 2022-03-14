package com.nativeappreact;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.ImageProxy;
import com.mrousavy.camera.frameprocessor.FrameProcessorPlugin;

public class MoveGeneratorPlugin extends FrameProcessorPlugin{

    public MoveGeneratorPlugin() {
        super("GenerateMove");
    }

    @Nullable
    @Override
    public Object callback(@NonNull androidx.camera.core.ImageProxy image, @NonNull Object[] params) {
        return "This is from the Move generator Plugnah I am kiddingin";
    }
}
