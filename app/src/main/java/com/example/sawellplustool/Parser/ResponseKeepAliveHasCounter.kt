package com.example.bytearraylesson.Parser

import com.example.bytearraylesson.TAG

import android.util.Log
import com.example.bytearraylesson.Send.SendKeepAliveWithCounter

class ResponseKeepAliveHasCounter:SendKeepAliveWithCounter() {    //繼承它我就有counter了
    var rDataByteArray = ByteArray(1024)    // 收到的資料, 它會告訴我幾筆

    init {
        rDataByteArray.set(0, 0x55)
        rDataByteArray.set(1, 0xfe - 256)
        rDataByteArray.set(2, 0x04)
        rDataByteArray.set(3, 0xfd - 256)
        rDataByteArray.set(4, 0x02)
        rDataByteArray.set(5, 0x00)
        rDataByteArray.set(6, 0x00)              // counter 值會變
        rDataByteArray.set(7, 0x01)              //cs值被填死了, 所以可能發生錯誤
        rDataByteArray.set(8, 0x90 - 256)
    }

    val protocolFilterValue = 0xfe              //  寫固定
    val operationFilterValue = 0xfd             //  寫固定

//   val size = 9
    /*
    * 只能說資料在rDataByteArray內, 而且55是第1個出現的去直接取出值來
    * 取出之後就把它移掉
     */

    fun recevieFromDevice(
        //       rDataByteArray: ByteArray,       // 傳入接收的位元陣列   （可以由接收端知道）
        size: Int                                // 接收到的資料總byte （可以由接收端知道）測試值8

    ): Boolean {                               // 回傳布林值, ture表示成功 , false表示失敗
        val minSizeLimit = 9                    // 固定大小是9
        var retureState = false
        val rDataIntArray = ArrayList<Int>()

         Log.d(TAG, "Receive counter: $counter")

        // Byte -> Int  將位元陣列轉成整數陣列方便運算用
        Log.d(TAG, "rDataByteArray:${rDataByteArray} ")
        Log.d(TAG, "rDataByteArray:${rDataByteArray[0]} ")
        Log.d(TAG, "rDataByteArray:${rDataByteArray[1]} ")

        for (i in 0..size - 1) {                  // 一定先知道nubBytes的數字再去讀
            if (rDataByteArray[i].toInt() >= 0) {
                rDataIntArray.add(rDataByteArray[i].toString().toInt())

            } else {                            //byte值有負數必須先處理到正數
                rDataIntArray.add(256 + rDataByteArray[i].toInt())
            }
        }
        Log.d(TAG, "rDataIntArray:$rDataIntArray ")

        //--------基本過濾法  --------------
        if (rDataIntArray.contains(0x55) && rDataIntArray.contains(protocolFilterValue) && rDataIntArray.contains(
                operationFilterValue
            ) && rDataIntArray.contains(0x90) && size >= minSizeLimit
        ) {

            //       Log.d(TAG, "基本第一層過濾成功: ")
            val x = rDataIntArray.indexOf(0x55)   // 取到初值索引值
            if (rDataIntArray[x + 1] == protocolFilterValue && rDataIntArray[x + 3] == operationFilterValue) {
                if (x + rDataIntArray[x + 2] + 5 <= size) {
                    if (rDataIntArray[x + 4] == rDataIntArray[x + 2] - 2) {
                        // 判斷結束碼90
                        if (rDataIntArray[x + rDataIntArray[x + 2] + 5 - 1] == 0x90) {
                            //  計算cs值
                            var y = 0
                            for (i in x + 1..rDataIntArray[x + 2] + 2) {
                                y = y + rDataIntArray[i]
                                //   Log.d(TAG, "i,$i: ${rDataIntArray[i]} = , y: $y")
                            }
                            //取16進制最後2byte值
                            var z = y.toString(16)
                            var k = z.subSequence(z.length - 2, z.length).toString().toInt(16)
                            //開始判斷cs 是否正確(範例90)
                            if (k == rDataIntArray[x + rDataIntArray[x + 2] + 5 - 2]) {
                                //判斷clientcs 是否正確
                                if (rDataIntArray[x + 6] == counter-1) {
                                    //                     Log.d(TAG, "k: $k")
// 解析成功後之處理流程在此
                                    Log.d(TAG, "檢查方法全通過")
                                    retureState = true
                                } else {
                                    Log.d(TAG, "錯誤：回復上次的檢查碼有誤")
                                }
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