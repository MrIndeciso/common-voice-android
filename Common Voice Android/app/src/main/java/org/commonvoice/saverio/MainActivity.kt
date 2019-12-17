package org.commonvoice.saverio

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.webkit.WebViewFragment
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.lang.Exception


class MainActivity : AppCompatActivity() {

    private var firstRun = true
    private val RECORD_REQUEST_CODE = 101
    private var PRIVATE_MODE = 0
    private val PREF_NAME = "FIRST_RUN"
    private val LANGUAGE_NAME = "LANGUAGE"
    private val LOGGED_IN_NAME = "LOGGED" //false->no logged-in || true -> logged-in
    private val USER_CONNECT_ID = "USER_CONNECT_ID"
    private val USER_NAME = "USERNAME"

    var languagesListShortArray =
        arrayOf("en") // don't change manually -> it's imported from strings.xml
    var languagesListArray =
        arrayOf("English") // don't change manually -> it's imported from strings.xml
    var selectedLanguageVar = ""
    var logged: Boolean = false
    var userId: String = ""
    var userName: String = ""

    var statisticsYou = arrayOf(
        0,
        0,
        0,
        0
    ) //(todaySpeak, todayListen, everSpeak, everListen); "-1" indicates an error -> show "?"
    var statisticsEveryone = arrayOf(
        0,
        0,
        0,
        0
    ) //(todaySpeak, todayListen, everSpeak, everListen); "-1" indicates an error -> show "?"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val sharedPref: SharedPreferences = getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        this.firstRun = sharedPref.getBoolean(PREF_NAME, true)

        // import languages from array
        this.languagesListArray = resources.getStringArray(R.array.languages)
        this.languagesListShortArray = resources.getStringArray(R.array.languages_short)

        val sharedPref2: SharedPreferences = getSharedPreferences(LANGUAGE_NAME, PRIVATE_MODE)
        this.selectedLanguageVar = sharedPref2.getString(LANGUAGE_NAME, "en")

        val sharedPref3: SharedPreferences = getSharedPreferences(LOGGED_IN_NAME, PRIVATE_MODE)
        this.logged = sharedPref3.getBoolean(LOGGED_IN_NAME, false)

        if (logged) {
            val sharedPref4: SharedPreferences = getSharedPreferences(USER_CONNECT_ID, PRIVATE_MODE)
            this.userId = sharedPref4.getString(USER_CONNECT_ID, "")

            val sharedPref5: SharedPreferences = getSharedPreferences(USER_NAME, PRIVATE_MODE)
            this.userName = sharedPref5.getString(USER_NAME, "")

            loginSuccessful()
        }

        if (this.firstRun) {
            // close main and open tutorial -- first run
            openTutorial()
        } else {
            checkPermissions()
        }

