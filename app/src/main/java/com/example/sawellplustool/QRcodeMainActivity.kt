package com.example.sawellplustool

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import android.os.Handler
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.example.getclubapiexample.MyViewModel
import kotlinx.android.synthetic.main.fragment_list.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.fixedRateTimer


class QRCodeMainActivity : AppCompatActivity() {
    private lateinit var svBarcode: SurfaceView

    private lateinit var detector: BarcodeDetector
    private lateinit var cameraSource: CameraSource
    lateinit var timer: Timer
    var qrcodeScanState = false

    companion object {
        lateinit var myViewModel: MyViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //隱藏標題欄
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        //隱藏狀態欄
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        myViewModel.startQrCodeTimeOutDetect()                 //Timeout 偵測


        myViewModel.timeoutLiveData.observe(this, androidx.lifecycle.Observer {
            if (mainFragment.myViewModel.timeoutEnabled) {
                mainFragment.myViewModel.timeoutEnabled = false
                if (!qrcodeScanState) {
                    Log.d(TAG, "Timout : ")
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Warning ")
                    builder.setMessage("Timeout, when scanning QR code ")
                    builder.setPositiveButton("Ok") { dialog, which ->
                        finish()
                    }
                    val alert = builder.create()
                    alert.show()

                }
            }
        })



        setContentView(R.layout.activity_q_rcode_main)

        textViewBackKey.setOnClickListener {
            finish()                                //結束Activity
        }

//得到傳遞來的資料
        var bundle = intent.getBundleExtra("bun")
        val data = bundle?.get("data")
        //      Log.d(TAG, "data:$data ")

//  測試馬上傳回去
//        MainFragment.getrightqrcodemacvalue01 = "00:44:55:77:99:88"
        //       Log.d(TAG, "onCreate:${myViewModel}")
        //       MainFragment.myViewModel.getrightqrcodemacvalue ="00:11:22:33:44:55"


        /*       var intent2 = Intent(this, MainActivity::class.java)
               var str = "I am yct2"
       //用資料捆傳遞資料
               val bundle1 = Bundle()
               bundle1.putString("data2", str)
       //把資料捆設定改意圖
               intent2.putExtra("bun", bundle1)  //傳遞的資料
       //啟用意圖
               //      startActivity(intent2)
        //       myViewModel.qrcodelivedataenabled = true
          //     myViewModel.qrcodelivedata.value = myViewModel.qrcodelivedata.value
               finish()            // 結束這個就跳回去了, 此時viewmodel 不會被重建  */

//====================================
//
        val builder = AlertDialog.Builder(this)
        var taskHandler = Handler()
        var runnable = object : Runnable {
            override fun run() {
                cameraSource.stop()
                val alert = builder.create()
                alert.show()
                taskHandler.removeCallbacksAndMessages(null)
            }
        }

        svBarcode = findViewById(R.id.sv_barcode)

        detector = BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build()
        detector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {}

            @SuppressLint("MissingPermission")
            override fun receiveDetections(p0: Detector.Detections<Barcode>?) {
                val barcodes = p0?.detectedItems
                if (barcodes!!.size() > 0) {
                    val getAfterScanQRValue = barcodes.valueAt(0).displayValue

    //                var qrcodeScanState = false   //失敗
                    val a = ArrayList<String>()
                    //判斷機制 (找特徵值）
                    if (getAfterScanQRValue.contains("00-00") &&
                        getAfterScanQRValue.contains(":") &&
                        getAfterScanQRValue.contains("http://sawellplus.gosportsart.com") &&
                        getAfterScanQRValue.contains(";") &&
                        getAfterScanQRValue.contains(";;") &&
                        getAfterScanQRValue.contains("-")
                    ) {
                        Log.d(TAG, "getAfterScanQRValue1:${getAfterScanQRValue} ")
                        //找;;起始值
                        val a0 = getAfterScanQRValue.indexOf(";;")
                        Log.d(TAG, "a0:${a0}")
                        //找檢查碼
                        val a1 = getAfterScanQRValue.subSequence(a0 + 2, a0 + 4).toString()
                        Log.d(TAG, "a1:${a1}")
                        //把抓到的值存入陣列中

                        for (i in 5..20 step 3) {
                            a.add(getAfterScanQRValue.subSequence(a0 + i, a0 + i + 2).toString())
                        }

                        var k = 0
                        for (i in a) {
                            k = k + i.toInt(16)
                            Log.d(TAG, "k: $k")
                        }

                        val cshex = k.toString(16) //80(dec)= 50(hex)
                        Log.d(TAG, "cshex: $cshex")
                        val css1 = cshex.length // 大小 (2)
                        Log.d(TAG, "css1: $css1")
                        val css3 =
                            cshex.subSequence(css1 - 2, css1) //ccs1 = 2 不含2 (range=0,2) 印出50(hex)
                        Log.d(TAG, "css3:$css3 ")
                        //比對檢查碼
                        if (css3 == a1) {
                            Log.d(TAG, "cs成功: ")

                            //       builder.setMessage(barcodes.valueAt(0).displayValue)
                            Log.d(TAG, "getAfterScanQRValue2:${a}")

                            /*                      var getAfterScanQRValue = ""
                                               //   myViewModel.qrcodescancompelteEnabled = true
                                                  for (i in a) {
                                                      getAfterScanQRValue = getAfterScanQRValue + i
                                                  }
                                                  Log.d(TAG, "getAfterScanQRValues:$getAfterScanQRValue") */
                            qrcodeScanState = true

                            Log.d(TAG, "qrcodeActivieMyViewModel: $myViewModel")
                            builder.setMessage("Scan successful")
                        } else {
                            builder.setMessage("Scan Fail - CheckSum Error")
                        }

                    } else {
                        builder.setMessage("Scan Fail")
                    }

                    builder.setPositiveButton("Close") { dialog, which ->

                        //按關閉後做
                        //按關閉後做
                        //  Log.d(TAG, "QRCodeFragment myviewmodel = ${myViewModel}")
                        //     println("getAfterScanQRValue = $getAfterScanQRValue")
                        //解析QRcode 是否正確
                        //內容為 00-00:00:00:00:00:00;93-C8:93:46:41:AF:02;;http://sawellplus.gosportsart.com
                        //有冒號的
                        if (qrcodeScanState) {
                            var getAfterScanQRValue = ""
                            for (i in a) {
                                getAfterScanQRValue = getAfterScanQRValue + i
                            }
                            Log.d(TAG, "getAfterScanQRValues:$getAfterScanQRValue")
                            myViewModel.qrcodescancompelteEnabled = true
                            myViewModel.qrcodedatafromscanner.value = getAfterScanQRValue
                            //            cameraSource.start(svBarcode.holder)
                            finish()

                        } else {
                            finish()
                        }

                    }  //關閉

                    taskHandler.post(runnable)
                }
            }
        })
        cameraSource = CameraSource.Builder(this, detector).setRequestedPreviewSize(1024, 768)
            .setRequestedFps(30f).setAutoFocusEnabled(true).build()

        svBarcode.holder.addCallback(object : SurfaceHolder.Callback2 {

            override fun surfaceCreated(p0: SurfaceHolder) {
                print("4")
                if (ContextCompat.checkSelfPermission(
                        this@QRCodeMainActivity,
                        android.Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                )
                    cameraSource.start(svBarcode.holder)
                else ActivityCompat.requestPermissions(
                    this@QRCodeMainActivity,
                    arrayOf(android.Manifest.permission.CAMERA),
                    123
                )
            }

            override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
                print("2")
            }

            override fun surfaceDestroyed(p0: SurfaceHolder) {
                print("3")
                cameraSource.stop()
            }

            override fun surfaceRedrawNeeded(p0: SurfaceHolder) {
                print("1")
            }

        })
    }


    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                cameraSource.start(svBarcode.holder)
            else Toast.makeText(this, "scanner", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        detector.release()
        cameraSource.stop()
        cameraSource.release()
    }


    private fun startTimer() {
        timer = fixedRateTimer("", false, 10000, 1000) {
            //timer 有值就會自動啟動
            Log.d(TAG, "QRmyViewModel: $myViewModel")
            stopTimer()
                 myViewModel.timeoutEnabled = true
                 myViewModel.timeoutLiveData.value = myViewModel.timeoutLiveData.value
                        finish()
        }
    }


    private fun stopTimer() {
        timer.cancel()
    }


}
