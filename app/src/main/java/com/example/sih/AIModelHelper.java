package com.example.sih;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Pair;

import androidx.room.jarjarred.org.stringtemplate.v4.Interpreter;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import java.io.IOException;
import java.nio.MappedByteBuffer;

public class AIModelHelper {

    private Interpreter interpreter;
    private static final int IMG_WIDTH = 224;
    private static final int IMG_HEIGHT = 224;
    private static final String[] CLASS_NAMES = {"Gir", "Sahiwal", "Murrah"};
    private static final int NUM_CLASSES = 3;

    public AIModelHelper(Context context) {
        try {
            MappedByteBuffer tfliteModel = FileUtil.loadMappedFile(context, "model.tflite");
            interpreter = new Interpreter(tfliteModel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Pair<String, Float> predict(Bitmap bitmap) {
        float[][][][] input = preprocessBitmap(bitmap);
        float[][] output = new float[1][NUM_CLASSES];
        interpreter.run(input, output);

        int maxIndex = 0;
        float maxVal = output[0][0];
        for (int i = 1; i < NUM_CLASSES; i++) {
            if (output[0][i] > maxVal) {
                maxVal = output[0][i];
                maxIndex = i;
            }
        }
        return new Pair<>(CLASS_NAMES[maxIndex], maxVal);
    }

    private float[][][][] preprocessBitmap(Bitmap bitmap) {
        Bitmap resized = Bitmap.createScaledBitmap(bitmap, IMG_WIDTH, IMG_HEIGHT, true);
        float[][][][] input = new float[1][IMG_WIDTH][IMG_HEIGHT][3];
        for (int x = 0; x < IMG_WIDTH; x++) {
            for (int y = 0; y < IMG_HEIGHT; y++) {
                int px = resized.getPixel(x, y);
                input[0][x][y][0] = Color.red(px) / 255f;
                input[0][x][y][1] = Color.green(px) / 255f;
                input[0][x][y][2] = Color.blue(px) / 255f;
            }
        }
        return input;
    }
}
