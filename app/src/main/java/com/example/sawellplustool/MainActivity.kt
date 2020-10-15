package com.example.sawellplustool

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.wifi.ScanResult
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.bytearraylesson.Parser.ResponseSendDataRequest
import com.example.getclubapiexample.MyViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.itemlayout.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

const val LOGINAPIURL = "http://202.88.100.249/SAWELLPlus_club/php/Login_server.php"
val TAG = "myTag"
var navigationSimulationCurrentPosition = 0
lateinit var networkNameToConnect: String
lateinit var wifiScanResultList: List<ScanResult>
var wifiManagerWrapper: WifiManagerWrapper? = null


class MainActivity : AppCompatActivity(), WifiScanCallbackResult, WifiConnectivityCallbackResult {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       //隱藏標題欄
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        //隱藏狀態欄
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )


        setContentView(R.layout.activity_main)


        //initData()
        //initView()

        val myViewModel = ViewModelProvider(this).get(MyViewModel::class.java)
        LoginFragment.myViewModel = myViewModel
        mainFragment.myViewModel = myViewModel
        ListFragment.myViewModel = myViewModel
        QRCodeMainActivity.myViewModel = myViewModel
        QRcodeActivity.myViewModel = myViewModel

        //    OPFragment.myViewModel = myViewModel

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //           Log.d(TAG, "Open ACCESS_COARSE_LOCATION")
// 打開這個權限才能用, 這個也要在Manifest 定義才行
            requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 0)

            //do something if have the permissions
        } else {
            //do something, permission was previously granted; or legacy device
            //    scanWifi()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //           Log.d(TAG, "Open ACCESS_COARSE_LOCATION")
// 打開這個權限才能用, 這個也要在Manifest 定義才行
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)

            //do something if have the permissions
        } else {
            //do something, permission was previously granted; or legacy device
            //    scanWifi()
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            //           Log.d(TAG, "Open ACCESS_COARSE_LOCATION")
// 打開這個權限才能用, 這個也要在Manifest 定義才行
            requestPermissions(arrayOf(Manifest.permission.CHANGE_WIFI_STATE), 0)

            //do something if have the permissions
        } else {
            //do something, permission was previously granted; or legacy device
            //    scanWifi()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //           Log.d(TAG, "Open ACCESS_COARSE_LOCATION")
// 打開這個權限才能用, 這個也要在Manifest 定義才行
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //           Log.d(TAG, "Open ACCESS_COARSE_LOCATION")
// 打開這個權限才能用, 這個也要在Manifest 定義才行
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
        }


        //do it first  (為了要跟AP連線）
        wifiManagerWrapper = WifiManagerWrapper()
        val k = wifiManagerWrapper!!.wifiManagerInti(this).autoWifiScanner(this)
        Log.d(TAG, "onCreate_k: $k")


        /*       scanBtn.setOnClickListener {
            wifiManagerWrapper = WifiManagerWrapper()
            wifiManagerWrapper!!.wifiManagerInti(this).autoWifiScanner(this)
        }

        connectBtn.setOnClickListener(View.OnClickListener {
            networkNameToConnect = networkNameEt.text.toString()
            val k = wifiManagerWrapper?.connectWifi(
                //          "sbtest", "", wifiManagerWrapper!!.None,
                networkNameEt.text.toString(),
                networkPasswordEt.text.toString(),
                wifiManagerWrapper!!.WPA_WPA2_PSK,
                this
            )    //只要回應不是null 那就是對的   */

        /*          Log.d(
                          TAG,
                          "networkNameToConnect: ${networkNameToConnect},size = ${networkNameToConnect.length} "
                      )
                      Log.d(
                          TAG,
                          "networkPasswordEt:${networkPasswordEt.text.toString()},size = ${networkPasswordEt.text.toString().length} "
                      )
            Log.d(TAG, "k:$k ")
            if (k == null) {
                Toast.makeText(this, "連接不成功", Toast.LENGTH_SHORT).show()
            }
        })  */


        /*       forgetBtn.setOnClickListener(View.OnClickListener {
                   if (wifiManagerWrapper != null)
                       wifiManagerWrapper!!.forgetWifi(networkNameEt.text.toString(), this)
               }) */

        //       writeDataToFile2()

    }

    override fun wifiFailureResult(results: MutableList<ScanResult>) {
        println("Wi-fi Failure Result*****************= $results")
        wifiScanResultList = emptyList()
    }

    override fun wifiSuccessResult(results: List<ScanResult>) {
        println("Wi-Fi Success Result******************= $results")
        wifiScanResultList = emptyList()
        wifiScanResultList = results
        //Check Available Devices
        checkDeviceConnected(wifiScanResultList)

    }


    override fun wifiConnectionStatusChangedResult() {
        println("************Connection Status Changed Result************")
        checkDeviceConnected(wifiScanResultList)

    }

    private fun checkDeviceConnected(wifiScanResultListCheck: List<ScanResult>): Boolean? {
        for (index in wifiScanResultListCheck.indices) {
            return if (wifiManagerWrapper?.isConnectedTo(wifiScanResultListCheck[index].SSID)!!) {
                wifiScanResultList[index].capabilities = "Connected"
                println("Connected")
                true
            } else {
                wifiScanResultList[index].capabilities = "Connection not established"
                println("Connected not established")
                false
            }
        }
        return null
    }

    //按Menu (3個點or 地球的動作）
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        /*
        動作
        if (item.itemId == R.id.menu_page){}
         */
        if (item.itemId == R.id.saveto) {
            Log.d(TAG, "Save To: ")

            val item = LayoutInflater.from(this@MainActivity).inflate(R.layout.itemlayout, null)
            AlertDialog.Builder(this@MainActivity)   //顯示對話框
                .setTitle(R.string.inputfilename)
                .setView(item)
                .setPositiveButton(R.string.ok)
                { dialog, which ->

                    val myViewModel = ViewModelProvider(this).get(MyViewModel::class.java)

                    val i = item.edit_text.getText().toString()
                    Log.d(TAG, "i:$i ")

                    when {
                        i == "" -> {
                            val builder = AlertDialog.Builder(this)
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

                            val builder = AlertDialog.Builder(this)
                            builder.setTitle("錯誤 ")
                            builder.setMessage("檔名內容有特殊字元 /  \\  ? , < > | * :  ")
                            builder.setNegativeButton("Ok") { dialog, which -> }
                            val alert = builder.create()
                            alert.show()
                        }
                        else -> {
                            //得到輸入檔案名
                            if (checkDataContain()) {            //判斷沒有錯誤再執行, 有錯誤不執行
                                myViewModel.inputFileName = item.edit_text.getText().toString()
                                Log.d(TAG, "myViewModelofmainactivity: ${myViewModel}")
                                myViewModel.writeFileEnabled = true
                                myViewModel.writeFileLiveData.value =
                                    myViewModel.writeFileLiveData.value
                            }
                        }


                    }  //更新數值

//判斷檔名正確再寫入然後再轉向
                    //            writeDataToFile2(item.edit_text.getText().toString()) //使用者輸入
// findNavController(R.id.fragment3).navigate(R.id.mainFragment)

                }

                .setNegativeButton("取消 ") { _, _ ->
                    Log.d(TAG, "inputfilename cancel: ")
                    //         readDataFromFile()
                }
                .show()

            /*          val builder = AlertDialog.Builder(this)
                   builder.setTitle("錯誤 ")
                   builder.setMessage("2.4GHz / 5GHz Band Switch 必須不可同時為Diabled ")
                   builder.setPositiveButton("Ok") { _, _ -> println ("abc") }
            //       val alert = builder.create()
           //        alert.show()
                       .show() */


        }
        // Restore file
        if (item.itemId == R.id.restorefrom) {
            Log.d(TAG, "RestoreFrom: ")
            //檔案清單
            findNavController(R.id.fragment3).navigate(R.id.listFragment)

        }
        return super.onOptionsItemSelected(item)
    }


    //menu & back 做法
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)  //裝上menu 出現3個點
        val actionbar = supportActionBar            //back 按鍵
        actionbar?.setDisplayHomeAsUpEnabled(true)   //這是箭頭
        actionbar?.setTitle("Back")              // 旁邊加上文字
        return super.onCreateOptionsMenu(menu)
        //      return true
    }

    override fun onSupportNavigateUp(): Boolean {
        //按back動作在此
        // fragment3 是容器的名稱放在activitymain.xml 上 , hostnav 的名稱
        /*
        0 = MainActivity
        1= loginFragement
        2= MainFragment
         */
        Log.d(TAG, "onSupportNavigateUp: $navigationSimulationCurrentPosition")
        when (navigationSimulationCurrentPosition) {
            1 -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Are you sure? ")
                builder.setMessage("Press Exit to confirm to leave, press CANCEL to cancel")
                builder.setPositiveButton("Exit") { dialog, which -> finish() }
                builder.setNegativeButton("Cancel") { dialog, which -> println("hello") }
                val alert = builder.create()
                alert.show()
            }         //login
            2 -> {
                findNavController(R.id.fragment3).navigate(R.id.loginFragment)
            }         // mainfragment
        }

        return super.onSupportNavigateUp()
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        //     LoginFragment.menu2 = menu!!

        Log.d(TAG, "onPrepareOptionsMenu1: $menu")
        if (menu != null) {
            LoginFragment.menu = menu
            mainFragment.menu = menu

            Log.d(TAG, "LoginFragment.menu2: ${LoginFragment.menu}")
        }
        val item = menu?.findItem(R.id.empty)   //可以隱藏單一的元件（item)
        item?.setVisible(false)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onBackPressed() {
        when (navigationSimulationCurrentPosition) {
            1 -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Are you sure? ")
                builder.setMessage("Press Exit to confirm to leave, press CANCEL to cancel")
                builder.setPositiveButton("Exit") { dialog, which -> finish() }
                builder.setNegativeButton("Cancel") { dialog, which -> println("hello") }
                val alert = builder.create()
                alert.show()
            }         //login
            2 -> {
                findNavController(R.id.fragment3).navigate(R.id.loginFragment)
            }         // mainfragment
        }


    }


    fun checkDataContain(): Boolean {
        var r = false

        when {
            spinnerBandSwitch2_4G.selectedItemPosition == 0 && spinnerBandSwitch5G.selectedItemPosition == 0
            -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("錯誤 ")
                builder.setMessage("2.4GHz / 5GHz Band Switch 必須不可同時為Diabled ")
                builder.setPositiveButton("Ok") { dialog, which -> }
                val alert = builder.create()
                alert.show()
            }

            //Password 可以為空白, 因為加密方式的問題
            spinnerBandSwitch2_4G.selectedItemPosition == 1 && (editText2_4G.text.toString() == "" || editTextPassword_2_4G.text.toString() == "")
            -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("錯誤 ")
                builder.setMessage("2.4GHz ssid /password 內容不可為空白 ")
                builder.setNegativeButton("Ok") { dialog, which -> }
                val alert = builder.create()
                alert.show()
            }

            spinnerBandSwitch5G.selectedItemPosition == 1 && (editText5G.text.toString() == "" || editTextPassword_5G.text.toString() == "")
            -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("錯誤 ")
                builder.setMessage("5GHz ssid /password 內容不可為空白 ")
                builder.setNegativeButton("Ok") { dialog, which -> }
                val alert = builder.create()
                alert.show()
            }

            spinnerEapMethod.selectedItemPosition != 0 && (editTextEapUserId.text.toString() == "" || editTextEapUserPassword.text.toString() == "")
            -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("錯誤 ")
                builder.setMessage("EAP ssid /password 內容不可為空白 ")
                builder.setNegativeButton("Ok") { dialog, which -> }
                val alert = builder.create()
                alert.show()
            }

            editTextSgIp1.text.toString() == "" || editTextSgIp2.text.toString() == "" || editTextSgIp3.text.toString() == "" || editTextSgIp4.text.toString() == "" -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("錯誤 ")
                builder.setMessage("SG IP Address 內容不可為空白 ")
                builder.setNegativeButton("Ok") { dialog, which -> }
                val alert = builder.create()
                alert.show()
            }
            spinnerSGIPSwitch.selectedItemPosition == 1 && (editTextSgIp1.text.toString() == "0" && editTextSgIp2.text.toString() == "0" && editTextSgIp3.text.toString() == "0" && editTextSgIp4.text.toString() == "0") -> {
                val builder = AlertDialog.Builder(this)
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


}

