package com.example.bytearraylesson.Parser

import android.util.Log
import com.example.bytearraylesson.TAG

class ResponseSendDataRequest {
    var commandHead = 0x55            //固定
    var protocol = 0xFE               //固定
    var protocolLength = 0x71
    var operation = 0xfe              //固定
    var operationLength = 0x6F        // Protocollength -2
    var sendType = 1                  //固定
    var wifiVersion = ArrayList<Int>()
    var macAddress = ArrayList<Int>()
    var bandSwitch2_4G = 0x01                         // Enabled
    var ssid_2_4GLength = 0x0c
    var ssid_2_4G = ArrayList<Int>()
    var password_2_4GLength = 0x12
    var password_2_4G = ArrayList<Int>()
    var bandSwitch_5G = 0x01
    var ssid_5GLength = 0x0f
    var ssid_5G = ArrayList<Int>()
    var password_5GLength = 0x08
    var password_5G = ArrayList<Int>()
    var eapMethod = 0
    var eapInnerMethod = 0
    var eapUserIdLength = 0x0c
    var eapUserId = ArrayList<Int>()
    var eapUserPasswordLength = 0x0f
    var eapUserPassword = ArrayList<Int>()
    var sgIP = ArrayList<Int>()   //資料從低byte送
    var serialNumber = ArrayList<Int>()
    var cs = 0x71
    val commandEnd = 0x90

    var rDataByteArray = ByteArray(1024)    // 收到的資料, 它會告訴我幾筆鎮

