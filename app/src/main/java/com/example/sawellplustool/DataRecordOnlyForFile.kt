package com.example.sawellplustool

data class DataRecordOnlyForFile(                                  // 14byte
  // var wifiVersion: String,                      // 從程式取得, 不能還原，所以不用記
   //var macAddress: String,                       // 從程式取得, 不能還原，所以不用記
    // 應該記錄的是使用者的輸入
   var bandSwitch2_4G: Int,                        // Enabled
    var ssid_2_4G: String,
    var password_2_4G: String,
    var bandSwitch_5G: Int,
    var ssid_5GString: String,
    var password_5G: String,
    var eapMethod: Int,
    var eapInnerMethod: Int,
    var eapUserId: String,
    var eapUserPassword: String,
    var sgIPSwitch: Int,
    var sgIP1: String,   //資料從低byte送
    var sgIP2: String,
    var sgIP3: String,
   var sgIP4: String

 //   var serialNumber: String,                       // 從程式取得, 不能還原，所以不用記
) {

}