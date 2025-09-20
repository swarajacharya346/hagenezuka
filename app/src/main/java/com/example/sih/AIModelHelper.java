package com.example.sih;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

public class AIModelHelper {

    private Interpreter interpreter;
    private List<String> labels;

    public AIModelHelper(Context context, String modelPath, List<String> labels) throws IOException {
        this.interpreter = new Interpreter(loadModelFile(context, modelPath));
        this.labels = labels;
    }

    private MappedByteBuffer loadModelFile(Context context, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public Prediction predict(Bitmap bitmap) {
        // Resize or preprocess bitmap as needed for your model
        int inputSize = 224; // example
        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, false);

        float[][] output = new float[1][labels.size()]; // model output array
        float[] input = bitmapToFloatArray(scaled);

        interpreter.run(input, output);

        // Find the label with max confidence
        int maxIdx = 0;
        for (int i = 1; i < output[0].length; i++) {
            if (output[0][i] > output[0][maxIdx]) maxIdx = i;
        }

        return new Prediction(labels.get(maxIdx), output[0][maxIdx]);
    }

    private float[] bitmapToFloatArray(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float[] floatValues = new float[width * height * 3];
        int[] intValues = new int[width * height];
        bitmap.getPixels(intValues, 0, width, 0, 0, width, height);

        for (int i = 0; i < intValues.length; i++) {
            int val = intValues[i];
            floatValues[i * 3] = ((val >> 16) & 0xFF) / 255.f;
            floatValues[i * 3 + 1] = ((val >> 8) & 0xFF) / 255.f;
            floatValues[i * 3 + 2] = (val & 0xFF) / 255.f;
        }
        return floatValues;
    }

    public static class Prediction {
        public final String label;
        public final float confidence;

        public Prediction(String label, float confidence) {
            this.label = label;
            this.confidence = confidence;
        }
    }
}
