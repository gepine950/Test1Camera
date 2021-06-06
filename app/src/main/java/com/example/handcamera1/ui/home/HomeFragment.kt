package com.example.handcamera1.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.nio.ByteBuffer
import java.util.concurrent.Executors
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import com.example.handcamera1.databinding.FragmentHomeBinding
import com.example.handcamera1.ml.SignLanguage
import com.example.handcamera1.util.YuvToRgbConverter
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteOrder
import java.util.concurrent.ExecutorService

@Suppress("DEPRECATION")
class HomeFragment : Fragment() {
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            activity?.let {
                ActivityCompat.requestPermissions(
                        it, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
            }
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(activity,
                        "Permissions not granted by the user.",
                        Toast.LENGTH_SHORT).show()
                activity?.finish()
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = activity?.let { ProcessCameraProvider.getInstance(it) }

        cameraProviderFuture?.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                    }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            // image analyze to model
            val imageAnalysis = ImageAnalysis.Builder()
                    .setTargetResolution(Size(64, 64))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_BLOCK_PRODUCER)
                    .build()

            imageAnalysis.setAnalyzer(cameraExecutor, ImageAnalysis.Analyzer { image ->
                val rotationDegrees = image.imageInfo.rotationDegrees
                // insert your code here.
                Log.d("TEST", rotationDegrees.toString())
                activity?.let { ImageAnalyzerz(it).analyze(image) }
            })

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageAnalysis)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(activity))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        activity?.let { it1 ->
            ContextCompat.checkSelfPermission(
                    it1, it)
        } == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXBasic"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}

private class ImageAnalyzerz(ctx: Context) : ImageAnalysis.Analyzer {
    // Our model expects a RGB image, hence the channel size is 3
    private val channelSize = 3

    // Width of the image that our model expects
    var inputImageWidth = 64

    // Height of the image that our model expects
    var inputImageHeight = 64

    // Size of the input buffer size (if your model expects a float input, multiply this with 4)
    private var modelInputSize = inputImageWidth * inputImageHeight * channelSize

    // Output you get from your model, this is essentially as we saw in netron
    val resultArray = Array(1) { ByteArray(3) }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        // Specify the size of the byteBuffer
        val byteBuffer = ByteBuffer.allocateDirect(modelInputSize)
        byteBuffer.order(ByteOrder.nativeOrder())
        // Calculate the number of pixels in the image
        val pixels = IntArray(inputImageWidth * inputImageHeight)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        var pixel = 0
        // Loop through all the pixels and save them into the buffer
        for (i in 0 until inputImageWidth) {
            for (j in 0 until inputImageHeight) {
                val pixelVal = pixels[pixel++]
                // Do note that the method to add pixels to byteBuffer is different for quantized models over normal tflite models
                byteBuffer.put((pixelVal shr 16 and 0xFF).toByte())
                byteBuffer.put((pixelVal shr 8 and 0xFF).toByte())
                byteBuffer.put((pixelVal and 0xFF).toByte())
            }
        }

        // Recycle the bitmap to save memory
        bitmap.recycle()
        return byteBuffer
    }

    private val signModel: SignLanguage by lazy {
        SignLanguage.newInstance(ctx)
    }

    override fun analyze(image: ImageProxy) {

        val imageBitmap = toBitmap(image)

        val resizedImage =
            imageBitmap?.let { Bitmap.createScaledBitmap(it, inputImageWidth, inputImageHeight, true) }

        val imageBuffer = resizedImage?.let { convertBitmapToByteBuffer(it) }
//
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 64, 64, 3), DataType.FLOAT32)
//        if (imageBuffer != null) {
//            inputFeature0.loadBuffer(imageBuffer)
//        }

        val outputs = signModel.process(inputFeature0)
                .outputFeature0AsTensorBuffer

        Log.d(TAG, outputs.toString())

        image.close()
    }

    /**
     * Convert Image Proxy to Bitmap
     */
    private val yuvToRgbConverter = YuvToRgbConverter(ctx)
    private lateinit var bitmapBuffer: Bitmap
    private lateinit var rotationMatrix: Matrix

    @SuppressLint("UnsafeExperimentalUsageError")
    private fun toBitmap(imageProxy: ImageProxy): Bitmap? {

        val image = imageProxy.image ?: return null

        // Initialise Buffer
        if (!::bitmapBuffer.isInitialized) {
            // The image rotation and RGB image buffer are initialized only once
            Log.d(TAG, "Initalise toBitmap()")
            rotationMatrix = Matrix()
            rotationMatrix.postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
            bitmapBuffer = Bitmap.createBitmap(
                imageProxy.width, imageProxy.height, Bitmap.Config.ARGB_8888
            )
        }

        // Pass image to an image analyser
        yuvToRgbConverter.yuvToRgb(image, bitmapBuffer)

        // Create the Bitmap in the correct orientation
        return Bitmap.createBitmap(
            bitmapBuffer,
            0,
            0,
            bitmapBuffer.width,
            bitmapBuffer.height,
            rotationMatrix,
            false
        )
    }
}
