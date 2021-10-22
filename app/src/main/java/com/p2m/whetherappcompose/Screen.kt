package com.p2m.whetherappcompose

sealed class Screen(val route :String){
    object  EntranceScreen:Screen("entrance_screen")
    object  FlashScreen: Screen("flash_screen")
    object DataScreen: Screen("data_screen")
}
