package com.example.bankingnow.ui

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.bankingnow.MyApplication.Companion.prefs
import com.example.bankingnow.R
import com.example.bankingnow.databinding.ActivityMainBinding
import com.example.bankingnow.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date

class MainActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_RECORD_AUDIO_CODE = 200
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController : NavController



    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkRecordPermission()


        val navHostFragment=supportFragmentManager.findFragmentById(R.id.mainFrame) as NavHostFragment
        navController = navHostFragment.navController

        prefs.setBoolean("isLogin", false)
        prefs.setString("Dpnm", "싱크트리")
        prefs.setString("Iscd", "002116")
        prefs.setString("AccessToken", "b51e05230924338a995143c03eb8a12d9cc4e48fbfc573a00b0f3fb64a62be6e")
        prefs.setString("Bncd", "011")
        prefs.setString("Acno", "3020000009308")
        prefs.setString("FinAcno", "00820100021160000000000017092")
    }

    private fun checkRecordPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this, // this => context
                android.Manifest.permission.RECORD_AUDIO

            ) == PackageManager.PERMISSION_GRANTED -> {
                // 실제로 녹음 시작하면 됨
            }

            // 권한요청을 했는데 취소한적이 있는 경우
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.RECORD_AUDIO
            ) -> {
                // 사용자지정 권한요청 다이얼로그
                showPermissionRationalDialog()
            }
            // 권한을 부여 받지 않은경우
            else -> {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.RECORD_AUDIO),
                    REQUEST_RECORD_AUDIO_CODE
                )
            }
        }
    }

    private fun showPermissionRationalDialog() {
        AlertDialog.Builder(this)
            .setMessage("녹음 권한을 켜주셔야지 앱을 정상적으로 사용할 수 있습니다.")
            .setPositiveButton("권한 허용하기"){_,_ ->
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.RECORD_AUDIO),
                    REQUEST_RECORD_AUDIO_CODE)
            }.setNegativeButton("취소"){ dialogInterface, _ -> dialogInterface.cancel()}
            .show()
    }
}