    init {
        //  55 FE 71 FE 6F 00 31 37 38 88
        rDataByteArray.set(0, 0x55)
        rDataByteArray.set(1, 0xfe - 256)
        rDataByteArray.set(2, 0x71)
        rDataByteArray.set(3, 0xfe - 256)
        rDataByteArray.set(4, 0x6f)
        rDataByteArray.set(5, 0x00)
        rDataByteArray.set(6, 0x31)
        rDataByteArray.set(7, 0x37)
        rDataByteArray.set(8, 0x38)
        rDataByteArray.set(9, 0x88 - 256)
        //DA 1A F9 57 68 01 0C 53 70 6f
        rDataByteArray.set(10, 0xda - 256)
        rDataByteArray.set(11, 0x1a)          //test = 0x1a
        rDataByteArray.set(12, 0xf9 - 256)
        rDataByteArray.set(13, 0x57)
        rDataByteArray.set(14, 0x68)
        rDataByteArray.set(15, 0x01)
        rDataByteArray.set(16, 0x0c)
        rDataByteArray.set(17, 0x53)
        rDataByteArray.set(18, 0x70)
        rDataByteArray.set(19, 0x6f)
        //72 74 73 41 72 74 5f 31 30 12
        rDataByteArray.set(20, 0x72)
        rDataByteArray.set(21, 0x74)
        rDataByteArray.set(22, 0x73)
        rDataByteArray.set(23, 0x41)
        rDataByteArray.set(24, 0x72)
        rDataByteArray.set(25, 0x74)
        rDataByteArray.set(26, 0x5f)
        rDataByteArray.set(27, 0x31)
        rDataByteArray.set(28, 0x30)     //?
        rDataByteArray.set(29, 0x12)
        //  73 70 6f 72 74 73 61 72 74 30
        rDataByteArray.set(30, 0x73)
        rDataByteArray.set(31, 0x70)
        rDataByteArray.set(32, 0x6f)
        rDataByteArray.set(33, 0x72)
        rDataByteArray.set(34, 0x74)
        rDataByteArray.set(35, 0x73)
        rDataByteArray.set(36, 0x61)
        rDataByteArray.set(37, 0x72)
        rDataByteArray.set(38, 0x74)
        rDataByteArray.set(39, 0x30)
        //      36 33 38 34 30 38 38 38 01 0F
        rDataByteArray.set(40, 0x36)
        rDataByteArray.set(41, 0x33)
        rDataByteArray.set(42, 0x38)
        rDataByteArray.set(43, 0x34)
        rDataByteArray.set(44, 0x30)
        rDataByteArray.set(45, 0x38)
        rDataByteArray.set(46, 0x38)
        rDataByteArray.set(47, 0x38)   //?
        rDataByteArray.set(48, 0x01)
        rDataByteArray.set(49, 0x0f)
//53 70 6f 72 74 73 41 72 74 5f
        rDataByteArray.set(50, 0x53)
        rDataByteArray.set(51, 0x70)
        rDataByteArray.set(52, 0x6f)
        rDataByteArray.set(53, 0x72)
        rDataByteArray.set(54, 0x74)
        rDataByteArray.set(55, 0x73)
        rDataByteArray.set(56, 0x41)
        rDataByteArray.set(57, 0x72)
        rDataByteArray.set(58, 0x74)
        rDataByteArray.set(59, 0x5f)
//   31 30 5f 35 47 08 30 30 30 30
        rDataByteArray.set(60, 0x31)
        rDataByteArray.set(61, 0x30)
        rDataByteArray.set(62, 0x5f)
        rDataByteArray.set(63, 0x35)
        rDataByteArray.set(64, 0x47)
        rDataByteArray.set(65, 0x08)
        rDataByteArray.set(66, 0x30)
        rDataByteArray.set(67, 0x30)
        rDataByteArray.set(68, 0x30)
        rDataByteArray.set(69, 0x30)
//30 30 30 30 00 00 0C 53 70 6f
        rDataByteArray.set(70, 0x30)
        rDataByteArray.set(71, 0x30)
        rDataByteArray.set(72, 0x30)
        rDataByteArray.set(73, 0x30)
        rDataByteArray.set(74, 0x00)
        rDataByteArray.set(75, 0x00)
        rDataByteArray.set(76, 0x0c)
        rDataByteArray.set(77, 0x53)
        rDataByteArray.set(78, 0x70)
        rDataByteArray.set(79, 0x6f)
//72 74 73 41 72 74 45 41 50 0F
        rDataByteArray.set(80, 0x72)
        rDataByteArray.set(81, 0x74)
        rDataByteArray.set(82, 0x73)
        rDataByteArray.set(83, 0x41)
        rDataByteArray.set(84, 0x72)
        rDataByteArray.set(85, 0x74)
        rDataByteArray.set(86, 0x45)
        rDataByteArray.set(87, 0x41)
        rDataByteArray.set(88, 0x50)
        rDataByteArray.set(89, 0x0f)

        //       73 61 2e 65 61 70 30 36 33 38
        rDataByteArray.set(90, 0x73)
        rDataByteArray.set(91, 0x61)
        rDataByteArray.set(92, 0x2e)
        rDataByteArray.set(93, 0x65)
        rDataByteArray.set(94, 0x61)
        rDataByteArray.set(95, 0x70)
        rDataByteArray.set(96, 0x30)
        rDataByteArray.set(97, 0x36)
        rDataByteArray.set(98, 0x33)
        rDataByteArray.set(99, 0x38)
        //     34 30 38 38 38 C0 A8 00 0E 31  （SGip)
        rDataByteArray.set(100, 0x34)
        rDataByteArray.set(101, 0x30)
        rDataByteArray.set(102, 0x38)
        rDataByteArray.set(103, 0x38)
        rDataByteArray.set(104, 0x38)
        rDataByteArray.set(105, 0xc0 - 256)
        rDataByteArray.set(106, 0xa8 - 256)       //sgip
        rDataByteArray.set(107, 0x00)             //sgip
        rDataByteArray.set(108, 0x0e)           //sgip
        rDataByteArray.set(109, 0x31)           //sgip
        //   32 33 34 35 36 37 8F 90   (序號）
        rDataByteArray.set(110, 0x32)
        rDataByteArray.set(111, 0x33)
        rDataByteArray.set(112, 0x34)
        rDataByteArray.set(113, 0x35)
        rDataByteArray.set(114, 0x36)
        rDataByteArray.set(115, 0x37)
        rDataByteArray.set(116, 0x8f - 256)
        rDataByteArray.set(117, 0x90 - 256)
    }

    val protocolFilterValue = 0xfe              //  寫固定
    val operationFilterValue = 0xfe             //  寫固定
    //   val size = 118                         // 模擬值


