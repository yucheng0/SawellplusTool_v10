package com.example.bytearraylesson.Send

import android.util.Log

open class SendDataToDevice {
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


    //
    init {
        wifiVersion.add(0x31)                             // 這部分會由轉板收到
        wifiVersion.add(0x37)
        wifiVersion.add(0x38)

        macAddress.add(0x88)                                //這部分會由轉板收到
        macAddress.add(0xda)
        macAddress.add(0x1a)
        macAddress.add(0xf9)
        macAddress.add(0x57)
        macAddress.add(0x68)

        //SportsArt_10   fix 12byte
        ssid_2_4G.add('S'.toInt())
        ssid_2_4G.add('p'.toInt())
        ssid_2_4G.add('o'.toInt())
        ssid_2_4G.add('r'.toInt())
        ssid_2_4G.add('t'.toInt())
        ssid_2_4G.add('s'.toInt())
        ssid_2_4G.add('A'.toInt())
        ssid_2_4G.add('r'.toInt())
        ssid_2_4G.add('t'.toInt())
        ssid_2_4G.add('_'.toInt())
        ssid_2_4G.add('1'.toInt())
        ssid_2_4G.add('0'.toInt())
//sportsart063840888  fix:  18byte
        password_2_4G.add('s'.toInt())
        password_2_4G.add('p'.toInt())
        password_2_4G.add('o'.toInt())
        password_2_4G.add('r'.toInt())
        password_2_4G.add('t'.toInt())
        password_2_4G.add('s'.toInt())
        password_2_4G.add('a'.toInt())
        password_2_4G.add('r'.toInt())
        password_2_4G.add('t'.toInt())
        password_2_4G.add('0'.toInt())
        password_2_4G.add('6'.toInt())
        password_2_4G.add('3'.toInt())
        password_2_4G.add('8'.toInt())
        password_2_4G.add('4'.toInt())
        password_2_4G.add('0'.toInt())
        password_2_4G.add('8'.toInt())
        password_2_4G.add('8'.toInt())
        password_2_4G.add('8'.toInt())
//SportArt_10_5G     fix = 15byte
        ssid_5G.add('S'.toInt())
        ssid_5G.add('p'.toInt())
        ssid_5G.add('o'.toInt())
        ssid_5G.add('r'.toInt())
        ssid_5G.add('t'.toInt())
        ssid_5G.add('s'.toInt())
        ssid_5G.add('A'.toInt())
        ssid_5G.add('r'.toInt())
        ssid_5G.add('t'.toInt())
        ssid_5G.add('_'.toInt())
        ssid_5G.add('1'.toInt())
        ssid_5G.add('0'.toInt())
        ssid_5G.add('_'.toInt())
        ssid_5G.add('5'.toInt())
        ssid_5G.add('G'.toInt())
//00000000     fix:8byte
        password_5G.add('0'.toInt())
        password_5G.add('0'.toInt())
        password_5G.add('0'.toInt())
        password_5G.add('0'.toInt())
        password_5G.add('0'.toInt())
        password_5G.add('0'.toInt())
        password_5G.add('0'.toInt())
        password_5G.add('0'.toInt())

// //   var eapUserId = "SportsArtEAP"   fix:12byte
        eapUserId.add('S'.toInt())
        eapUserId.add('p'.toInt())
        eapUserId.add('o'.toInt())
        eapUserId.add('r'.toInt())
        eapUserId.add('t'.toInt())
        eapUserId.add('s'.toInt())
        eapUserId.add('A'.toInt())
        eapUserId.add('r'.toInt())
        eapUserId.add('t'.toInt())
        eapUserId.add('E'.toInt())
        eapUserId.add('A'.toInt())
        eapUserId.add('P'.toInt())

//  var eapUserPassword = "sa.eap063840888"     fix:15byte
        eapUserPassword.add('s'.toInt())
        eapUserPassword.add('a'.toInt())
        eapUserPassword.add('.'.toInt())
        eapUserPassword.add('e'.toInt())
        eapUserPassword.add('a'.toInt())
        eapUserPassword.add('p'.toInt())
        eapUserPassword.add('0'.toInt())
        eapUserPassword.add('6'.toInt())
        eapUserPassword.add('3'.toInt())
        eapUserPassword.add('8'.toInt())
        eapUserPassword.add('4'.toInt())
        eapUserPassword.add('0'.toInt())
        eapUserPassword.add('8'.toInt())
        eapUserPassword.add('8'.toInt())
        eapUserPassword.add('8'.toInt())

        sgIP.add(0xc0)
        sgIP.add(0xa8)
        sgIP.add(0x00)
        sgIP.add(0x0e)
        serialNumber.add(0x31)
        serialNumber.add(0x32)
        serialNumber.add(0x33)
        serialNumber.add(0x34)
        serialNumber.add(0x35)
        serialNumber.add(0x36)
        serialNumber.add(0x37)
    }

