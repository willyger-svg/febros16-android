package com.example.security

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

/**
 * Device Admin Receiver to help prevent accidental or impulsive deactivation
 * of Bulldog Protection while Commitment Lock is active.
 *
 * (Documents exact behavior & limitations in UI as required by Section 15 & 28).
 */
class BulldogDeviceAdminReceiver : DeviceAdminReceiver() {

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        Toast.makeText(context, "Bulldog Protection Shield Admin Activated", Toast.LENGTH_SHORT).show()
    }

    override fun onDisableRequested(context: Context, intent: Intent): CharSequence {
        return "Warning: Disabling Device Admin will degrade your Bulldog Shield commitment protection."
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        Toast.makeText(context, "Bulldog Shield Device Admin Deactivated", Toast.LENGTH_SHORT).show()
    }
}
