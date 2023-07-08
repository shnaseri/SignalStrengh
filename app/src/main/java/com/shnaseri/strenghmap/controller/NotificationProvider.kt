package com.shnaseri.strenghmap.controller

import android.app.Notification
import android.content.Context
import android.os.Build

object NotificationProvider {
    /**
     * Sends notification of download progress
     */
    fun uploadProgress(context: Context?, maximum: Int, progress: Int): Notification {
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, "1202")
        } else {
            Notification.Builder(context).setSound(null)
        }
        return builder
            .setContentTitle("Points uploaded: $progress of $maximum")
            .setProgress(maximum, progress, false)
            .build()
        //        mNotificationManager.notify(ID_NOTIFICATION_UPLOAD, notification);
//        startForeground(ID_NOTIFICATION_UPLOAD, notification);
    }

    /**
     * Sends notification of download progress
     */
    fun uploadFinished(context: Context?): Notification {
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, "1202")
        } else {
            Notification.Builder(context).setSound(null)
        }
        return builder
            .setContentTitle("Points upload finished")
            .setContentText("No more points for upload")
            .build()
    }
}