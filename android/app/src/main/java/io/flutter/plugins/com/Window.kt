package com.example.application_lock
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.PixelFormat
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import com.ob.pinlockviewapp.PinLockListener
import com.ob.pinlockviewapp.PinLockView
import com.example.application_lock.R

@SuppressLint("InflateParams")
class Window(private val context: Context) {

    private val mView: View
    private var pinCode: String = ""
    private val mWindowManager: WindowManager
    private val layoutInflater: LayoutInflater

    // UI elements
    private var mPinLockView: PinLockView? = null
    private var txtView: TextView? = null

    // Parameters for window layout
    private var mParams: WindowManager.LayoutParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
        PixelFormat.TRANSLUCENT
    ).apply {
        gravity = Gravity.CENTER
    }

    init {
        // Initialize layout inflater and window manager
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        // Inflate the view for the window
        mView = layoutInflater.inflate(R.layout.pin_activity, null)

        // Find UI components in the inflated view
        mPinLockView = mView.findViewById(R.id.pinLockView1)
        txtView = mView.findViewById(R.id.alertError)

        // Setup the pin lock view and listeners
        setupPinLockView()
    }

	    // Public getter to access txtView
    fun getTxtView(): TextView? {
        return txtView
    }

    // Setup the PinLockView and its listener
    private fun setupPinLockView() {
        mPinLockView?.apply {
			val saveAppData: SharedPreferences = context.getSharedPreferences("save_app_data", Context.MODE_PRIVATE)
            val savedPin = saveAppData.getString("password", "PASSWORD") ?: "123456" // Fallback to default pin if not set

            setPinCode(savedPin)

            setPinLockListener(object : PinLockListener {
                override fun onPinEnter() {
                    Log.d(PinCodeActivity.TAG, "Pin entered")
                }

                override fun onPinComplete(result: Boolean) {
                    Log.d(PinCodeActivity.TAG, "Pin complete")
                    if (result) {
                        Toast.makeText(context, "Pin Code Correct", Toast.LENGTH_SHORT).show()
                        Log.d(PinCodeActivity.TAG, "Pin Correct")
                        doneButton()
						close()
                    } else {
                        Toast.makeText(context, "Pin Code Wrong", Toast.LENGTH_SHORT).show()
                        Log.d(PinCodeActivity.TAG, "Pin Wrong")
                        txtView?.visibility = View.VISIBLE // Show error message
                    }
                }

                override fun onPinDelete() {
                    Log.d("PinLockView", "Pin deleted")
                }

                override fun onPinEmpty() {
                    Log.d(PinCodeActivity.TAG, "Pin empty")
                }
            })
        }
    }

    // Function to open the window view
    fun open() {
        try {
            if (mView.windowToken == null && mView.parent == null) {
                mWindowManager.addView(mView, mParams)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Check if the window is open
    fun isOpen(): Boolean {
        return mView.windowToken != null && mView.parent != null
    }

    // Function to close the window view
    fun close() {
        try {
            Handler(Looper.getMainLooper()).postDelayed({
                if (isOpen()) {
                    mWindowManager.removeView(mView)
                    mView.invalidate()
                }
            }, 500)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Function to handle the Done button click event
    fun doneButton() {
        try {
            val saveAppData: SharedPreferences = context.getSharedPreferences("save_app_data", Context.MODE_PRIVATE)
            val savedPin = saveAppData.getString("password", "PASSWORD")!!  // Change according to your stored pin

            if (pinCode == savedPin) {
                Log.d(PinCodeActivity.TAG, "Pin verified successfully")
                close()
            } else {
                txtView?.visibility = View.VISIBLE // Show error message
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

	companion object {
        const val TAG = "PinCodeActivity"
    }
}
