public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 1001;
    private ImageView imageView;
    private AIModelHelper aiModelHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.capturedImage);
        Button captureBtn = findViewById(R.id.captureBtn);
        aiModelHelper = new AIModelHelper(this);

        captureBtn.setOnClickListener(v -> {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);

            // Run AI prediction
            Pair<String, Float> result = aiModelHelper.predict(photo);
            Intent intent = new Intent(this, ResultActivity.class);
            intent.putExtra("breed", result.first);
            intent.putExtra("confidence", result.second);
            startActivity(intent);
        }
    }
}
