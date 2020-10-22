package com.example.sawellplustool

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.bytearraylesson.Parser.ResponseSendDataRequest
import com.example.bytearraylesson.ResponseConnectionStatus
import com.example.bytearraylesson.Send.SendConnectioStatus
import com.example.bytearraylesson.Send.SendKeepAliveWithCounter
import com.example.getclubapiexample.MyViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.itemlayout.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket

const val HOSTIP = "192.168.10.1"            //指Wifi AP
const val PORT = 5001

//const val DATA = "taonce"
const val DATA = "0fff"
const val COMMAND_Client_send_connection_status_request = "55FE04FF0201010590"

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
enum class wifiProtocol {
    connectionStatus ,
    wifiParameter,
    KeepAlive,
    ResetRequest

}

class mainFragment : Fragment() {

    var host = HOSTIP // 主机是本机
    var port = PORT// 使用 2333 端口
    var datatext = DATA
    private var param1: String? = null
    private var param2: String? = null

    var mSocket: Socket? = null         // 客戶端
    var sSocket: Socket? = null  // 伺服器端
    var outputStream: OutputStream? = null
    var inputStream: InputStream? = null
    var sendConnectioStatusCounter = 0
    var sendWifiParameterCounter = 0
    var sendKeepAliverCounter= 0
    var sendResetRequest = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    companion object {
        lateinit var myViewModel: MyViewModel
        var menu: Menu? = null

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            mainFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //     fileMenu()
        isEnabled(true)            //重建時打開下層所有UI功能

        navigationSimulationCurrentPosition = 2

        onPrepareOptionsMenu(menu)     // 重建時, 直接打開 ActionBar 右上角的圖示
        Log.d(TAG, "menu3:${LoginFragment.menu}")
        //

        val bandSwitch2_4G_Array = arrayListOf("Disable", "Enable")
        spinnerBandSwitch2_4G.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            bandSwitch2_4G_Array
        )

        spinnerBandSwitch2_4G.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                println("bandSwitch2_4G_Array ${bandSwitch2_4G_Array[p2]}")
                //           Log.d(TAG, "BandSwitch: ${spinnerBandSwitch2_4G.selectedItemPosition}")    //選到的item 轉成數值
                when (spinnerBandSwitch2_4G.selectedItemPosition) {
                    0 -> {
                        editText2_4G.visibility = View.GONE
                        editText2_4G.setText("SportsArt_10")
                        editTextPassword_2_4G.visibility = View.GONE
                        editTextPassword_2_4G.setText("sportsart063840888")
                    }
                    1 -> {
                        editText2_4G.visibility = View.VISIBLE
                        editTextPassword_2_4G.visibility = View.VISIBLE
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        } //onItemSelectedListener end

//=================================================
        val bandSwitch5G_Array = arrayListOf("Disable", "Enable")
        spinnerBandSwitch5G.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            bandSwitch5G_Array
        )

        spinnerBandSwitch5G.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                println("bandSwitch5G_Array ${bandSwitch5G_Array[p2]}")
                when (spinnerBandSwitch5G.selectedItemPosition) {
                    0 -> {
                        editText5G.visibility = View.GONE
                        editText5G.setText("SportsArt_10_5G")
                        editTextPassword_5G.visibility = View.GONE
                        editTextPassword_5G.setText("00000000")
                    }
                    1 -> {
                        editText5G.visibility = View.VISIBLE
                        editTextPassword_5G.visibility = View.VISIBLE
                    }
                }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }


        } //onItemSelectedListener end

        //=================================================
        val eapMethod_Array = arrayListOf("None", "TLS", "TTLS", "FAST", "LEAP", "PEAP")
        spinnerEapMethod.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            eapMethod_Array
        )
        spinnerEapMethod.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                println("eapMethe_Array ${eapMethod_Array[p2]}")
                when (spinnerEapMethod.selectedItemPosition) {

                    0 -> {
                        spinnerEapInnerMethod.setSelection(0)   //它將強迫設0
                        spinnerEapInnerMethod.visibility = View.GONE
                        editTextEapUserId.visibility = View.GONE
                        editTextEapUserId.setText("SportsArtEAP")
                        editTextEapUserPassword.visibility = View.GONE
                        editTextEapUserPassword.setText("sa.eap063840888")
                    }
                    2, 4 -> {
                        spinnerEapInnerMethod.visibility = View.VISIBLE
                        editTextEapUserId.visibility = View.VISIBLE
                        editTextEapUserPassword.visibility = View.VISIBLE

                    }  // 可以選
                    1, 3 -> {
                        spinnerEapInnerMethod.setSelection(0)   //它將強迫設0
                        spinnerEapInnerMethod.visibility = View.GONE
                        editTextEapUserId.visibility = View.VISIBLE
                        editTextEapUserPassword.visibility = View.VISIBLE
                    } //不能選
                }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        } //onItemSelectedListener end

        //=================================================
        val eapInnerMethod_Array = arrayListOf("None", "MSCHAP", "MSCHAPV2")
        spinnerEapInnerMethod.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            eapInnerMethod_Array
        )
        spinnerEapInnerMethod.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                println("eapInnerMethe_Array ${eapInnerMethod_Array[p2]}")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        } //onItemSelectedListener end


        //=================================================
        val sgIpSwithc_Array = arrayListOf("Disable", "Enable")
        spinnerSGIPSwitch.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            sgIpSwithc_Array
        )


        spinnerSGIPSwitch.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                println("sgIpSwithc_Array ${sgIpSwithc_Array[p2]}")
                when (spinnerSGIPSwitch.selectedItemPosition) {
                    0 -> {
                        editTextSgIp1.visibility = View.GONE
                        editTextSgIp2.visibility = View.GONE
                        editTextSgIp3.visibility = View.GONE
                        editTextSgIp4.visibility = View.GONE
                        editTextSgIp1.setText("0")
                        editTextSgIp2.setText("0")
                        editTextSgIp3.setText("0")
                        editTextSgIp4.setText("0")
                    }
                    1 -> {
                        editTextSgIp1.visibility = View.VISIBLE
                        editTextSgIp2.visibility = View.VISIBLE
                        editTextSgIp3.visibility = View.VISIBLE
                        editTextSgIp4.visibility = View.VISIBLE
                    }
                }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }


        } //onItemSelectedListener end


