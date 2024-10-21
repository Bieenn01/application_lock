package com.example.application_lock
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.ob.pinlockviewapp.PinLockView
import com.ob.pinlockviewapp.PinLockListener
import com.example.application_lock.R

class PinCodeActivity(private val context: Context) {

    private var pinCode: String = ""
    private var mPinLockView: PinLockView? = null
    private var sharedPreferences: SharedPreferences? = null

    init {
        setupPinLockView()
    }

    private fun setupPinLockView() {
        try {
            // Retrieve the stored pin from the password received via Method Channel
            val savedPin = pinCode ?: "123456"  // Fallback to default pin if not set

            // mPinLockView = PinLockView(context, null) // Assuming this is dynamically created or assigned elsewhere

            mPinLockView?.apply {
                setPinCode(savedPin) // Set the expected pin

                // Set pin length if method exists
                setPinLockListener(object : PinLockListener {
                    override fun onPinEnter() {
                        Log.d(TAG, "Pin entered")
                    }

                    override fun onPinComplete(result: Boolean) {
                        if (result) {
                            Log.d(TAG, "Pin Correct")
                            Toast.makeText(context, "Pin Code Correct", Toast.LENGTH_SHORT).show()
                            handleSuccessfulPin()
                        } else {
                            Log.d(TAG, "Pin Wrong")
                            Toast.makeText(context, "Pin Code Incorrect", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onPinDelete() {
                        Log.d(TAG, "Pin deleted")
                    }

                    override fun onPinEmpty() {
                        Log.d(TAG, "Pin empty")
                    }
                })
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in setting up PinLockView", e)
        }
    }

    private fun handleSuccessfulPin() {
        Log.d(TAG, "Handling successful pin entry.")
    }

    companion object {
        const val TAG = "PinCodeActivity"
    }
}
