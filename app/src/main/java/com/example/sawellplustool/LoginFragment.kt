package com.example.sawellplustool

//val TAG = "myTag"
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.text.InputType.*
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.getclubapiexample.MyViewModel
import com.google.gson.Gson
import com.squareup.okhttp.*
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import androidx.appcompat.app.AlertDialog

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var client = OkHttpClient()                            //要一個實例
    var JSON = MediaType.parse("application/json; charset=utf-8")
    var phpsessid = ""

    // var body = RequestBody.create(JSON, "{\"account\":\"w06\" ,\"pw\":\"w\" , \"timezone\":8 }")
    lateinit var request: Request
    lateinit var response: Response
    lateinit var bodyData: String


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

        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    companion object {
        lateinit var myViewModel: MyViewModel
        var menu: Menu? = null

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        navigationSimulationCurrentPosition = 1


        //       readFile()                            //開機就去讀取

        val accountRecored = AccountRecord(false, "w06@gmail.com", "w")
        //      accountRecored.save(requireContext())   //寫到SharedPreferences 內, 檔名是固定MyShared
        accountRecored.read(requireContext())  //得到x,y,z值
        Log.d(
            TAG,
            "x,y,z: ${accountRecored.returen_remeberme},${accountRecored.return_sgAccount},${accountRecored.return_sgPassword}} "
        )

        checkBoxRemembeMe.isChecked = accountRecored.returen_remeberme
        editTextSgAccount.setText(accountRecored.return_sgAccount)
        editTextSgPassword.setText(accountRecored.return_sgPassword)

       checkBoxShowPassword.setOnClickListener{
           if (checkBoxShowPassword.isChecked) {
               //顯示密碼
               editTextSgPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
           }
        else  {
               //隱藏密碼
            editTextSgPassword.setTransformationMethod(PasswordTransformationMethod.getInstance())
    }
       }


        keyProc(myViewModel)                    // key處理
        onPrepareOptionsMenu(menu)
        Log.d(TAG, "menu2:$menu")
    }        //OnCreate End

    //keyProc
    fun keyProc(myViewModel: MyViewModel) {
        //        按鍵
        login.setOnClickListener {
            val accountRecored = AccountRecord(
                checkBoxRemembeMe.isChecked,
                editTextSgAccount.text.toString(),
                editTextSgPassword.text.toString()
            )

            if (editTextSgAccount.getText().toString() == "" || editTextSgPassword.getText()
                    .toString() == ""
            ) {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("錯誤 ")
                builder.setMessage("輸入值為空")
                builder.setPositiveButton("Ok") { dialog, which -> }
                val alert = builder.create()
                alert.show()
            } else {
                //        if (checkBoxRemembeMe.isChecked) {
                //            accountRecored.save(requireContext())
                //       }
                //   writeFile()
                post_login(myViewModel, accountRecored)
            }
        }
        textViewHelpKey.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("如何操作 ")
            builder.setMessage("1.找到在機台的紅色按鈕, 並在10秒內快速按5下進入設定模式" +
                    "\n2.Login系統 （底下的步驟都是進入系統後才能做的動作）" +
                    "\n3.在最下方按QRcode 按鈕去掃描機台QRcode 標千" +
                    "\n4. 進行2.4G/5G/EAP 相關的設定" +
                    "\n5.按Submit 去執行寫入系統" +
                    "\n*. 若你有多台並想要做同一個動作, 那麼你可以按右上方像磁碟機的按鈕將目前的設定值寫入檔案到系統, 在下一台設定時, 先掃描QRCode後取得連線, 並按此按鈕把它叫出之前的設定")

            builder.setPositiveButton("Ok") { dialog, which -> }
            val alert = builder.create()
            alert.show()
        }
    }