//=================    按鍵的處理===========================================

        btnQrCode.setOnClickListener {
            println("Qrcode")
            val intent = Intent(context, SplashActivity::class.java)
            startActivity(intent)
        }

        btnReConnect.setOnClickListener {
            val k = wifiManagerWrapper?.connectWifi(
                "88DA1AF89BA0",
                "00000000",
                wifiManagerWrapper!!.WPA_WPA2_PSK,
                MainActivity()
            )
            //           wifiManagerWrapper!!.WPA_WPA2_PSK,
            //         )    //只要回應不是null 如果連錯ssid也會出現是對的   */
            Log.d(TAG, "k:$k ")
            if (k == null) {
                Toast.makeText(activity, "連接不成功", Toast.LENGTH_SHORT).show()
            }
        }   //onClick


        // Submit 按鍵處理
        btnSubmit.setOnClickListener {
            tcpConnect()
            //判斷是否可以傳送
            /*        if (checkDataContain2()) {
                        //           tcpSendData(COMMAND_Client_send_connection_status_request)
                        val sendConnectioStatus = SendConnectioStatus()
                        var result = sendConnectioStatus.getReadySendData()   //取得回傳值
                        //         tcpSendData(result)
                        for (i in 0..result.size - 1) {
                            Log.d(TAG, "wifiParameter0: ${result[i]}")
                        }

                        //第2種傳送
                        val sendDataToDevice = SendDataToDevice()
                        result = sendDataToDevice.getReadySendData()
                        //          tcpSendData(result)          //真的送出去
                        for (i in 0..result.size - 1) {
                            Log.d(TAG, "wifiParameter1: ${result[i]}")
                        }
                    } */
        }  //submit

        imgfile.setOnClickListener {
            /*
            val lunch = ArrayList<String>()
            lunch.add("Save To File")
            lunch.add("Restore From Files")
          val dialog =  AlertDialog.Builder(requireContext())
                    .setTitle("Please Select")
                    .setPositiveButton("Save") {_,_, -> }
                    .setNegativeButton("Restore") {_,_, -> }
                    .setIcon(R.drawable.ic_baseline_restore_24)  //指的Title的icon

                .setItems(lunch.toTypedArray()) { _, which ->
                    val name = lunch[which]
                    Toast.makeText(requireContext(), "你今天吃的是" + name, Toast.LENGTH_SHORT).show()
                }
                .show()
           dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(26F)  //Button */
            fileMenu()
        }


        textViewBackKey.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }


        //==============  觀測數值的變化 ===============
        Log.d(
            TAG, "qrcodedMyViewModel:${
                myViewModel
            } "
        )

        myViewModel.qrcodedatafromscanner.observe(viewLifecycleOwner, Observer {
            if (myViewModel.qrcodescancompelteEnabled) {
                myViewModel.qrcodescancompelteEnabled = false
                //得到QRCode 後的做法->就是利用它跟機台連接了
                val netWorkSSID1 = myViewModel.qrcodedatafromscanner.value.toString()

                val k = wifiManagerWrapper?.connectWifi(
                    netWorkSSID1,  //"88DA1AF89BA0",
                    "00000000",
                    wifiManagerWrapper!!.WPA_WPA2_PSK,
                    MainActivity()
                )
                //           wifiManagerWrapper!!.WPA_WPA2_PSK,
                //         )    //只要回應不是null 如果連錯ssid也會出現是對的   */
                Log.d(TAG, "k:$k ")
                if (k == null) {
                    val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    builder.setTitle("Error ")
                    builder.setMessage("AP Connect not successful please QRCode again")
                    builder.setPositiveButton("Ok") { dialog, which ->
                    }
                    val alert = builder.create()
                    alert.show()

                    Toast.makeText(activity, "AP 連接不成功", Toast.LENGTH_SHORT).show()
                } else {
                    Log.d(TAG, "AP 連接成功 ")
                }
                Log.d(
                    TAG,
                    "myViewModel.qrcodedatafromscanner.value = ${myViewModel.qrcodedatafromscanner.value} "
                )
                tcpConnect()

                /*               if (tcpConnect()) {           //進行TCP連線  （它的提示是連接出錯）
                                   val sendConnectioStatus = SendConnectioStatus()
                                   val result = sendConnectioStatus.getReadySendData()  // 我覺得這不是送, 這是得到資料準備去送
                                   tcpSendData(result)                 // 真正送出資料（送出後就會有回應）
                                   myViewModel.fixTimeProc()            // 每500ms讀1次TCP資料
                               } */
            }
        })