        //get_language()
    }

    fun getDashboardValues(type: String): Array<Int> {
        if (type == "you") {
            return this.statisticsYou
        } else if (type == "everyone") {
            return this.statisticsEveryone
        }
        return arrayOf(-1, -1, -1, -1) //-1 indicates and error, show "?"
    }

    fun getHiUsernameLoggedIn(): String {
        val sharedPref3: SharedPreferences = getSharedPreferences(LOGGED_IN_NAME, PRIVATE_MODE)
        this.logged = sharedPref3.getBoolean(LOGGED_IN_NAME, false)

        if (logged) {
            val sharedPref4: SharedPreferences = getSharedPreferences(USER_CONNECT_ID, PRIVATE_MODE)
            this.userId = sharedPref4.getString(USER_CONNECT_ID, "")

            val sharedPref5: SharedPreferences = getSharedPreferences(USER_NAME, PRIVATE_MODE)
            this.userName = sharedPref5.getString(USER_NAME, "")
        }

        if (this.userName == "") {
            return getString(R.string.text_hi_username) + "!"
        } else {
            return getString(R.string.text_hi_username) + ", " + userName + "!"
        }
    }

    fun getLanguage() {
        Toast.makeText(
            this,
            "Language: " + this.selectedLanguageVar + " index: " + languagesListShortArray.indexOf(
                this.selectedLanguageVar
            ),
            Toast.LENGTH_LONG
        ).show()
    }

    fun loginSuccessful() {
        //login successful -> show username and log-out button
        /*Toast.makeText(
            this,
            "Login successful!",
            Toast.LENGTH_LONG
        ).show()*/
    }

    fun setLanguageSettings(lang: String) {
        val sharedPref: SharedPreferences = getSharedPreferences(LANGUAGE_NAME, PRIVATE_MODE)
        val editor = sharedPref.edit()
        editor.putString(LANGUAGE_NAME, lang)
        editor.apply()

        var language_changed = false
        if (this.selectedLanguageVar != lang) {
            language_changed = true
        }

        this.selectedLanguageVar = lang

        if (language_changed) {
            Toast.makeText(
                this,
                getString(R.string.toast_language_changed).replace(
                    "{{*{{lang}}*}}",
                    this.languagesListArray.get(this.languagesListShortArray.indexOf(this.getSelectedLanguage()))
                ),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun getLanguageList(): ArrayAdapter<String> {
        return ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, languagesListArray)
    }

    fun getSelectedLanguage(): String {
        return this.selectedLanguageVar
    }

    fun openTutorial() {
        val intent = Intent(this, TutorialActivity::class.java).also {
            startActivity(it)
        }
        finish()
    }

    fun openSpeakSection() {
        val intent = Intent(this, SpeakActivity::class.java).also {
            startActivity(it)
        }
    }

    fun openListenSection() {
        val intent = Intent(this, ListenActivity::class.java).also {
            startActivity(it)
        }
    }

    fun openLoginSection() {
        val intent = Intent(this, LoginActivity::class.java).also {
            startActivity(it)
            //close the MainActivity
            finish()
        }
    }

    fun openLogoutSection() {
        // logout -> delete USERNAME, USERID e LOGGEDIN variables (shared)
        val intent = Intent(this, LoginActivity::class.java).also {
            startActivity(it)
            //close the MainActivity
            finish()
        }
    }

    fun noLoggedInNoStatisticsYou() {
        Toast.makeText(
            this,
            getString(R.string.toastNoLoginNoStatistics),
            Toast.LENGTH_LONG
        ).show()
    }

    fun loadStatistics() {
        //load statistics
        var everyoneTodaySpeak: Int = loadStatisticsOf("everyoneTodaySpeak")
        this.statisticsEveryone = arrayOf(
            loadStatisticsOf("everyoneTodaySpeak"),
            loadStatisticsOf("everyoneTodayListen"),
            loadStatisticsOf("everyoneEverSpeak"),
            loadStatisticsOf("everyoneEverListen")
        )
        this.statisticsYou = arrayOf(
            loadStatisticsOf("youTodaySpeak"),
            loadStatisticsOf("youTodayListen"),
            loadStatisticsOf("youEverSpeak"),
            loadStatisticsOf("youEverListen")
        )
        //println(" >> >> "+loadStatisticsOf("everyoneTodaySpeak"))
    }

    fun loadStatisticsOf(type: String): Int {
        var valueToReturn = -1
        var speakOrListen = true //true->speak, false->listen
        if (type.contains("Listen")) {
            speakOrListen = false
        }

        val requestUrl = when (type) {
            "youTodaySpeak" -> "" //?
            "youTodayListen" -> "" //?
            "youEverSpeak" -> "https://voice.mozilla.org/api/v1/user_client" //clips_count
            "youEverListen" -> "https://voice.mozilla.org/api/v1/user_client" //votes_count
            "everyoneTodaySpeak" -> "https://voice.mozilla.org/api/v1/it/clips/daily_count" //just the value we need
            "everyoneTodayListen" -> "https://voice.mozilla.org/api/v1/it/clips/votes/daily_count" //just the value we need
            "everyoneEverSpeak" -> "https://voice.mozilla.org/api/v1/it/clips/stats" //total
            "everyoneEverListen" -> "https://voice.mozilla.org/api/v1/it/clips/stats" //valid
            else -> ""
        }

        if (requestUrl != "") {
            try {
                val que = Volley.newRequestQueue(this)
                val req = object : StringRequest(Request.Method.GET, requestUrl,
                    Response.Listener {
                        val jsonResult = it.toString()
                        //println(" >>>> " + jsonResult)
                        if (type == "everyoneTodaySpeak" || type == "everyoneTodayListen") {
                            if (jsonResult.toInt() >= 0) {
                                valueToReturn = jsonResult.toInt()
                                //println(" >>>> YES! ")
                            }
                        }
                        //println(" >>>> " + valueToReturn)
                    }, Response.ErrorListener {
                        //println(" -->> Something wrong: " + it.toString() + " <<-- ")
                    }
                ) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val headers = HashMap<String, String>()
                        //it permits to get the audio to validate (just if user doesn't do the log-in/sign-up)
                        if (logged) {
                            headers.put(
                                "Cookie",
                                "connect.sid=" + userId
                            )
                        }
                        return headers
                    }
                }
                que.add(req)
            } catch (e: Exception) {
                //println(" -->> Something wrong: " + e.toString() + " <<-- ")
                valueToReturn = -1
            }
        }
        //println(" >> >> "+valueToReturn)
        return valueToReturn
    }

    fun checkPermissions() {
        try {
            checkRecordVoicePermission()
        } catch (e: Exception) {
            //println(" -->> Exception: " + e.toString())
        }
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED
            ) {
                checkStoragePermission()
            }
        } catch (e: Exception) {
            //println(" -->> Exception: " + e.toString())
        }
    }

    fun checkRecordVoicePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                RECORD_REQUEST_CODE
            )
        }
    }

    fun checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                RECORD_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            RECORD_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    checkPermissions()
                }
            }
        }
    }
}
