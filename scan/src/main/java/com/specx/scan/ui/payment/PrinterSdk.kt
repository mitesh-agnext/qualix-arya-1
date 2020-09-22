package com.specx.scan.ui.payment;

import android.bluetooth.BluetoothSocket
import android.graphics.Bitmap
import android.text.TextUtils
import com.printer.app.PrintUtils
import com.printer.app.util.Constants
import com.specx.scan.data.model.analysis.AnalysisItem
import com.specx.scan.data.model.commodity.CommodityItem
import com.specx.scan.data.model.sample.SampleItem
import timber.log.Timber
import java.io.IOException
import java.io.OutputStream
import java.io.UnsupportedEncodingException
import java.util.*

class PrinterSdk {

    private var btsocket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null

    fun printBill(btsocket: BluetoothSocket, commodity: CommodityItem,
                  sample: SampleItem, analyses: List<AnalysisItem>) {
        this.btsocket = btsocket
        var opstream: OutputStream? = null
        try {
            opstream = btsocket.outputStream
        } catch (e: IOException) {
            Timber.e(e)
        }
        outputStream = opstream
        //print command
        try {
            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                Timber.e(e)
            }
            outputStream = btsocket.outputStream
            val printformat = byteArrayOf(0x1B, 0x21, 0x03)
            outputStream!!.write(printformat)
            printCustom("AgNext Technologies", 0, 1)
            printCustom("Qualix Labs", 2, 1)
            printNewLine()
            printCustom("Commodity: ${commodity.name}", 0, 1)
            if(!TextUtils.isEmpty(sample.lotId)){
                printCustom("Lot ID: ${sample.lotId}", 0, 1)
            }
            printCustom("Sample ID: ${sample.id}", 0, 1)
            val dateTime = getDateTime()
            printCustom("Date: ${dateTime[0]} Time: ${dateTime[1]}", 0, 1)
            printNewLine()
            printCustom("Amount", 0, 1)
            printCustom(String.format("Rs %s", commodity.totalAmount), 2, 1)
            printNewLine()
            printCustom(String.format("%-15s %10s", "Analysis", "Result"), 0, 1)
            printCustom("---------------------------", 0, 1)
            for (analysis: AnalysisItem in analyses) {
                printCustom(String.format("%-15s %10s", analysis.name,
                        analysis.totalAmount + analysis.amountUnit), 0, 1)
            }
            printNewLine()
            printCustom(".. Thank You ..", 0, 1)
            printNewLine()
            printNewLine()
            outputStream!!.flush()
        } catch (e: IOException) {
            Timber.e(e)
        }
    }

    //print custom
    private fun printCustom(msg: String, size: Int, align: Int) {
        //Print config "mode"
        val cc = byteArrayOf(0x1B, 0x21, 0x03) // 0- normal size text
        //byte[] cc1 = new byte[]{0x1B,0x21,0x00};  // 0- normal size text
        val bb = byteArrayOf(0x1B, 0x21, 0x08) // 1- only bold text
        val bb2 = byteArrayOf(0x1B, 0x21, 0x20) // 2- bold with medium text
        val bb3 = byteArrayOf(0x1B, 0x21, 0x10) // 3- bold with large text
        try {
            when (size) {
                0 -> outputStream!!.write(cc)
                1 -> outputStream!!.write(bb)
                2 -> outputStream!!.write(bb2)
                3 -> outputStream!!.write(bb3)
            }
            when (align) {
                0 ->  //left align
                    outputStream!!.write(Constants.ESC_ALIGN_LEFT)
                1 ->  //center align
                    outputStream!!.write(Constants.ESC_ALIGN_CENTER)
                2 ->  //right align
                    outputStream!!.write(Constants.ESC_ALIGN_RIGHT)
            }
            outputStream!!.write(msg.toByteArray())
            outputStream!!.write(0x0A)
            // outputStream!!.write(Constants.LF!!)
            //outputStream.write(cc);
            //printNewLine();
        } catch (e: IOException) {
            Timber.e(e)
        }
    }

    //print photo
    fun printPhoto(bmp: Bitmap) {
        try {
            val command: ByteArray = PrintUtils.decodeBitmap(bmp)
            outputStream!!.write(Constants.ESC_ALIGN_CENTER)
            printText(command)
        } catch (e: Exception) {
            Timber.e(e, "PrintTools: the file isn't exists")
        }
    }

    //print unicode
    fun printUnicode() {
        try {
            outputStream!!.write(Constants.ESC_ALIGN_CENTER)
            printText(PrintUtils.UNICODE_TEXT)
        } catch (e: UnsupportedEncodingException) {
            Timber.e(e)
        } catch (e: IOException) {
            Timber.e(e)
        }
    }

    //print new line
    private fun printNewLine() {
        try {
            outputStream!!.write(Constants.FEED_LINE)
        } catch (e: IOException) {
            Timber.e(e)
        }
    }

    //print text
    private fun printText(msg: String) {
        try { // Print normal text
            outputStream!!.write(msg.toByteArray())
        } catch (e: IOException) {
            Timber.e(e)
        }
    }

    //print byte[]
    private fun printText(msg: ByteArray) {
        try { // Print normal text
            outputStream!!.write(msg)
            printNewLine()
        } catch (e: IOException) {
            Timber.e(e)
        }
    }

    private fun getDateTime(): Array<String?> {
        val c = Calendar.getInstance()
        val dateTime = arrayOfNulls<String>(2)
        val day = c[Calendar.DAY_OF_MONTH]
        val month = c[Calendar.MONTH] + 1
        val year = c[Calendar.YEAR]
        val hour = c[Calendar.HOUR_OF_DAY]
        val minute = c[Calendar.MINUTE]
        dateTime[0] = String.format("%02d/%02d/%d", day, month, year)
        dateTime[1] = String.format("%2d:%2d", hour, minute)
        return dateTime
    }

    fun cleanUp() {
        try {
            if (btsocket != null) {
                outputStream!!.close()
                btsocket!!.close()
                btsocket = null
            }
        } catch (e: IOException) {
            Timber.e(e)
        }
    }
}