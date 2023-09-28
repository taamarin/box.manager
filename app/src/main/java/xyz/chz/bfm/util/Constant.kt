package xyz.chz.bfm.util

// bcs yq yaml cant get emojis
// dont make this to regex
const val EXTRACTOR =
    "sed 's/\\[//g' | sed 's/\\]//g' | sed 's/\"//g' | sed 's/,//g' | sed 's/  //g' | awk 'NF'"
const val QUOTES = "sed 's/\"//g'"
const val META_REPO = "https://api.github.com/repos/MetaCubeX/Clash.Meta/releases/latest"