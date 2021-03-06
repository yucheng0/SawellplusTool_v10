package com.example.bytearraylesson

import android.util.Log
import com.example.sawellplustool.QRcodeActivity

class ResponseConnectionStatus {
    var rDataByteArray = ByteArray(1024)    // 收到的資料, 它會告訴我幾筆

    init {
        rDataByteArray.set(0, 0x55)
        rDataByteArray.set(1, 0xfe - 256)
        rDataByteArray.set(2, 0x04)         //04
        rDataByteArray.set(3, 0xff - 256)    //ff - 256
        rDataByteArray.set(4, 0x02)          // 02
        rDataByteArray.set(5, 0x00)
        rDataByteArray.set(6, 0x01)
        rDataByteArray.set(7, 0x04)          //04
        rDataByteArray.set(8, 0x90 - 256)
    }

    val protocolFilterValue = 0xfe              //  寫固定
    val operationFilterValue = 0xff             //  寫固定


    /*
    * 只能說資料在rDataByteArray內, 而且55是第1個出現的去直接取出值來
    * 取出之後就把它移掉
     */
 fun parseCheck():Boolean                   // 內定使用myViewModel.tcpReceivedData
    {
        val minSizeLimit = 9                   // 固定大小是9
        var retureState = false 
        val size = QRcodeActivity.myViewModel.tcpReceivedData.size
        //--------基本過濾法  --------------
        if (QRcodeActivity.myViewModel.tcpReceivedData.contains(0x55) && QRcodeActivity.myViewModel.tcpReceivedData.contains(protocolFilterValue) && QRcodeActivity.myViewModel.tcpReceivedData.contains(
                operationFilterValue
            ) && QRcodeActivity.myViewModel.tcpReceivedData.contains(0x90) && size >= minSizeLimit
        ) {
            val x = QRcodeActivity.myViewModel.tcpReceivedData.indexOf(0x55)   // 取到初值索引值
            if (QRcodeActivity.myViewModel.tcpReceivedData[x + 1] == protocolFilterValue && QRcodeActivity.myViewModel.tcpReceivedData[x + 3] == operationFilterValue) {
                if (x + QRcodeActivity.myViewModel.tcpReceivedData[x + 2] + 5 <= size) {
                    if (QRcodeActivity.myViewModel.tcpReceivedData[x + 4] == QRcodeActivity.myViewModel.tcpReceivedData[x + 2] - 2) {
                        if (QRcodeActivity.myViewModel.tcpReceivedData[x + QRcodeActivity.myViewModel.tcpReceivedData[x + 2] + 5 - 1] == 0x90) {
                            //  計算cs值
                            var y = 0
                            for (i in x + 1..QRcodeActivity.myViewModel.tcpReceivedData[x + 2] + 2) {
                                y = y + QRcodeActivity.myViewModel.tcpReceivedData[i]
                                //   Log.d(TAG, "i,$i: ${QRcodeActivity.myViewModel.tcpReceivedData[i]} = , y: $y")
                            }
                            //取16進制最後2byte值
                            var z = y.toString(16)
                            var k = z.subSequence(z.length - 2, z.length).toString().toInt(16)
                            //開始判斷cs 是否正確(範例90)
                            if (k == QRcodeActivity.myViewModel.tcpReceivedData[x + QRcodeActivity.myViewModel.tcpReceivedData[x + 2] + 5 - 2]) {
                                //                     Log.d(TAG, "k: $k")
// 解析成功後之處理流程在此

                                //要刪掉解析過的資料
                                Log.d(TAG, "x: $x")
                                Log.d(TAG, "x+minSizeLimit-1: ${x+minSizeLimit-1}")
                               for (i in x..x+minSizeLimit-1) {
                                QRcodeActivity.myViewModel.tcpReceivedData.removeAt(x)
                                }

                                Log.d(TAG, "myViewModel.tcpReceivedData: ${ QRcodeActivity.myViewModel.tcpReceivedData}")

                                Log.d(TAG, "檢查方法全通過")
                                retureState = true


                            } else {
                                Log.d(TAG, "錯誤：檢查碼有誤")
                            }
                        } else {
                            Log.d(TAG, "錯誤：結束碼 90有誤")
                        }

                    } else {
                        Log.d(TAG, "錯誤：協定長度  & 運算長度不合")
                    }
                } else {
                    Log.d(TAG, "錯誤:長度太短")
                }
            } else {
                Log.d(TAG, "錯誤：協定命令 & 運算命令不合 ")
            }
        } else {                                            //ok
            Log.d(TAG, "過濾基本規格不合跳開")
        }
        return retureState     
        
    }
    
  
    
    
    
    
    
