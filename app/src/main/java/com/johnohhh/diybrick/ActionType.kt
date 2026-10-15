package com.johnohhh.diybrick

enum class ActionType(val label: String) {
    OPEN_CAMERA("Open Camera"),
    TOGGLE_FLASHLIGHT("Toggle Flashlight"),
    BLUETOOTH_TOGGLE("Toggle Bluetooth"),
    WIFI_TOGGLE("Toggle WiFi"),
    HOTSPOT_TOGGLE("Toggle Hotspot"),
    DND_TOGGLE("Toggle Do Not Disturb"),
    LAUNCH_MAPS("Launch Maps"),
    LAUNCH_MUSIC("Launch Music"),
    SHARE_CONTACT("Share My Contact"),
    CUSTOM_TEXT("Custom Text Toast"),
    NONE("No Action")
}
