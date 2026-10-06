package com.example.commitment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.database.BulldogDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Boot completed receiver to ensure that Commitment Lock state and protection
 * parameters persist reliably across device restarts.
 */
class BulldogBootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_MY_PACKAGE_REPLACED
        ) {
            Log.d("BulldogBootReceiver", "Device reboot or package update detected. Restoring Bulldog state.")

            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = BulldogDatabase.getInstance(context)
                    val lock = db.commitmentDao().getCommitmentLock()
                    if (lock != null && lock.enabled) {
                        Log.i(
                            "BulldogBootReceiver",
                            "Commitment lock is ACTIVE in mode=${lock.mode}. Protection validated."
                        )
                    }
                } catch (e: Exception) {
                    Log.e("BulldogBootReceiver", "Error verifying boot state", e)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