    companion object {
        var clientCs = 0
    }


    fun getReadySendData():ByteArray {             //當你的資料備好時, 我只負責送不負責計算？ cs是否例外
        val dataIntArray = ArrayList<Int>()
        dataIntArray.add(commandHead)
        dataIntArray.add(protocol)
        dataIntArray.add(protocolLength)                       //     dataIntArray.add()      //Protol Length
        dataIntArray.add(operation)
        dataIntArray.add(operationLength)   // Operation Lenght
        dataIntArray.add(sendType)   // Sender = Client

        for (i in wifiVersion) {                   // 字元 Ascii 轉數字
            dataIntArray.add(i.toInt())
        }

        for (i in macAddress) {                   // 字元 Ascii 轉數字
            dataIntArray.add(i.toInt())
        }

        dataIntArray.add(bandSwitch2_4G)

        dataIntArray.add(ssid_2_4GLength)

        for (i in ssid_2_4G) {                          // 字元 Ascii 轉數字
            dataIntArray.add(i.toInt())
            Log.d(com.example.bytearraylesson.TAG, "dataIntArray:${dataIntArray}")
        }

        dataIntArray.add(password_2_4GLength)

        for (i in password_2_4G) {                          // 字元 Ascii 轉數字
            dataIntArray.add(i.toInt())
        }

        dataIntArray.add(bandSwitch_5G)
        dataIntArray.add(ssid_5GLength)

        for (i in ssid_5G) {                          // 字元 Ascii 轉數字
            dataIntArray.add(i.toInt())
        }

        dataIntArray.add(password_5GLength)

        for (i in password_5G) {                          // 字元 Ascii 轉數字
            dataIntArray.add(i.toInt())
        }

        dataIntArray.add(eapMethod)
        dataIntArray.add(eapInnerMethod)

        dataIntArray.add(eapUserIdLength)

        for (i in eapUserId) {                          // 字元 Ascii 轉數字
            dataIntArray.add(i.toInt())
        }

        dataIntArray.add(eapUserPasswordLength)

        for (i in eapUserPassword) {                          // 字元 Ascii 轉數字
            dataIntArray.add(i.toInt())
        }

        for (i in sgIP) {                   // 字元 Ascii 轉數字
            dataIntArray.add(i.toInt())
        }

        for (i in serialNumber) {                   // 字元 Ascii 轉數字
            dataIntArray.add(i.toInt())
        }

//cs 是算出來的
        cs = 0
        for (i in 1..dataIntArray.size - 1) {            // 從1是去頭55, 去尾90再算裡面
            cs = cs + dataIntArray[i]
        }
        Log.d(com.example.bytearraylesson.TAG, "原cs: ${cs}")

        var css = cs.toString(16)
        Log.d(com.example.bytearraylesson.TAG, "css: ${css}")                           // 16f
        var cs1 = css.subSequence((css.length) - 2, (css.length))   //6f 取後面2位數
        cs = cs1.toString().toInt(16)
        clientCs = cs                      // 存起來
        Log.d(com.example.bytearraylesson.TAG, "果cs: ${cs}")

        dataIntArray.add(cs)

        dataIntArray.add(commandEnd)
        //
        val dataByteArray = ByteArray(dataIntArray.size)
        for (i in 0..dataIntArray.size - 1) {
            dataByteArray[i] = dataIntArray[i].toByte()
            //           Log.d(TAG, "dataByteArray[i]: ${dataByteArray[i]} ")
        }

        Log.d(com.example.bytearraylesson.TAG, "dataIntArray: ${dataIntArray}")

        // test 測試, 轉成16進制
        val dataIntArrayHex = ArrayList<String>()
        val x = dataIntArray[0].toString(16)
        //      Log.d(TAG, "tcpWifisendx: $x")
        for (i in 0..dataByteArray.size - 1) {
            dataIntArrayHex.add(dataIntArray[i].toString(16))
            //         Log.d(TAG, "Hex:${dataIntArrayHex[i]}")
        }
        Log.d(com.example.bytearraylesson.TAG, "dataIntArrayHex: ${dataIntArrayHex}")

        return dataByteArray
    }
    }