// 監控500ms到時, 再次去啟動協程

//有值的變化,就去讀資料
        myViewModel.keepAliveLiveData.observe(viewLifecycleOwner, Observer {
            if (myViewModel.keepAliveEnabled) {
                myViewModel.keepAliveEnabled = false
                myViewModel.tcpKeepAliveTime()              //繼續每2sec一次的KeepAlive傳送
//送
                val sendKeepAliveWithCounter = SendKeepAliveWithCounter()
                val result =
                    sendKeepAliveWithCounter.getReadySendData()  // 我覺得這不是送, 這是得到資料準備去送,  // 有回傳ByteArray指標
                tcpSendData(result)                 // 真正送出資料（送出後就會有回應）
                //      myViewModel.fixTimeProc()           // 啟動接收的 (已經啟動）
            }
        })



        myViewModel.tcpReceiverLiveData.observe(viewLifecycleOwner, Observer {
            if (myViewModel.tcpReceiverEnabled) {
                myViewModel.tcpReceiverEnabled = false
                myViewModel.fixTimeProc()                //啟動協程500ms
                if (!(myViewModel.tcpReceiverDoing)) {
                    tcpReceiverData()           //讀資料, 資料讀完放在viewmodel.tcpReceiverData
                }
            }
        })



        ListFragment.textWifi = textViewWifiVersionLable

        //寫檔案
        myViewModel.writeFileLiveData.observe(viewLifecycleOwner, Observer {

            if (myViewModel.writeFileEnabled) {
                myViewModel.writeFileEnabled = false
                //          val r = checkDataContain()
                //           Log.d(TAG, "r: $r ")
                //          if (checkDataContain() == true) {//檢查檔案內容是否真的可以寫
                Log.d(TAG, "寫檔案: ")
                writeDataToFile(myViewModel.inputFileName)

            }  //寫入檔名
        })


//讀檔案   要用myviewModel.readFileLiveData.value 才會被觸發
        myViewModel.readFileLiveData.observe(viewLifecycleOwner, Observer {

            if (myViewModel.readFileEnabled) {
                myViewModel.readFileEnabled = false
                Log.d(TAG, "讀檔案: ")
                readDataFromFile(myViewModel.readFileName)
                //        readDataFromFile("sa")
            }
        })

