package xyz.chz.bfm.service

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import xyz.chz.bfm.util.Util
import xyz.chz.bfm.util.command.TermCmd

class TileBox : TileService() {
    override fun onClick() {
        super.onClick()
        when (qsTile.state) {

            Tile.STATE_INACTIVE -> {
                qsTile.state = Tile.STATE_ACTIVE
                qsTile.updateTile()
                TermCmd.start {
                    Util.isProxyed = if (it) {
                        qsTile.state = Tile.STATE_ACTIVE
                        qsTile.updateTile()
                        true
                    } else {
                        qsTile.state = Tile.STATE_INACTIVE
                        qsTile.updateTile()
                        false
                    }
                }
            }

            else -> {
                qsTile.state = Tile.STATE_INACTIVE
                qsTile.updateTile()
                TermCmd.stop {
                    Util.isProxyed = if (it) {
                        qsTile.state = Tile.STATE_INACTIVE
                        qsTile.updateTile()
                        false
                    } else {
                        qsTile.state = Tile.STATE_ACTIVE
                        qsTile.updateTile()
                        true
                    }
                }
            }
        }
    }

    override fun onStartListening() {
        super.onStartListening()
        if (Util.isProxyed) qsTile.state = Tile.STATE_ACTIVE else qsTile.state = Tile.STATE_INACTIVE
        qsTile.updateTile()
    }

    override fun onStopListening() {
        super.onStopListening()
        qsTile.updateTile()
    }

}