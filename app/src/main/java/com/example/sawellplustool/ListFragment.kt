package com.example.sawellplustool

import android.content.DialogInterface
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.fragment.findNavController
import com.example.getclubapiexample.MyViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    //
    val fixPath = "/data/data/com.example.sawellplustool/files"
    private val IMG_ITEM = "image"
    private val NAME_ITEM = "name"
    private var filesList: MutableList<Map<String, Any?>>? = null
    private var names: MutableList<String>? = null
    private var paths: MutableList<String>? = null
    private var files: Array<File>? = null

    //private var filesMap: MutableMap<String, Any?>? = null
    private var filesMap = HashMap<String, Any>()
    private val fileImg = intArrayOf(
        R.drawable.directory,
        R.drawable.file
    )
    private var simpleAdapter: SimpleAdapter? = null

    //   private var list_view: ListView? = null
    private var nowPath: String? = null
    private var createDir: TextView? = null
    private val delFile: TextView? = null
    private val modifyName: TextView? = null


    //
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
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    companion object {
        private val ROOT = Environment.getExternalStorageDirectory().absolutePath
        private const val PRE_LEVEL = ".."
        const val FIRST_ITEM = 0
        const val SECOND_ITEM = 1
        private val ACTION = arrayOf("修改", "刪除")
        lateinit var textWifi: TextView
        lateinit var myViewModel: MyViewModel

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)



        initData()
        initView()

        textViewBackKey.setOnClickListener {
            findNavController().navigate(R.id.mainFragment)     //跳回來
        }
    }

    private fun initView() {
        simpleAdapter = SimpleAdapter(
            requireContext(),               //this
            filesList,                      //mutableList<map<String,Any>>
            R.layout.simple_adapter,        // 客制化清單（內含有id 有 text , image 2個控件 )
            arrayOf(IMG_ITEM, NAME_ITEM),   // 對應IMG_ITEM, NAME_ITEM  key : value
            intArrayOf(R.id.image, R.id.text)
        )


        //    list_view = findViewById(R.id.list_view) as ListView
        list_view.adapter = simpleAdapter          //adatper 綁定

        list_view.onItemClickListener =            //監聽按下的元件 (點擊）
            AdapterView.OnItemClickListener { parent, view, position, id ->       //為了取得position
                val target = paths!![position]
                //按下後得到檔名（可能要濾掉？）
                Log.d(
                    TAG,
                    "target:$target "
                )  //得到 /data/data/com.example.filemanagerlesson/files/a.bin


                findNavController().navigate(R.id.mainFragment)   //先重建我再送資料更新
                // 它們是分工合作的嗎？
                //所以要延遲嗎？

                //取到最後的檔名
                val size = target.length
                var find_the_position_of_the_last_slash = 0
                Log.d(TAG, "target[0]:${target[0]} ")
                for (i in 0..size - 1) {
                    Log.d(TAG, "target[i]:${target[i]} ")
                    if (target[i] == '/') {
                        find_the_position_of_the_last_slash = i
                    }
                }
                //找到ｊ是最後一個的索引值, 把資料取出放人viewmodel
                myViewModel.readFileName =
                    target.subSequence(find_the_position_of_the_last_slash + 1, size).toString()
                myViewModel.readFileEnabled = true
                myViewModel.readFileLiveData.value = myViewModel.readFileLiveData.value

          //      Log.d(TAG, " 我死了但我也會繼續工作")
           //     Log.d(TAG, " 繼續工作中")

/*
                if (target == MainActivity.Companion.ROOT) {
                    nowPath = paths!![position]
                    getFileDirectory(MainActivity.Companion.ROOT)
                    simpleAdapter!!.notifyDataSetChanged()
                } else if (target == MainActivity.Companion.PRE_LEVEL) {
                    nowPath = paths!![position]
                    getFileDirectory(File(nowPath).parent)
                    simpleAdapter!!.notifyDataSetChanged()
                } else {
                    val file = File(target)
                    if (file.canRead()) {
           /*             if (file.isDirectory) {
                            nowPath = paths!![position]
                            getFileDirectory(paths!![position])
                            simpleAdapter!!.notifyDataSetChanged()
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                R.string.is_not_directory,
                                Toast.LENGTH_SHORT
                            ).show()
                        }  */
                    } else {
                        Toast.makeText(this@MainActivity, R.string.can_not_read, Toast.LENGTH_SHORT)
                            .show()
                    }
                }  */
            }
        list_view.onItemLongClickListener =                //長按就是倏改/刪除
            AdapterView.OnItemLongClickListener { parent, view, position, id ->
                android.app.AlertDialog.Builder(requireContext())
                    .setItems(
                        ACTION,
                        DialogInterface.OnClickListener { dialog, which ->
                            val path = paths!![position]
                            when (which) {
                                0 -> rename(path)
                                1 -> delFile(path)
                            }
                        })
                    .show()
                true
            }
        //      createDir = findViewById(R.id.new_dir) as TextView
        //     createDir!!.setOnClickListener { addNewDir() }
    }   //initview end

    private fun rename(path: String) {
        val item = LayoutInflater.from(requireContext()).inflate(R.layout.itemlayout, null)
        android.app.AlertDialog.Builder(requireContext())      //顯示對話框
            .setTitle(R.string.input_you_rename)
            .setView(item)
            .setPositiveButton(R.string.ok,
                DialogInterface.OnClickListener { dialog, which ->
                    val editText = item.findViewById<View>(R.id.edit_text) as EditText
                    if (editText.getText().toString() == "") {
                        Toast.makeText(
                            requireContext(),
                            R.string.input_dir_name,
                            Toast.LENGTH_SHORT
                        ).show()
                        return@OnClickListener
                    }
                    //nowPaht = /storage/emulated/0
                    //Fiel.separator = /  就是斜線分隔符號
                    //           val newPath = nowPath + File.separator + editText.text
                    val newPath = fixPath + File.separator + editText.text
                    Log.d(TAG, "File.separator:${File.separator} ")

                    Log.d(TAG, "newPath: ${newPath}")
                    //              val newPath = "/data/data/com.example.filemanagerlesson/files" +
                    Log.d(TAG, "path: $path")   //原被按的路徑paht 改成新的路徑newpath
//  /data/data/com.example.filemanagerlesson/files/e  ->/data/data/com.example.filemanagerlesson/files/f
                    val f = File(path)
                    Log.d(TAG, "f: $f")
                    if (f.renameTo(File(newPath))) {     //f原來的
                        Toast.makeText(
                            requireContext(),
                            R.string.modify_success,
                            Toast.LENGTH_SHORT
                        ).show()

//                        getFileDirectory(nowPath)
                        getFileDirectory(fixPath)
                        simpleAdapter!!.notifyDataSetChanged()
                    } else {
                        Toast.makeText(requireContext(), R.string.modify_fail, Toast.LENGTH_SHORT)
                            .show()
                    }
                })
            .show()
    }

    private fun delFile(path: String) {
        android.app.AlertDialog.Builder(requireContext())          //顯示對話框
            .setTitle(R.string.make_sure_del)
            .setNegativeButton(R.string.ok,
                DialogInterface.OnClickListener { dialog, which ->
                    val file = File(path)
                    if (file.exists()) {
                        if (file.delete()) {
                            Toast.makeText(
                                requireContext(),
                                R.string.del_success,
                                Toast.LENGTH_SHORT
                            ).show()
                            //                          getFileDirectory(nowPath)
                            //Refresh 重刷一次目錄確定可刪除
                            getFileDirectory("/data/data/com.example.filemanagerlesson/files")
                            simpleAdapter!!.notifyDataSetChanged()
                        } else {
                            Toast.makeText(requireContext(), R.string.del_fail, Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            R.string.file_is_not_exist,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }).setPositiveButton(R.string.cancel,
                DialogInterface.OnClickListener { dialog, which -> })
            .show()
    }

    private fun addNewDir() {
        val item = LayoutInflater.from(requireContext()).inflate(R.layout.itemlayout, null)
        android.app.AlertDialog.Builder(requireContext())   //顯示對話框
            .setTitle(R.string.input_dir_name)
            .setView(item)
            .setPositiveButton(R.string.ok,
                DialogInterface.OnClickListener { dialog, which ->
                    val editText = item.findViewById<View>(R.id.edit_text) as EditText
                    if (editText.getText().toString() == "") {            //判斷它為空
                        Toast.makeText(
                            requireContext(),
                            R.string.input_dir_name,
                            Toast.LENGTH_SHORT
                        ).show()
                        return@OnClickListener
                    }
                    val filePath = nowPath + File.separator + editText.text.toString()
                    val f = File(filePath)
                    if (f.mkdir()) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.create_dir_success) + filePath,
                            Toast.LENGTH_SHORT
                        ).show()
                        getFileDirectory(nowPath)
                        simpleAdapter!!.notifyDataSetChanged()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            R.string.create_dir_fail,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            .show()
    }

    /*  固定路徑
    /data/data/com.example.filemanagerlesson
     */
    private fun initData() {
        nowPath = ListFragment.Companion.ROOT
        Log.d(TAG, "nowPath: $nowPath")
        filesList = ArrayList()         //item = HashMap
        names = ArrayList()
        paths = ArrayList()

        //    getFileDirectory(MainActivity.Companion.ROOT)
        //       getFileDirectory(ListFragment.Companion.ROOT)
        //  "/data/data/com.example.filemanagerlesson/files"
        getFileDirectory(fixPath)

    }

    private fun getFileDirectory(path: String?) {
        filesList!!.clear()
        paths!!.clear()
        if (path != ListFragment.Companion.ROOT) {   //不等於根目錄（我符合條件）
            //回根目錄  (顯示根目錄名稱）
/*           filesMap = HashMap()   //item
            names!!.add(MainActivity.Companion.ROOT)
            paths!!.add(MainActivity.Companion.FIRST_ITEM, MainActivity.Companion.ROOT)
            filesMap[IMG_ITEM] = fileImg[0]
           filesMap[NAME_ITEM] = MainActivity.Companion.ROOT
            filesList!!.add(filesMap) */
            //回上一層 （顯示..回上一層)
            /*          filesMap = HashMap()
                      names!!.add(MainActivity.Companion.PRE_LEVEL)
                      paths!!.add(MainActivity.Companion.SECOND_ITEM, File(path).parent)
                       filesMap [IMG_ITEM] = fileImg[0]
                      filesMap [NAME_ITEM] = MainActivity.Companion.PRE_LEVEL
                      filesList!!.add(filesMap) */
        }
        //列出目錄及檔案
        Log.d(TAG, "path: $path")
        /*
        /storage/emulated/0
        /data/data/com.example.filemanagerlesson/files
        */

        files = File(path).listFiles()
        Log.d(TAG, "files: $files")
        if (files != null) {
            for (i in files!!.indices) {
                filesMap = HashMap()
                names!!.add(files!![i].name)
                paths!!.add(files!![i].path)
                if (files!![i].isDirectory) filesMap[IMG_ITEM] =
                    fileImg[0] else filesMap[IMG_ITEM] = fileImg[1]
                filesMap[NAME_ITEM] = files!![i].name
                filesList!!.add(filesMap)
            }
        }
    }

    /*==============================   讀檔案 ==================================
  * 注意：是否會讀到空的值造成當機
  *  有困難目前無法處理只能在Main去處理
   */
    fun readDataFromFile3() {
        GlobalScope.launch {
            val fileName = "my_data"
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
                //還原資料
        //        Log.d(TAG, "jsonData3.wifiVersion: ${jsonData3.wifiVersion}")
                myViewModel
                //    val item1 = LayoutInflater.from().inflate(R.layout.fragment_main,null)
                //    Log.d(TAG, "item: $item1")
                //         val tv1 = requireActivity().findViewById(R.id.textViewWifiVersion) as TextView
                //   Log.d(TAG, "tv1: $tv1")
                //  textWifi.text = "12345678"
                Log.d(TAG, "textwifi: $textWifi")
                //         textViewWifiVersion.text = jsonData3.wifiVersion
                //        Log.d(TAG, "textViewWifiVersion.text: ${item1.textViewWifiVersion.text}")

                //       textViewMacAddress.text = jsonData3.macAddress

                //    spinnerBandSwitch2_4G.setSelection(jsonData3.bandSwitch2_4G)
                //           editText2_4G.setText(jsonData3.ssid_2_4G)
                /*      editTextPassword_2_4G.setText(jsonData3.password_2_4G)
                  //    spinnerBandSwitch5G.setSelection(jsonData3.bandSwitch_5G)
                      editText5G.setText(jsonData3.ssid_5GString)
                      editTextPassword_5G.setText(jsonData3.password_5G)
                  //   spinnerEapMethod.setSelection(jsonData3.eapMethod)
                   //   spinnerEapInnerMethod.setSelection(jsonData3.eapInnerMethod)
                      editTextEapUserId.setText(jsonData3.eapUserId)
                      editTextEapUserPassword.setText(jsonData3.eapUserPassword)
                      editTextSgIp.setText(jsonData3.sgIP)
                      textViewSerialNumber.text = jsonData3.serialNumber  */


            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                Log.d(TAG, "readFile3: 檔案不存在2 ")
                // 設初值
            }
        }

    }


}