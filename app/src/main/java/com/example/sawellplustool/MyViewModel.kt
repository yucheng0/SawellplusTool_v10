package com.example.getclubapiexample

import android.util.Log
import android.view.Menu
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sawellplustool.QRCodeMainActivity
import com.example.sawellplustool.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MyViewModel : ViewModel() {
    var timeoutEnabled = false
    var timeoutLiveData = MutableLiveData<Int>()
    var inputFileName = ""
    var readFileName = " "
    var readFileEnabled = false
    var readFileLiveData = MutableLiveData<Int>()
    var writeFileEnabled = false
    var writeFileLiveData = MutableLiveData<Int>()
    lateinit var menu: Menu
    var connectStatus = false
    var loginStatus = false
    var qrcodedatafromscanner = MutableLiveData<String>()
    var qrcodescancompelteEnabled = false
    var diff = 0L
    var now = 0L
    var next = 0L
    var tcpReceiverEnabled = false
    var tcpReceiverDoing = false
    var tcpReceiverLiveData = MutableLiveData<Int>()            //觀測值的變化
    var tcpReceivedData = ArrayList<Int>()          //從tcp所讀到的資料存在此
    var connectStatus1 = MutableLiveData<Boolean>()
    var keepAliveEnabled = false
    var keepAliveLiveData = MutableLiveData<Int>()

    init {
        qrcodedatafromscanner.value = ""
        tcpReceiverLiveData.value = 0
    }


    fun tcpKeepAliveTime() {
        viewModelScope.launch(Dispatchers.Main) {
            keepAlivedelay2000ms()
        }
    }

    private suspend fun keepAlivedelay2000ms() { //耗時操作delay
        delay(20000)
        Log.d(TAG, "delay2s 到")
//去執行KeepAliveTim
        QRCodeMainActivity.myViewModel.keepAliveEnabled = true
        QRCodeMainActivity.myViewModel.keepAliveLiveData.value = QRCodeMainActivity.myViewModel.keepAliveLiveData.value
    }


    fun startQrCodeTimeOutDetect(){
     var job = viewModelScope.launch(Dispatchers.Main) {
            delay1000ms()              //20sec Timeout
    }
 //       job.cancel()

    }

    private suspend fun delay1000ms() { //耗時操作delay
        delay(1000)

//去執行Timeout
        QRCodeMainActivity.myViewModel.timeoutEnabled = true
        QRCodeMainActivity.myViewModel.timeoutLiveData.value = QRCodeMainActivity.myViewModel.timeoutLiveData.value
    }


    fun fixTimeProc() {
        viewModelScope.launch(Dispatchers.Main) {
// 先執行再delay
    //        next = SystemClock.uptimeMillis() //取系統時間
       //     var x1: Long = now.toString().toLong()
      //      var x = next.minus(x1)
      //      var y = x.toString().toLong() / 1000
          //  diff = y
            delay500ms()
        }
    }

    private suspend fun delay500ms() { //耗時操作delay
        delay(500)
        Log.d(TAG, "delay500ms 到")
        tcpReceiverEnabled = true                               // 去執行Receiver的接收
        tcpReceiverLiveData.value = tcpReceiverLiveData.value

    }
}