/*editTextSgIp1.setOnClickListener {
    Log.d(TAG, "我有進來: ")
    if (editTextSgIp1.text.toString() == "0") {
        Log.d(TAG, "為什麼沒有清除: ")
        editTextSgIp1.setText("")
    }

}*/

        //SGIP 監控
        editTextSgIp1.addTextChangedListener(textWatcher1)
        editTextSgIp2.addTextChangedListener(textWatcher2)
        editTextSgIp3.addTextChangedListener(textWatcher3)
        editTextSgIp4.addTextChangedListener(textWatcher4)


    }  //OnActivityCreated end


    // 　=================         TCP 副程式開始     =====================
    // Client connect 連接
    fun tcpConnect(): Boolean {
        var bool = false
        GlobalScope.launch {
            mSocket = Socket()
            try {
                //             host = editTextTargetIP.text.toString()
                //             port = editTextTargetPort.text.toString().toInt()
                host = HOSTIP
                port = PORT

                mSocket!!.connect(InetSocketAddress(host, port), 2000)
                if (mSocket!!.isConnected) {
// sendData("taonce")
// receiverData()
                    activity?.runOnUiThread { textViewConnectStatus.text = "Client 連接成功" }
                    //       字是紅色的
                    //         btnConnect.setTextColor(android.graphics.Color.RED)
                    bool = true

                    //連線成功後, 執行開始通訊的動作, 是否要delay一下
                    val sendConnectioStatus = SendConnectioStatus()
                    val result = sendConnectioStatus.getReadySendData()  // 我覺得這不是送, 這是得到資料準備去送
                    tcpSendData(result)                 // 真正送出資料（送出後就會有回應）
                    sendConnectioStatusCounter++
                    myViewModel.fixTimeProc()            // 每500ms讀1次TCP資料(讀資料）
                    //
                    //                myViewModel.tcpKeepAliveTime()      //啟動keepAlive
                }
            } catch (e: Exception) {
                activity?.runOnUiThread { textViewConnectStatus.text = "Client 連接出錯" }
                Log.d(TAG, "连接出错：${e.message}")
                bool = false
            }
        } //globalscope
        return bool
    }

    /*動作： Client 送資料
     input:  Byte  =======================
     */
    fun tcpSendData(msg: ByteArray) {
        GlobalScope.launch {
            if (mSocket!!.isConnected) {
                Log.d(TAG, "SendData 我進來了 ")
                //          Log.d(TAG, "mSocket,$mSocket")
                try {
                    outputStream = mSocket?.getOutputStream()

                    var s = ""
                    for (i in msg) {
                        s = s + i
                    }
                    Log.d(TAG, "s: $s")
                 //   textViewMessage.text ="Tx:" + textViewMessage.text.toString() + s + "\n"
                    /*                  val size = message.length / 2  // 忽略奇數值, 若輸入3個字串只處理1個byte
                                      val msg = ByteArray(size)           // 取得size

                                      for (i in 0..size - 1) {         //取值
                                          msg[i] = message.subSequence(i * 2, i * 2 + 2)
                                              .toString()
                                              .toInt(16)
                                              .toByte()
                                      }  */
                    outputStream!!.write(msg)           //寫入資料 （Hex16進制）
                    outputStream!!.flush()             //即時送出

                    Log.d(TAG, "发送给服务端内容为：$msg")
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d(TAG, "sendData: 發送錯誤")
                }
            } //if  end
        } //協程End
    }

    /* ================Client 接收數據 ==================
    output: 16進制放在Viewmodel.tcpRecevierData內
    */
    fun tcpReceiverData() {
        GlobalScope.launch {
            if (mSocket!!.isConnected) {
                myViewModel.tcpReceiverDoing = true    //正在做
                Log.d(TAG, "receiverData: 接收進來了")
                try {
                    val inputStream = mSocket?.getInputStream()
                    val data = BufferedReader(InputStreamReader(inputStream, "ISO-8859-1"))
                    val cbuf = CharArray(1024)            // 1次讀1024字元
                    var num = data.read(cbuf)              // 這是阻塞式,就是一直等到有值為止
                    //     myViewModel.tcpReceiverData.clear()     // 清空再做
//我不要字串, 我要cbuf的東西
                    for (i in 0..num - 1) {
                      myViewModel.tcpReceivedData.add(cbuf[i].toInt())
   //                               Log.d(TAG, "获取服务端内容为0：${myViewModel.tcpReceivedData[i]}")   //這是10進制顯示
                 }
                    //以下是顯示debug用對我沒有太大用途
                    Log.d(TAG, "获取服务端數目为：$num")
                    var s = ""
                    for (i in 0..num - 1) {
                        s = s + cbuf[i].toInt().toString(16)     // 重點在toString(16)
                        Log.d(TAG, "获取服务端内容为1：${cbuf[i].toInt()}")   //這是10進制顯示
                    }

                    //   myViewModel.tcpReceiverData = s                 // 暫存收到的資料到ViewModel
                    activity?.runOnUiThread {
                        textViewMessage.text = "Recv:" + textViewMessage.text.toString() + s + "\n"
                    }   //印出結果


                    myViewModel.tcpReceiverDoing = false            //做完了, 讀結束了
// 讀到了資料要判斷
                    if (sendConnectioStatusCounter>0){
                        Log.d(TAG, "進行解析: ")
                        sendConnectioStatusCounter--              //做一次
                      val R = ResponseConnectionStatus()
                         R.parseCheck()

                    }

  //                  myViewModel.tcpKeepAliveTime()    //啟動keepAlive

                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d(TAG, "receiveData: 接收錯誤")
                    myViewModel.tcpReceiverDoing = false    //做完了,讀結束了
                }
            }
        } //if 結束
    }    //協程結束 */

    /*==============================   讀檔案 ==================================
    * 注意：是否會讀到空的值造成當機
    *
     */
    fun readDataFromFile(fileName: String) {
        GlobalScope.launch {
            //        val fileName = "my_data"
            val gson = Gson()
            var sb = StringBuffer()
            //進來先讀出值
            // read file....................
            try {
                val inputStream = getActivity()?.openFileInput(fileName)
                val bytes = ByteArray(1024)
                //    val sb = StringBuffer()
                while (inputStream?.read(bytes) != -1) {         //1次讀1024byte
                    sb.append(String(bytes))
                }
                var ss: String = ""                         // 必須取出正確的值才行
                for (i in sb) {                             //這一部分是字串錯誤, 看起來是對的但是長度卻有 1024
                    if (i == '}') {                         // 重新存入一個新的字串內
                        ss = ss + i
                        break                               // 跳出回圈
                    } else {
                        ss = ss + i
                    }
                }


                var jsonData3 = gson.fromJson(ss, DataRecordOnlyForFile::class.java)

                val re = ResponseSendDataRequest()   // 先前要先得到這個資料才行
                re.parser(118)            // 這個呼叫要做不然會當機
                //還原資料 （資料取得是從myViewModel.tcpReceiverData 來的

                // wifi version : 0x31 0x37 0x38 = 313738
                val sb1 = StringBuffer()

                sb1.append("v")
                for (i in 0..2) {
                    sb1.append(re.wifiVersion[i].toChar().toString())
                    if (i == 0) {
                        sb1.append(".")
                    }
                }
                textViewWifiVersion.text = sb1

                //處理Mac address 顯示
                sb1.delete(0, sb1.length)   //清空
                for (i in 0..5) {
                    if (re.macAddress[i] <= 9) {
                        sb1.append("0" + re.macAddress[i].toString(16))
                    } else {
                        sb1.append(re.macAddress[i].toString(16))
                    }
                    if (i != 5) {
                        sb1.append(":")
                    }
                }
                textViewMacAddress.text = sb1
//
                spinnerBandSwitch2_4G.setSelection(jsonData3.bandSwitch2_4G)
                editText2_4G.setText(jsonData3.ssid_2_4G)
                editTextPassword_2_4G.setText(jsonData3.password_2_4G)
                spinnerBandSwitch5G.setSelection(jsonData3.bandSwitch_5G)
                editText5G.setText(jsonData3.ssid_5GString)
                editTextPassword_5G.setText(jsonData3.password_5G)
                spinnerEapMethod.setSelection(jsonData3.eapMethod)
                spinnerEapInnerMethod.setSelection(jsonData3.eapInnerMethod)
                editTextEapUserId.setText(jsonData3.eapUserId)
                editTextEapUserPassword.setText(jsonData3.eapUserPassword)
                //if sgip1~ 4 = 0.0.0.0 then set sgswitch = disable
                spinnerSGIPSwitch.setSelection(jsonData3.sgIPSwitch)
                editTextSgIp1.setText(jsonData3.sgIP1)
                editTextSgIp2.setText(jsonData3.sgIP2)
                editTextSgIp3.setText(jsonData3.sgIP3)
                editTextSgIp4.setText(jsonData3.sgIP4)

                //處理序號顯示
                sb1.delete(0, sb1.length)   //清空字串列
                for (i in 0..6) {
                    sb1.append(re.serialNumber[i].toChar())
                }
                textViewSerialNumber.text = sb1
//
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                Log.d(TAG, "readFile: 檔案不存在2 ")
                // 設初值
            }
        }

    }

    /*  =============    寫檔案  ====================
    * 注意：必須處理空字串？ 還是不用呢？ 空的就存空的就好了，會不會當機啊！
    ** */
    fun writeDataToFile(fileName: String) {
        Log.d(TAG, "writeDataToFile: Hi")


        GlobalScope.launch {
            //           val fileName = "my_data"
            val gson = Gson()
//檔案不存在,導航會當機
            // set Json  寫入資料 (若輸入值是空的會當機）
            val responseSendDataRequest = ResponseSendDataRequest()
            responseSendDataRequest.parser(118)   //測試值是118bytes
            val a = spinnerBandSwitch2_4G.selectedItemPosition
            Log.d(TAG, "writeDataToFile A: $a")
            val b = spinnerBandSwitch5G.selectedItemPosition
            Log.d(TAG, "writeDataToFile b: $b")

            //判斷若sgip = 0.0.0.0 則強迫它為Disabled 存入
/*            var sgIPSwitchValue = 0
            if (editTextSgIp1.text.toString() == "0" &&
                editTextSgIp2.text.toString() == "0" &&
                editTextSgIp3.text.toString() == "0" &&
                editTextSgIp4.text.toString() == "0"
            ) {
                sgIPSwitchValue = 0            //強迫它為0
            } else {
                sgIPSwitchValue = spinnerSGIPSwitch.selectedItemPosition
            }
*/
            //先檢查是否為空,為空就要填空值
            val data0 = DataRecordOnlyForFile(
                //      textViewWifiVersion.text.toString(),
                //     textViewMacAddress.text.toString(),
                //                     spinnerBandSwitch2_4G.selectedItemPosition,     //0=disable , 1=Enable
                1,
                editText2_4G.text.toString(),
                editTextPassword_2_4G.text.toString(),
                spinnerBandSwitch5G.selectedItemPosition,
                editText5G.text.toString(),
                editTextPassword_5G.text.toString(),
                spinnerEapMethod.selectedItemPosition,
                spinnerEapInnerMethod.selectedItemPosition,     //選到的item 轉成數值
                editTextEapUserId.text.toString(),
                editTextEapUserPassword.text.toString(),
                spinnerSGIPSwitch.selectedItemPosition,                        //o or 1
                editTextSgIp1.text.toString(),
                editTextSgIp2.text.toString(),
                editTextSgIp3.text.toString(),
                editTextSgIp4.text.toString()
                //     textViewSerialNumber.text.toString()
            )

/* 測試值

            val data0 = DataRecordOnlyForFile(
                /*         "313738",         //0x31,0x37,0x38
                         "88DA1AF95768",  // "88:DA:1A:F9:57:68" */
                1,
                "SportsArt_10",
                "SportsArt063840888",
                1,
                "SportArt10_5G",
                "00000000",
                2,
                1,
                "SportsAtEAP",
                "sa.sportsart063840888",
                "194",
                "168",
                "0",
                "112"

                //            "1234567"
            )  */


            var jsonData = gson.toJson(data0)
            //     println("jsonData = ${jsonData}")

            // write file
            try {
                val outputStream = getActivity()?.openFileOutput(fileName, Context.MODE_PRIVATE)
                outputStream?.write(jsonData.toByteArray())
                outputStream?.close()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            } //catch end

//載入值
            //       myViewModel.SELECTED = s15
            //=============================================================================

        }

    }


    fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        Log.d(TAG, "onPrepareOptionsMenu2: $menu")
        val item = menu?.findItem(R.id.empty)   //可以隱藏單一的元件（item)
        item?.setVisible(true)
        return true

    }


    // 文字監控

    val textWatcher1 = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            Log.d(TAG, "p0: $p0 ")  // 回應輸入的文字
            Log.d(TAG, "p1: $p1 ")  //它是索引值從 0 開始
            Log.d(TAG, "p2: $p2 ")
            Log.d(TAG, "p3: $p3 ")

            if (p0?.length == 2 && p0[0].toString() == "0") {
                Log.d(TAG, "onTextChanged進來可: ")
                val a = p0[1].toString()
                editTextSgIp1.setText(a)
                editTextSgIp1.setSelection(editTextSgIp1.length())  //移動cursor

            }


            /*      if (p0.toString() == "") {
                      editTextSgIp2.setText("0")
                  }  */
            if (p0?.length == 3 && p0.toString().toInt() > 255) {               //等於2就是索引值3
                editTextSgIp1.setText("")
            }
            if (p0?.length == 3 && p0.toString().toInt() <= 255) {               //跳到下一個輸入
                editTextSgIp2.requestFocus()
            }
        }

        override fun afterTextChanged(p0: Editable?) {

        }

    }


    val textWatcher2 = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            Log.d(TAG, "p0: $p0 ")  // 回應輸入的文字
            Log.d(TAG, "p1: $p1 ")  //它是索引值從 0 開始
            Log.d(TAG, "p2: $p2 ")
            Log.d(TAG, "p3: $p3 ")

            if (p0?.length == 2 && p0[0].toString() == "0") {
                Log.d(TAG, "onTextChanged進來可: ")
                val a = p0[1].toString()
                editTextSgIp2.setText(a)
                editTextSgIp2.setSelection(editTextSgIp2.length())  //移動cursor

            }


            /*      if (p0.toString() == "") {
                      editTextSgIp2.setText("0")
                  }  */
            if (p0?.length == 3 && p0.toString().toInt() > 255) {               //等於2就是索引值3
                editTextSgIp2.setText("")
            }
            if (p0?.length == 3 && p0.toString().toInt() <= 255) {               //跳到下一個輸入
                editTextSgIp3.requestFocus()
            }
        }

        override fun afterTextChanged(p0: Editable?) {

        }

    }


    val textWatcher3 = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            Log.d(TAG, "p0: $p0 ")  // 回應輸入的文字
            Log.d(TAG, "p1: $p1 ")  //它是索引值從 0 開始
            Log.d(TAG, "p2: $p2 ")
            Log.d(TAG, "p3: $p3 ")

            if (p0?.length == 2 && p0[0].toString() == "0") {
                Log.d(TAG, "onTextChanged進來可: ")
                val a = p0[1].toString()
                editTextSgIp3.setText(a)
                editTextSgIp3.setSelection(editTextSgIp3.length())  //移動cursor

            }


            /*         if (p0.toString() == "") {
                         editTextSgIp3.setText("0")
                     } */
            if (p0?.length == 3 && p0.toString().toInt() > 255) {               //等於2就是索引值3
                editTextSgIp3.setText("")
            }
            if (p0?.length == 3 && p0.toString().toInt() <= 255) {               //跳到下一個輸入
                editTextSgIp4.requestFocus()
            }
        }

        override fun afterTextChanged(p0: Editable?) {

        }

    }

    val textWatcher4 = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            Log.d(TAG, "p0: $p0 ")  // 回應輸入的文字
            Log.d(TAG, "p1: $p1 ")  //它是索引值從 0 開始
            Log.d(TAG, "p2: $p2 ")
            Log.d(TAG, "p3: $p3 ")

            if (p0?.length == 2 && p0[0].toString() == "0") {
                Log.d(TAG, "onTextChanged進來可: ")
                val a = p0[1].toString()
                editTextSgIp4.setText(a)
                editTextSgIp4.setSelection(editTextSgIp4.length())  //移動cursor

            }


            /*          if (p0.toString() == "") {
                          editTextSgIp4.setText("0")
                      }  */
            if (p0?.length == 3 && p0.toString().toInt() > 255) {               //等於2就是索引值3
                editTextSgIp4.setText("")
            }
        }

        override fun afterTextChanged(p0: Editable?) {

        }

    }

    /*  mEtSearch = editText 控件
        https://juejin.im/post/6844904164632297479
     */
    fun hideSoftInput() {
        Log.d(TAG, "hideSoftInput: ")
        val inputManager = requireActivity()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(editTextSgIp1.windowToken, 0)
    }

    fun checkDataContain2(): Boolean {
        var r = false

        when {
            spinnerBandSwitch2_4G.selectedItemPosition == 0 && spinnerBandSwitch5G.selectedItemPosition == 0
            -> {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("錯誤 ")
                builder.setMessage("2.4GHz / 5GHz Band Switch 必須不可同時為Diabled ")
                builder.setPositiveButton("Ok") { dialog, which -> }
                val alert = builder.create()
                alert.show()
            }

            //Password 可以為空白, 因為加密方式的問題
            spinnerBandSwitch2_4G.selectedItemPosition == 1 && (editText2_4G.text.toString() == "" || editTextPassword_2_4G.text.toString() == "")
            -> {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("錯誤 ")
                builder.setMessage("2.4GHz ssid /password 內容不可為空白 ")
                builder.setNegativeButton("Ok") { dialog, which -> }
                val alert = builder.create()
                alert.show()
            }

            spinnerBandSwitch5G.selectedItemPosition == 1 && (editText5G.text.toString() == "" || editTextPassword_5G.text.toString() == "")
            -> {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("錯誤 ")
                builder.setMessage("5GHz ssid /password 內容不可為空白 ")
                builder.setNegativeButton("Ok") { dialog, which -> }
                val alert = builder.create()
                alert.show()
            }

            spinnerEapMethod.selectedItemPosition != 0 && (editTextEapUserId.text.toString() == "" || editTextEapUserPassword.text.toString() == "")
            -> {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("錯誤 ")
                builder.setMessage("EAP ssid /password 內容不可為空白 ")
                builder.setNegativeButton("Ok") { dialog, which -> }
                val alert = builder.create()
                alert.show()
            }

            editTextSgIp1.text.toString() == "" || editTextSgIp2.text.toString() == "" || editTextSgIp3.text.toString() == "" || editTextSgIp4.text.toString() == "" -> {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("錯誤 ")
                builder.setMessage("SG IP Address 內容不可為空白 ")
                builder.setNegativeButton("Ok") { dialog, which -> }
                val alert = builder.create()
                alert.show()
            }
            spinnerSGIPSwitch.selectedItemPosition == 1 && (editTextSgIp1.text.toString() == "0" && editTextSgIp2.text.toString() == "0" && editTextSgIp3.text.toString() == "0" && editTextSgIp4.text.toString() == "0") -> {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("錯誤 ")
                builder.setMessage("SG IP Address 不允許為 0.0.0.0 ")
                builder.setNegativeButton("Ok") { dialog, which -> }
                val alert = builder.create()
                alert.show()
                //收起鍵盤
                //                    hideSoftInput()
            }


            else -> {
                r = true
            }
        }   //when end  */
        return r
    }


    fun fileMenu() {

        isEnabled(false)

        val image = intArrayOf(
            R.drawable.ic_baseline_save_alt_24,
            R.drawable.ic_baseline_restore_24,
            R.drawable.ic_baseline_cancel_24
        )
        val imgText = arrayOf("Save", "Restore", "Cancel")

        val items = ArrayList<Map<String, Any>>()    //List<＞　括號是型別，Map<String,Any> 就是型別
        for (i in image.indices) {
            val item = HashMap<String, Any>()        //宣告key名, 好像HashMap 不可少
            item["image1"] = image[i]
            item["text1"] = imgText[i]
            items.add(item)                         //再寫入 (把圖及對應的文字放入items)
        }
/* SimpleAdapter 需要map  (image1 對應R.id.image ; text1 對應 R.id.text  ==> 即1張圖1個文字
items HasMap, 資料內容
*/
        val adapter = SimpleAdapter(
            requireContext(),
            items, R.layout.filemenulayout, arrayOf("image1", "text1"),
            intArrayOf(R.id.image, R.id.text)
        )

        main_page_gridview.numColumns = 1 // 分3列, 它其實也是listview的應用 （顯示3欄的排列）
// girdview的adapter就要找另一個adapter來銜接, 那它就找到adapter 是一個simpleAdapter
        main_page_gridview.adapter = adapter    //對接adapter

        main_page_gridview.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->

                if (position == 0) {               //選到Save
                    //           Toast.makeText(requireContext(), "你選擇了" + imgText[position], Toast.LENGTH_SHORT).show()
                    isEnabled(true)                                 //恢復正常
//
                    val item =
                        LayoutInflater.from(requireContext()).inflate(R.layout.itemlayout, null)
                    AlertDialog.Builder(requireContext())   //顯示對話框
                        .setTitle(R.string.inputfilename)
                        .setView(item)

                        .setPositiveButton(R.string.ok)
                        { dialog, which ->

                            //           val myViewModel = ViewModelProvider(viewLifecycleOwner).get(MyViewModel::class.java)

                            val i = item.edit_text.getText().toString()
                            Log.d(TAG, "i:$i ")

                            when {
                                i == "" -> {
                                    val builder = AlertDialog.Builder(requireContext())
                                    builder.setTitle("錯誤 ")
                                    builder.setMessage("檔名不得為空")
                                    builder.setNegativeButton("Ok") { dialog, which -> }
                                    val alert = builder.create()
                                    alert.show()
                                }
//不允許檔案字元判斷
                                i.contains(">") ||
                                        i.contains("/") || i.contains("*") ||
                                        i.contains("?") || i.contains("\\") ||
                                        i.contains(",") || i.contains("<") ||
                                        i.contains("|") || i.contains(":") -> {

                                    val builder = AlertDialog.Builder(requireContext())
                                    builder.setTitle("錯誤 ")
                                    builder.setMessage("檔名內容有特殊字元 /  \\  ? , < > | * :  ")
                                    builder.setNegativeButton("Ok") { dialog, which -> }
                                    val alert = builder.create()
                                    alert.show()
                                }
                                else -> {
                                    //得到輸入檔案名
                                    if (checkDataContain2()) {            //判斷沒有錯誤再執行, 有錯誤不執行
                                        myViewModel.inputFileName =
                                            item.edit_text.getText().toString()
                                        Log.d(TAG, "myViewModelofmainactivity: ${myViewModel}")
                                        myViewModel.writeFileEnabled = true
                                        myViewModel.writeFileLiveData.value =
                                            myViewModel.writeFileLiveData.value
                                    }
                                }
                            }

                        }   //.setPositiveButton = ok

                        .setNegativeButton("取消 ") { _, _ ->
                            Log.d(TAG, "inputfilename cancel: ")
                            //         readDataFromFile()
                        }
                        .show()
                }


                if (position == 1) {             //選到Restore
                    findNavController().navigate(R.id.listFragment)   //跳到另一個fragment
                }


                if (position == 2) {
                    isEnabled(true)
                }
//============


            }


    }


    fun isEnabled(bool: Boolean) {

        if (bool == true) {
            frameLayout01.visibility = View.GONE
            linearLayoutV0.alpha = 1.0f
        } else {
            frameLayout01.visibility = View.VISIBLE
            linearLayoutV0.alpha = 0.1f
        }


        spinnerBandSwitch2_4G.isEnabled = bool
        editText2_4G.isEnabled = bool
        editTextPassword_2_4G.isEnabled = bool

        spinnerBandSwitch5G.isEnabled = bool
        editText5G.isEnabled = bool
        editTextPassword_5G.isEnabled = bool

        spinnerEapMethod.isEnabled = bool
        spinnerEapInnerMethod.isEnabled = bool
        editTextEapUserId.isEnabled = bool
        editTextEapUserPassword.isEnabled = bool

        spinnerSGIPSwitch.isEnabled = bool
        editTextSgIp1.isEnabled = bool
        editTextSgIp2.isEnabled = bool
        editTextSgIp3.isEnabled = bool
        editTextSgIp4.isEnabled = bool

        btnQrCode.isEnabled = bool
        btnSubmit.isEnabled = bool
        btnReConnect.isEnabled = bool
        textViewBackKey.isEnabled = bool
    }


}