package com.example.sih;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class AIModelHelper {

    private Interpreter interpreter;
    private List<String> labels;
    private int inputSize; // model input size, e.g., 224

    public AIModelHelper(Context context, String modelPath, String labelsPath, int inputSize) throws IOException {
        this.interpreter = new Interpreter(loadModelFile(context, modelPath));
        this.labels = loadLabels(context, labelsPath);
        this.inputSize = inputSize;
    }

    private MappedByteBuffer loadModelFile(Context context, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private List<String> loadLabels(Context context, String labelsPath) throws IOException {
        List<String> labelList = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(labelsPath)));
        String line;
        while ((line = reader.readLine()) != null) {
            labelList.add(line.trim());
        }
        reader.close();
        return labelList;
    }

    public Prediction predict(Bitmap bitmap) {
        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, false);

        // TensorFlow Lite expects a 4D input: [1, height, width, 3]
        float[][][][] input = new float[1][inputSize][inputSize][3];
        int[] intValues = new int[inputSize * inputSize];
        scaled.getPixels(intValues, 0, inputSize, 0, 0, inputSize, inputSize);

        for (int y = 0; y < inputSize; y++) {
            for (int x = 0; x < inputSize; x++) {
                int pixel = intValues[y * inputSize + x];
                input[0][y][x][0] = ((pixel >> 16) & 0xFF) / 255.f;
                input[0][y][x][1] = ((pixel >> 8) & 0xFF) / 255.f;
                input[0][y][x][2] = (pixel & 0xFF) / 255.f;
            }
        }

        float[][] output = new float[1][labels.size()];
        interpreter.run(input, output);

        // Find max confidence
        int maxIdx = 0;
        for (int i = 1; i < output[0].length; i++) {
            if (output[0][i] > output[0][maxIdx]) maxIdx = i;
        }

        return new Prediction(labels.get(maxIdx), output[0][maxIdx]);
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
