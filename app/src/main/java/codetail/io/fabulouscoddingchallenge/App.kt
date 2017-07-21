package codetail.io.fabulouscoddingchallenge

import android.app.Application
import timber.log.Timber

/**
 * created at 7/21/17
 *
 * @author Ozodrukh
 * @version 1.0
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

}