//================================================================================
    //  副程式在此

    fun post_login(myViewModel: MyViewModel, accountRecord: AccountRecord) {
     var flag0 = false
        accountRecord.read(requireContext())
        Log.d(TAG, "paccountRecord.sgAccount: ${accountRecord.return_sgAccount}")
        Log.d(TAG, "paccountRecord.sgPassword: ${accountRecord.return_sgPassword}")

        if (accountRecord.return_sgAccount == editTextSgAccount.text.toString() && accountRecord.return_sgPassword == editTextSgPassword.text.toString()) {
            Log.d(TAG, "讀取記錄成功: ")

            activity?.runOnUiThread(java.lang.Runnable {
                findNavController().navigate(R.id.mainFragment)   //成功跳往，不用讀網路的, 並非結束還是在背景做
          flag0 = true
            })

        }
        else {
            Log.d(TAG, "記錄錯誤 : ")
            flag0 = false
        }

       if (!flag0) {

           GlobalScope.launch(Dispatchers.IO) {
               //要使用Default,unconfined,IO , 用Main會當機, 奇怪
               val url = LOGINAPIURL
               client = OkHttpClient()                            //要一個實例
               JSON = MediaType.parse("application/json; charset=utf-8")

               val ACCOUNT = editTextSgAccount.text.toString()
               val PASSWORD = editTextSgPassword.text.toString()
               val jsondata = """{  "account":"$ACCOUNT",
                                 "pw": "$PASSWORD" ,
                                 "timezone":8
                                }"""

               val body = RequestBody.create(JSON, jsondata)
               request = Request.Builder()                    //建立需求
                   .url(url)
                   .post(body)
                   .build()



               try {

                   response = client.newCall(request).execute()            // 取得回應到response 來
                   bodyData = response.body()!!.string()
                   println("bodyData = $bodyData ")
                   myViewModel.loginStatus = bodyData.contains("\"status\":1")  //找結果
                   println("loginStatus=${myViewModel.loginStatus}")

                   activity?.runOnUiThread(java.lang.Runnable {
                       if (myViewModel.loginStatus) {

                           //登入成功
                           if (checkBoxRemembeMe.isChecked) {
                               accountRecord.save(requireContext())
                           }


                           findNavController().navigate(R.id.mainFragment)


                       } else {
                           //              Toast.makeText(context, "Login fail", Toast.LENGTH_SHORT).show()
                           val builder = AlertDialog.Builder(requireContext())
                           builder.setTitle("Login Error")
                           builder.setMessage("Please enter the correct SG account and password or contact the SportsArt's agent to get it.\n 只要登入成功一次之後就會記錄起來, 下次就用這個帳號登入即可")
                           builder.setPositiveButton("Ok") { dialog, which -> println("hello") }
                           val alert = builder.create()
                           alert.show()
                       }
                   })
               } catch (e: Exception) {
                   println("Error!!!!")
               }
           }   //GlobleScope
       }

    }   //post login


    /*==============================   讀檔案 ==================================
    *
    *
     */

    fun readFile() {
        GlobalScope.launch {
            val fileName = "my_file"
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

                var jsonData3 = gson.fromJson(ss, AccountRecord::class.java)
                //還原資料
                Log.d(TAG, "jsonData3.selected =${jsonData3.remeberme} ")
                checkBoxRemembeMe.isChecked = jsonData3.remeberme
                editTextSgAccount.setText(jsonData3.sgAccount)
                editTextSgPassword.setText(jsonData3.sgPassword)


            } catch (e: Exception) {
                e.printStackTrace()
                Log.d(TAG, "readFile: 檔案不存在2 ")
                // 設初值
            }
        }

    }
/*  =============    寫檔案  ====================
*
* */

    fun writeFile() {

        GlobalScope.launch {
            val fileName = "my_file"
            val gson = Gson()
            var sb = StringBuffer()
            // 回存到viewmodel 內, 統一管理數據
            Log.d(TAG, "checkBox.isChecked = ${checkBoxRemembeMe.isChecked} ")
            // check box

//檔案不存在,導航會當機
            // set Json  寫入資料
            val s1 = checkBoxRemembeMe.isChecked   //20200831
            val data0 = AccountRecord(
                s1,
                editTextSgAccount.text.toString(),
                editTextSgPassword.text.toString()
            )

            //        val data1 =  DataSetRecord(1000, 30, 30, 60)
            //        val dataArray = listOf<DataSetRecord>(data0,data1)

            var jsonData = gson.toJson(data0)
            //     println("jsonData = ${jsonData}")

            // write file
            try {
                val outputStream = getActivity()?.openFileOutput(fileName, Context.MODE_PRIVATE)
                outputStream?.write(jsonData.toByteArray())
                outputStream?.close()
            } catch (e: Exception) {
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
        item?.setVisible(false)
        return true
    }


}
