package com.example.siriusproject.app

import android.app.Application
import android.content.Context
import android.util.Log
import com.example.siriusproject.boofcv.CameraSpecs
import com.example.siriusproject.boofcv.DemoPreference

class SiriusProjectApp: Application() {

    companion object {
        private const val TAG = "ReconstructorApp"
    }

// --------- BoofCV init
    /**
     * Used for storage of global variables. These were originally static variables that could
     * get discarded if the main activity was unloaded.
     */
    @JvmField
    val specs: MutableList<CameraSpecs?> = ArrayList()

    // specifies which camera to use an image size
    @JvmField
    var preference = DemoPreference()

    // If another activity modifies the demo preferences this needs to be set to true so that it knows to reload
    // camera parameters.
    @JvmField
    var changedPreferences = false

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        Log.i(TAG, "Not reporting errors with ACRA")
        return
    }
// --------- End

}