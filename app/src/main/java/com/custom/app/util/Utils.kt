package com.custom.app.util

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Spinner
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import com.custom.app.R
import com.custom.app.ui.login.LoginActivity
import com.google.zxing.WriterException
import java.text.SimpleDateFormat
import java.util.*

class Utils {
    companion object {
        fun fetchPrimaryColor(activity: Activity): Int {
            val typedValue = TypedValue()
            val a = activity.obtainStyledAttributes(typedValue.data, intArrayOf(R.attr.colorPrimary))
            val color = a.getColor(0, 0)

            a.recycle()
            return color
        }

        fun setSpinner(activity: Activity, options: ArrayList<String>): ArrayAdapter<String> {
            val adapter = ArrayAdapter(activity, android.R.layout.simple_spinner_item, options)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            return adapter
        }

        fun timeStampDate(timestamp: Long): String {
            val sdf = SimpleDateFormat("dd-MMM-yyyy HH:mm")
            val date = sdf.format(Date(timestamp))
            return date
        }

        fun getTimeFromEpoch(timestamp: Long): String {
            val itemLong = (timestamp / 1000)
            val d = Date(itemLong * 1000L)
            return SimpleDateFormat("dd-MMM HH:mm").format(d)
        }

        fun getDaysAgo(daysAgo: Int): Date {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)

            return calendar.time
        }

        fun setSpinnerAdapter(context: Context, items: ArrayList<String>, spinner: Spinner) {
            val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, items)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        fun tokenExpire(context: Activity) {
            val i = Intent(context, LoginActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(i)
        }

        fun startActivityWithLoad(source: Activity, destination: Class<*>, bundle: Bundle, isFinish: Boolean) {
            val intent = Intent(source, destination)
            intent.putExtras(bundle)
            intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            source.startActivity(intent)
            if (isFinish!!) {
                source.finish()
            }
        }
        fun imageDialog(context: Context, imageBitmap: Bitmap) {


            var builder = Dialog(context);
            builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
            builder.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            builder.setOnDismissListener {
            }


            var imageView = ImageView(context);
            imageView.setImageBitmap(imageBitmap)
            builder.addContentView(
                    imageView, RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            )
            );
            builder.show();

        }

        /**Generate the QR and display*/
        fun generateQR(input: String, context: Context): Bitmap {
            var bitmap: Bitmap? = null

            val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = manager.defaultDisplay
            val point = Point()
            display.getSize(point)
            val width = point.x
            val height = point.y
            var smallerDimension = if (width < height) width else height
            smallerDimension = smallerDimension * 3 / 4
            val qrgEncoder = QRGEncoder(input, null, QRGContents.Type.TEXT, smallerDimension)

            try {
                bitmap = qrgEncoder.encodeAsBitmap()
                // ivQR.setImageBitmap(bitmap)
            } catch (e: WriterException) {
                Log.v("Error", e.toString())
            }

            return bitmap!!

        }

    }
}