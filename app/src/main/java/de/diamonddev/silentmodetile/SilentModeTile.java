package de.diamonddev.silentmodetile;

import android.app.NotificationManager;
import android.graphics.drawable.Icon;
import android.media.AudioManager;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.content.Context;
import android.util.Log;

public class SilentModeTile extends TileService {

    @Override
    public void onClick() {
        AudioManager am =
                (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        NotificationManager nm =
                (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        if (!nm.isNotificationPolicyAccessGranted()) return;

        Log.i("info", "onClick");

        this.setNextMode(am, nm);

        updateTile();
    }

    private void updateTile() {
        AudioManager am =
                (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        NotificationManager nm =
                (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        for (int[] mode: MODES) {
            if (isCurrentMode(mode, am, nm)) {
                getQsTile().setState(mode[2]);
                getQsTile().setIcon(Icon.createWithResource(getApplicationContext(), mode[3]));
                getQsTile().updateTile();
            }
        }
    }

    @Override
    public void onStartListening() {
        updateTile();
    }

    private final int[] MODE_NORMAL = {AudioManager.RINGER_MODE_NORMAL, NotificationManager.INTERRUPTION_FILTER_ALL, Tile.STATE_INACTIVE, R.drawable.normal};
    private final int[] MODE_VIBRATE = {AudioManager.RINGER_MODE_VIBRATE, NotificationManager.INTERRUPTION_FILTER_ALL, Tile.STATE_INACTIVE, R.drawable.vibrate};
    private final int[] MODE_PRIORITY = {AudioManager.RINGER_MODE_VIBRATE, NotificationManager.INTERRUPTION_FILTER_PRIORITY, Tile.STATE_ACTIVE, R.drawable.dnd};
    private final int[] MODE_FULLMUTE = {AudioManager.RINGER_MODE_SILENT, NotificationManager.INTERRUPTION_FILTER_NONE, Tile.STATE_ACTIVE, R.drawable.mute};
    private final int[][] MODES = {MODE_NORMAL, MODE_VIBRATE, MODE_PRIORITY, MODE_FULLMUTE};


    private void setNextMode(AudioManager am, NotificationManager nm) {
        for (int i = 0; i < MODES.length; i++) {
            if (isCurrentMode(MODES[i], am, nm)) {
                setMode(MODES[(i+1) % 4], am, nm);
                break;
            }
        }
    }

    private boolean isCurrentMode(int[] mode, AudioManager am, NotificationManager nm) {
        return (am.getRingerMode() == mode[0] || nm.getCurrentInterruptionFilter() == NotificationManager.INTERRUPTION_FILTER_PRIORITY)
                && nm.getCurrentInterruptionFilter() == mode[1];
    }

    private void setMode(int[] mode, AudioManager am, NotificationManager nm) {
        am.setRingerMode(mode[0]);
        nm.setInterruptionFilter(mode[1]);
    }
}