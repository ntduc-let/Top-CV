package com.ntduc.topcv.ui.ui.account.information.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.ntduc.contextutils.inflater
import com.ntduc.datetimeutils.currentCalendar
import com.ntduc.datetimeutils.year
import com.ntduc.toastutils.shortToast
import com.ntduc.topcv.BuildConfig
import com.ntduc.topcv.databinding.ActivityAccountInformationBinding
import com.ntduc.topcv.ui.data.model.Account
import com.ntduc.topcv.ui.data.model.UserDB
import com.ntduc.topcv.ui.data.model.UserInfo
import com.ntduc.topcv.ui.networking.CallApiListener
import com.ntduc.topcv.ui.ui.account.change_password.activity.ChangePasswordActivity
import com.ntduc.topcv.ui.ui.account.information.adapter.MenuBirthYearAdapter
import com.ntduc.topcv.ui.ui.account.information.adapter.MenuGenderAdapter
import com.ntduc.topcv.ui.ui.account.information.dialog.ChooseImageBottomDialog
import com.ntduc.topcv.ui.ui.account.information.dialog.LogoutDialog
import com.ntduc.topcv.ui.ui.dialog.LoadingDialog
import com.ntduc.topcv.ui.ui.home.activity.MainActivity
import com.ntduc.topcv.ui.utils.PermissionUtil
import com.ntduc.topcv.ui.utils.Prefs
import com.ntduc.topcv.ui.ui.account.information.dialog.RequestPermissionCameraDialog
import com.ntduc.topcv.ui.ui.account.information.dialog.RequestPermissionReadAllFileDialog
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.File

class AccountInformationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountInformationBinding.inflate(inflater)
        setContentView(binding.root)

        init()
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun init() {
        initView()
        initData()
        initEvent()
    }

    private fun initEvent() {
        binding.btnClose.setOnClickListener {
            onBackPressed()
        }

        binding.btnChangePassword.setOnClickListener {
            changePWLauncher.launch(Intent(this, ChangePasswordActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            val dialog = LogoutDialog()
            dialog.setClickLogoutListener {
                mPrefs!!.saveLogin(false)
                val intent = Intent(this, MainActivity::class.java)
                setResult(RESULT_LOGOUT, intent)
                finish()
            }
            dialog.show(supportFragmentManager, "LogoutDialog")
        }

        binding.layoutAva.root.setOnClickListener {
            openChooseImageDialog()
        }

        binding.layoutBottom.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.layoutBottom.btnUpdate.setOnClickListener {
            if (account != null) {
                if (binding.edtName.text.trim().length !in 2..50){
                    shortToast("Tên phải từ 2 đến 50 ký tự")
                }else{
                    updateAccount()
                }
            } else {
                shortToast("Có lỗi xảy ra, vui lòng thử lại")
            }
        }
    }

    private fun initData() {
        mPrefs = Prefs(this)
        viewModel = ViewModelProvider(this)[AccountInformationActivityVM::class.java]
        db = Firebase.firestore
        storage = Firebase.storage

        if (mPrefs!!.email != null && mPrefs!!.password != null) {
            val account = Account(account = mPrefs!!.email, hash = mPrefs!!.password)

            viewModel.loginAccount(
                account = account,
                callApiListener = object : CallApiListener {
                    override fun onSuccess(userInfo: UserInfo?) {
                        if (userInfo != null) {
                            getDataAccount(userInfo)
                        } else {
                            shortToast("Có lỗi xảy ra, vui lòng thử lại")
                            mPrefs!!.saveLogin(false)
                            restartApp()
                        }
                    }

                    override fun onError(e: Throwable) {
                        shortToast("Có lỗi xảy ra, vui lòng thử lại")
                        mPrefs!!.saveLogin(false)
                        restartApp()
                    }
                })
        } else {
            shortToast("Có lỗi xảy ra, vui lòng thử lại")
            mPrefs!!.saveLogin(false)
            restartApp()
        }
    }

    private fun initView() {
        dataBirthYear = getDataBirthYear()
        val menuBirthYearAdapter = MenuBirthYearAdapter(this, dataBirthYear)
        binding.textBirthYear.setAdapter(menuBirthYearAdapter)

        dataGender = getDataGender()
        val menuGenderAdapter = MenuGenderAdapter(this, dataGender)
        binding.textGender.setAdapter(menuGenderAdapter)
    }

    private fun getDataAccount(userInfo: UserInfo) {
        val docRef = db.collection(userInfo._id!!).document("account")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("ntduc_debug", "DocumentSnapshot data: ${document.data}")
                    val account = document.toObject<UserDB>()
                    if (account != null) {
                        this.account = account
                        updateUI()
                    } else {
                        shortToast("Có lỗi xảy ra, vui lòng thử lại")
                        onBackPressed()
                    }
                } else {
                    shortToast("Có lỗi xảy ra, vui lòng thử lại")
                    onBackPressed()
                }
            }
            .addOnFailureListener { e ->
                Log.d("ntduc_debug", "get failed with $e")
                shortToast("Có lỗi xảy ra, vui lòng thử lại")
                onBackPressed()
            }
    }

    private fun updateAccount() {
        loadingDialog = LoadingDialog()
        loadingDialog!!.show(supportFragmentManager, "LoadingDialog")

        val updateAccount = UserDB(
            userInfo = account!!.userInfo,
            name = binding.edtName.text.toString(),
            birthYear = if (binding.textBirthYear.text.isEmpty()) null else binding.textBirthYear.text.toString().toInt(),
            gender = if (binding.textGender.text.isEmpty()) null else binding.textGender.text.toString(),
            height = if (binding.textHeight.text.isEmpty()) null else binding.textHeight.text.toString().toInt(),
            weight = if (binding.textWeight.text.isEmpty()) null else binding.textWeight.text.toString().toInt(),
            experience = if (binding.textExperience.text.isEmpty()) null else binding.textExperience.text.toString(),
            nameOfHighSchool = if (binding.textNameOfHighSchool.text.isEmpty()) null else binding.textNameOfHighSchool.text.toString(),
            numberHousehold = if (binding.textNumberHousehold.text.isEmpty()) null else binding.textNumberHousehold.text.toString(),
            idCCCD = if (binding.textCccd.text.isEmpty()) null else binding.textCccd.text.toString(),
            hobby = if (binding.textHobby.text.isEmpty()) null else binding.textHobby.text.toString(),
            personality = if (binding.textPersonality.text.isEmpty()) null else binding.textPersonality.text.toString(),
            hometown = if (binding.textHometown.text.isEmpty()) null else binding.textHometown.text.toString(),
            levelEducational = if (binding.textLevelEducational.text.isEmpty()) null else binding.textLevelEducational.text.toString().toInt(),
            wish = null,
            profession = null,
            specialConditions = null,
            workPlace = null,
            province = null,
            currentJob = null
        )
        db.collection(account!!.userInfo!!._id!!).document("account")
            .set(updateAccount)
            .addOnSuccessListener {
                Log.d("ntduc_debug", "DocumentSnapshot successfully written!")
                loadingDialog?.dismiss()
                shortToast("Cập nhật tài khoản thành công")

                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra(MainActivity.KEY_USER_DB, updateAccount)
                setResult(RESULT_UPDATE, intent)
                finish()
            }
            .addOnFailureListener { e ->
                Log.d("ntduc_debug", "Error writing document: $e")
                loadingDialog?.dismiss()

                shortToast("Có lỗi xảy ra. Vui lòng thử lại")
            }
    }

    private fun updateUI() {
        binding.layoutLoading.root.visibility = View.GONE

        binding.edtName.setText(account!!.name)

        if (account!!.birthYear != null) binding.textBirthYear.setText(account!!.birthYear!!.toString())
        if (account!!.gender != null) binding.textGender.setText(account!!.gender!!.toString())
        if (account!!.height != null) binding.textHeight.setText(account!!.height!!.toString())
        if (account!!.weight != null) binding.textWeight.setText(account!!.weight!!.toString())
        if (account!!.nameOfHighSchool != null) binding.textNameOfHighSchool.setText(account!!.nameOfHighSchool!!.toString())
        if (account!!.numberHousehold != null) binding.textNumberHousehold.setText(account!!.numberHousehold!!.toString())
        if (account!!.idCCCD != null) binding.textCccd.setText(account!!.idCCCD!!.toString())
        if (account!!.hobby != null) binding.textHobby.setText(account!!.hobby!!.toString())
        if (account!!.personality != null) binding.textPersonality.setText(account!!.personality!!.toString())
        if (account!!.hometown != null) binding.textHometown.setText(account!!.hometown!!.toString())
        if (account!!.levelEducational != null) binding.textLevelEducational.setText(account!!.levelEducational!!.toString())
        if (account!!.specialConditions != null) binding.textSpecialConditions.setText(account!!.specialConditions!!.toString())
    }

    private fun openChooseImageDialog() {
        val dialog = ChooseImageBottomDialog()
        dialog.setOnTakePhotoListener {
            if (PermissionUtil.checkPermissionCamera(this)) {
                openCamera()
                dialog.dismiss()
            } else {
                requestCameraPermission()
            }
        }

        dialog.setOnChooseFromAlbumsListener {
            if (PermissionUtil.checkPermissionReadAllFile(this)) {
                chooseFile()
                dialog.dismiss()
            } else {
                requestPermissionReadAllFile()
            }
        }

        dialog.setOnDeleteListener {

        }

        dialog.show(supportFragmentManager, "TakePhotoBottomDialog")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val result = CropImage.getActivityResult(data)
                binding.layoutAva.imgAva.setImageURI(result.uri)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                shortToast("Tải ảnh thất bại")
            }
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        uriCamera = getCaptureImageOutputUri(this)

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriCamera)
        intent.putExtra("return-data", true)
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        imageCaptureLauncher.launch(intent)
    }

    private fun chooseFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        val uri = Uri.parse(Environment.getDownloadCacheDirectory().path)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.setDataAndType(uri, "image/*")
        selectFileLauncher.launch(intent)
    }

    private fun restartApp() {
        val restartIntent = packageManager.getLaunchIntentForPackage(packageName)!!
        restartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(restartIntent)
    }

    private fun getDataBirthYear(): ArrayList<Int> {
        val result = arrayListOf<Int>()
        for (year in (currentCalendar.year - 9) downTo (currentCalendar.year - 9 - 90))
            result.add(year)
        return result
    }

    private fun getDataGender(): ArrayList<String> {
        val result = arrayListOf<String>()
        result.add("Nữ")
        result.add("Nam")
        result.add("Không xác định")

        return result
    }

    private fun getCaptureImageOutputUri(context: Context): Uri? {
        var outputFileUri: Uri? = null
        val getImage = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (getImage != null) {
            outputFileUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(
                    context, BuildConfig.APPLICATION_ID + ".provider",
                    File(getImage.path, "file" + System.currentTimeMillis().toString() + ".jpg")
                )
            } else {
                Uri.fromFile(
                    File(
                        getImage.path,
                        "file" + System.currentTimeMillis().toString() + ".jpeg"
                    )
                )
            }
        }
        return outputFileUri
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            REQ_CAMERA_CODE
        )
    }

    private fun requestPermissionReadAllFile() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            openDialogPermissionReadAllFile()
        } else {
            val permissions = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            requestPermissions(permissions, REQUEST_PERMISSION_READ_WRITE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_CAMERA_CODE) {
            for (permission in permissions) {
                if (permission == Manifest.permission.CAMERA) {
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            permission
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                            requestCameraPermission()
                        } else {
                            openDialogPermissionCamera()
                        }
                    }
                }
            }
        }
    }

    private fun openDialogPermissionCamera() {
        val dialog = RequestPermissionCameraDialog()
        dialog.setOnAllowListener {
            goSetting()
        }
        dialog.show(supportFragmentManager, "RequestPermissionCameraDialog")
    }

    private fun openDialogPermissionReadAllFile() {
        val dialog = RequestPermissionReadAllFileDialog()
        dialog.setOnAllowListener {
            requestAccessAllFile()
        }
        dialog.show(supportFragmentManager, "RequestPermissionReadWriteFileDialog")
    }

    private fun requestAccessAllFile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri)
                requestPermissionLauncher.launch(intent)
            } catch (e: Exception) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                requestPermissionLauncher.launch(intent)
            }
        } else {
            try {
                val uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri)
                requestPermissionLauncher.launch(intent)
            } catch (_: Exception) {
            }
        }
    }

    private fun goSetting() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.data = Uri.parse("package:" + this.packageName)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        startActivity(intent)
    }

    companion object {
        const val RESULT_LOGOUT = 100
        const val RESULT_UPDATE = 200

        private const val REQUEST_PERMISSION_READ_WRITE = 300
        private const val REQ_CAMERA_CODE = 400
    }

    private lateinit var binding: ActivityAccountInformationBinding
    private lateinit var viewModel: AccountInformationActivityVM
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private var dataBirthYear: ArrayList<Int> = arrayListOf()
    private var dataGender: ArrayList<String> = arrayListOf()

    private var mPrefs: Prefs? = null
    private var account: UserDB? = null
    private var loadingDialog: LoadingDialog? = null
    private var uriCamera: Uri? = null

    private val changePWLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                mPrefs!!.loadSavedPreferences()
            }
        }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (PermissionUtil.checkPermissionReadAllFile(this)) {
                chooseFile()
            }
        }

    private val imageCaptureLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                CropImage.activity(uriCamera)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setMultiTouchEnabled(true)
                    .start(this)
            } else {
                shortToast("Cancel")
            }
        }

    private val selectFileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                if (it.data != null) {
                    uriCamera = it.data!!.data!!
                    CropImage.activity(uriCamera)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMultiTouchEnabled(true)
                        .start(this)
                }
            } else {
                shortToast("Cancel")
            }
        }
}