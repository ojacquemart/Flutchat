package stllpt.com.flutchat

import android.content.Intent
import android.os.Bundle
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Entry point for the android project.
 */
class FirstActivity : FlutterActivity() {
    private var result: MethodChannel.Result? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 300) {
            data?.extras?.let { it ->
                val callStartDate: Date = it.getSerializable("callStartTime") as Date
                result?.success("${callStartDate.getDuration().first}:" +
                        "${callStartDate.getDuration().second}:" +
                        callStartDate.getDuration().third)
            }
        }
    }

    /**
     * Extension function to get time difference for current time and provided time.
     */
    private fun Date.getDuration(): Triple<String, String, String> {
        val todayDate = Calendar.getInstance()
        todayDate.time = this
        val serverDate = Calendar.getInstance()
        serverDate.time = Date()
        serverDate.set(todayDate.get(Calendar.YEAR),
                todayDate.get(Calendar.MONTH),
                todayDate.get(Calendar.DATE))
        val hours: Long
        val minutes: Long
        val seconds: Long
        return if (serverDate.after(todayDate)) {
            hours = TimeUnit.MILLISECONDS.toHours(serverDate.timeInMillis - todayDate.timeInMillis)
            minutes = TimeUnit.MILLISECONDS.toMinutes(serverDate.timeInMillis - todayDate.timeInMillis)
            seconds = TimeUnit.MILLISECONDS.toSeconds(serverDate.timeInMillis - todayDate.timeInMillis)
            Triple(hours.displayAsTwoDecimal(),
                    (minutes - 60 * hours).displayAsTwoDecimal(),
                    (seconds - 60 * minutes).displayAsTwoDecimal())
        } else {
            Triple("", "", "")
        }
    }

    /**
     * Extension function to display number as 2 digit decimals.
     */
    private fun Long.displayAsTwoDecimal(): String {
        val df = DecimalFormat("00")
        df.maximumFractionDigits = 2
        return df.format(this)
    }

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        GeneratedPluginRegistrant.registerWith(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, "com.stl.flutchat/opentok")
                .setMethodCallHandler { _, result ->
                    this@FirstActivity.result = result
                    startActivityForResult(Intent(this, VideoActivity::class.java), 300)
                }
    }

}

    