    fun tcpWifiReceiverParserCheck(
        rDataByteArray: ByteArray,              // 傳入接收的位元陣列   （可以由接收端知道）
        size: Int                             // 接收到的資料總byte （可以由接收端知道）
        //       minSizeLimit: Int                     // 最小可以接受的byte(先判斷）有8,9,34
//        protocolFilterValue: Int,              // 協定命令值目前都是Fe
        //       operationFilterValue: Int              //　操作元命令值目前有ff,fe,fd, fc
    ): Boolean {                               // 回傳布林值, ture表示成功 , false表示失敗
        val minSizeLimit = 9                   // 固定大小是9
        var retureState = false
    //    val QRcodeActivity.myViewModel.tcpReceivedData = ArrayList<Int>()
        Log.d(TAG, "rDataByteArray:${rDataByteArray} ")
        // Byte -> Int  將位元陣列轉成整數陣列方便運算用
        for (i in 0..size - 1) {                  // 一定先知道nubBytes的數字再去讀
            if (rDataByteArray[i].toInt() >= 0) {
                QRcodeActivity.myViewModel.tcpReceivedData.add(rDataByteArray[i].toString().toInt())
            } else {                            //byte值有負數必須先處理到正數
                QRcodeActivity.myViewModel.tcpReceivedData.add(256 + rDataByteArray[i].toInt())
            }
        }
        //--------基本過濾法  --------------
        if (QRcodeActivity.myViewModel.tcpReceivedData.contains(0x55) && QRcodeActivity.myViewModel.tcpReceivedData.contains(protocolFilterValue) && QRcodeActivity.myViewModel.tcpReceivedData.contains(
                operationFilterValue
            ) && QRcodeActivity.myViewModel.tcpReceivedData.contains(0x90) && size >= minSizeLimit
        ) {
            val x = QRcodeActivity.myViewModel.tcpReceivedData.indexOf(0x55)   // 取到初值索引值
            if (QRcodeActivity.myViewModel.tcpReceivedData[x + 1] == protocolFilterValue && QRcodeActivity.myViewModel.tcpReceivedData[x + 3] == operationFilterValue) {
                if (x + QRcodeActivity.myViewModel.tcpReceivedData[x + 2] + 5 <= size) {
                    if (QRcodeActivity.myViewModel.tcpReceivedData[x + 4] == QRcodeActivity.myViewModel.tcpReceivedData[x + 2] - 2) {
                        if (QRcodeActivity.myViewModel.tcpReceivedData[x + QRcodeActivity.myViewModel.tcpReceivedData[x + 2] + 5 - 1] == 0x90) {
                            //  計算cs值
                            var y = 0
                            for (i in x + 1..QRcodeActivity.myViewModel.tcpReceivedData[x + 2] + 2) {
                                y = y + QRcodeActivity.myViewModel.tcpReceivedData[i]
                                //   Log.d(TAG, "i,$i: ${QRcodeActivity.myViewModel.tcpReceivedData[i]} = , y: $y")
                            }
                            //取16進制最後2byte值
                            var z = y.toString(16)
                            var k = z.subSequence(z.length - 2, z.length).toString().toInt(16)
                            //開始判斷cs 是否正確(範例90)
                            if (k == QRcodeActivity.myViewModel.tcpReceivedData[x + QRcodeActivity.myViewModel.tcpReceivedData[x + 2] + 5 - 2]) {
                                //                     Log.d(TAG, "k: $k")
// 解析成功後之處理流程在此

                                //要刪掉解析過的資料

                                for (i in x..minSizeLimit-1)
                                    rDataByteArray[i]



                                Log.d(TAG, "檢查方法全通過")
                                retureState = true


                            } else {
                                Log.d(TAG, "錯誤：檢查碼有誤")
                            }
                        } else {
                            Log.d(TAG, "錯誤：結束碼 90有誤")
                        }

                    } else {
                        Log.d(TAG, "錯誤：協定長度  & 運算長度不合")
                    }
                } else {
                    Log.d(TAG, "錯誤:長度太短")
                }
            } else {
                Log.d(TAG, "錯誤：協定命令 & 運算命令不合 ")
            }
        } else {                                            //ok
            Log.d(TAG, "過濾基本規格不合跳開")
        }
        return retureState
    }  // fun end


}