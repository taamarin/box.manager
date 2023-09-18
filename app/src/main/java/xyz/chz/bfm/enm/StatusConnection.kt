package xyz.chz.bfm.enm

enum class StatusConnection(val str: String) {
    Enabled("Enabled"),
    Error("Error"),
    Disabled("Disabled"),
    Loading("Loading"),
    Unknown("Unknown")
}