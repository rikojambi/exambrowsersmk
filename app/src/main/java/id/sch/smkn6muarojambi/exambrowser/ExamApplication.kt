package id.sch.smkn6muarojambi.exambrowser

import android.app.Activity
import android.app.Application
import android.os.Bundle

/** Mendeteksi kondisi aplikasi masuk background menggunakan lifecycle resmi Android. */
class ExamApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
            override fun onActivityStarted(activity: Activity) = Unit

            override fun onActivityResumed(activity: Activity) {
                if (SessionMonitor.consumeReturn() != null) {
                    SessionMonitor.onReturn?.invoke()
                }
            }

            override fun onActivityPaused(activity: Activity) = Unit

            override fun onActivityStopped(activity: Activity) {
                // Rotasi layar bukan perpindahan aplikasi.
                if (!activity.isChangingConfigurations) {
                    SessionMonitor.markInterrupted()
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
            override fun onActivityDestroyed(activity: Activity) = Unit
        })
    }
}