    /*     接收
 Ex: totol 118byte, 71hex = 113 , 113+1(本身）+1（55)+1(fe)+1(cs)+1(end)
55 FE 71 FE 6F 00 31 37 38 88
DA 1A F9 57 68 01 0C 53 70 6f
72 74 73 41 72 74 5f 31 30 12
73 70 6f 72 74 73 61 72 74 30
36 33 38 34 30 38 38 38 01 0F
53 70 6f 72 74 73 41 72 74 5f
31 30 5f 35 47 08 30 30 30 30
30 30 30 30 00 00 0C 53 70 6f
72 74 73 41 72 74 45 41 50 0F
73 61 2e 65 61 70 30 36 33 38
34 30 38 38 38 C0 A8 00 0E 31
32 33 34 35 36 37 8F 90
*/

    fun parser(
        //       rDataByteArray: ByteArray,              // 傳入接收的位元陣列   （可以由接收端知道）
        size: Int,                             // 接收到的資料總byte （可以由接收端知道）測試值118
        //       minSizeLimit: Int                     // 最小可以接受的byte(先判斷）有8,9,34
//        protocolFilterValue: Int,              // 協定命令值目前都是Fe
        //       operationFilterValue: Int              //　操作元命令值目前有ff,fe,fd, fc
    ): Boolean {                               // 回傳布林值, ture表示成功 , false表示失敗
        val minSizeLimit = 34                   // 固定大小是34 (最小）
        var retureState = false
        val rDataIntArray = ArrayList<Int>()
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
                        if (rDataIntArray[x + rDataIntArray[x + 2] + 5 - 1] == 0x90) {   //test ==
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
                            if (k == rDataIntArray[x + rDataIntArray[x + 2] + 5 - 2]) {   //test ==

                                //過濾成功所有動作流程寫在此
                                val b1 = rDataIntArray.listIterator(x + 6)
                                //  sendType = rDataIntArray[x+5]
                                wifiVersion.clear()
                                for (i in 0..2) {               // Wifi version
                                    wifiVersion.add(b1.next())
                                }

                                Log.d(TAG, "wifiVersion: ${wifiVersion}")
                                macAddress.clear()
                                for (i in 0..5) {               //MAC Adadres
                                    macAddress.add(b1.next())
                                }
                                //                                                  }
                                Log.d(TAG, "macAddress : ${macAddress} ")
                                bandSwitch2_4G = b1.next()
                                ssid_2_4GLength = b1.next()
                                ssid_2_4G.clear()
                                for (i in 0..ssid_2_4GLength - 1) {
                                    ssid_2_4G.add(b1.next())
                                }
                                Log.d(TAG, "ssid_2_4G: $ssid_2_4G")

                                password_2_4GLength = b1.next()
                                Log.d(TAG, "password_2_4GLength: $password_2_4GLength")
                                password_2_4G.clear()
                                for (i in 0..password_2_4GLength - 1) {
                                    password_2_4G.add(b1.next())
                                }
                                Log.d(TAG, "password_2_4G: $password_2_4G")

                                bandSwitch_5G = b1.next()
                                ssid_5GLength = b1.next()
                                ssid_5G.clear()
                                for (i in 0..ssid_5GLength - 1) {
                                    ssid_5G.add(b1.next())
                                }
                                Log.d(TAG, "ssid_5G: $ssid_5G")

                                password_5GLength = b1.next()
                                password_5G.clear()
                                for (i in 0..password_5GLength - 1) {
                                    password_5G.add(b1.next())
                                }
                                Log.d(TAG, "password_5G: $password_5G")

                                eapMethod = b1.next()
                                eapInnerMethod = b1.next()
                                eapUserIdLength = b1.next()
                                eapUserId.clear()
                                for (i in 0..eapUserIdLength - 1) {
                                    eapUserId.add(b1.next())
                                }
                                Log.d(TAG, "eapUserId: $eapUserId")

                                eapUserPasswordLength = b1.next()
                                eapUserPassword.clear()
                                for (i in 0..eapUserPasswordLength - 1) {
                                    eapUserPassword.add(b1.next())
                                }
                                Log.d(TAG, "eapUserPassword: $eapUserPassword")

                                for (i in 0..3) {
                                    sgIP.add(b1.next())
                                }
                                Log.d(TAG, "sgIP: $sgIP")

                                for (i in 0..6) {
                                    serialNumber.add(b1.next())
                                }
                                Log.d(TAG, "serialNumber: $serialNumber")

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











