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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.ntduc.androidimagecropper.CropImage
import com.ntduc.androidimagecropper.CropImageView
import com.ntduc.contextutils.inflater
import com.ntduc.datetimeutils.currentCalendar
import com.ntduc.datetimeutils.year
import com.ntduc.toastutils.shortToast
import com.ntduc.topcv.BuildConfig
import com.ntduc.topcv.R
import com.ntduc.topcv.databinding.ActivityAccountInformationBinding
import com.ntduc.topcv.ui.data.model.*
import com.ntduc.topcv.ui.networking.CallApiListener
import com.ntduc.topcv.ui.ui.account.change_password.activity.ChangePasswordActivity
import com.ntduc.topcv.ui.ui.account.information.adapter.ItemProfessionAdapter
import com.ntduc.topcv.ui.ui.account.information.adapter.MenuBirthYearAdapter
import com.ntduc.topcv.ui.ui.account.information.adapter.MenuGenderAdapter
import com.ntduc.topcv.ui.ui.account.information.adapter.MenuProfessionAdapter
import com.ntduc.topcv.ui.ui.account.information.dialog.*
import com.ntduc.topcv.ui.ui.dialog.LoadingDialog
import com.ntduc.topcv.ui.ui.home.activity.MainActivity
import com.ntduc.topcv.ui.utils.PermissionUtil
import com.ntduc.topcv.ui.utils.Prefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

        binding.layoutProfession.btnAdd.setOnClickListener {
            val dialog = ChooseProfessionBottomDialog()
            dialog.setListProfession(professions)
            dialog.setOnClickItemListener {
                if (it.isSelected){
                    adapter.list.add(it)
                }else{
                    adapter.list.remove(it)
                }
                adapter.reloadData()
            }
            dialog.show(supportFragmentManager, "ChooseProfessionBottomDialog")
        }

        binding.layoutBottom.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.layoutBottom.btnUpdate.setOnClickListener {
            if (account != null) {
                if (binding.edtName.text.trim().length !in 2..50) {
                    shortToast("T??n ph???i t??? 2 ?????n 50 k?? t???")
                } else {
                    updateAccount()
                }
            } else {
                shortToast("C?? l???i x???y ra, vui l??ng th??? l???i")
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
                            shortToast("C?? l???i x???y ra, vui l??ng th??? l???i")
                            mPrefs!!.saveLogin(false)
                            restartApp()
                        }
                    }

                    override fun onError(e: Throwable) {
                        shortToast("C?? l???i x???y ra, vui l??ng th??? l???i")
                        mPrefs!!.saveLogin(false)
                        restartApp()
                    }
                })
        } else {
            shortToast("C?? l???i x???y ra, vui l??ng th??? l???i")
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

        adapter = ItemProfessionAdapter(this, ItemProfessionAdapter.MODE_REMOVE, arrayListOf())
        binding.layoutProfession.rcvList.adapter = adapter
        binding.layoutProfession.rcvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        dataExperience = getDataExperience()
        val menuExperienceAdapter = MenuGenderAdapter(this, dataExperience)
        binding.textExperience.setAdapter(menuExperienceAdapter)

        dataSalary = getDataSalary()
        val menuSalaryAdapter = MenuGenderAdapter(this, dataSalary)
        binding.textSalary.setAdapter(menuSalaryAdapter)
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
                        shortToast("C?? l???i x???y ra, vui l??ng th??? l???i")
                        onBackPressed()
                    }
                } else {
                    shortToast("C?? l???i x???y ra, vui l??ng th??? l???i")
                    onBackPressed()
                }
            }
            .addOnFailureListener { e ->
                Log.d("ntduc_debug", "get failed with $e")
                shortToast("C?? l???i x???y ra, vui l??ng th??? l???i")
                onBackPressed()
            }
    }

    private fun updateAccount() {
        loadingDialog = LoadingDialog()
        loadingDialog!!.show(supportFragmentManager, "LoadingDialog")

        val professions = arrayListOf<ProfessionDB>()
        adapter.list.forEach {
            professions.add(it.professionDB)
        }

        val currentJob = JobDB(
            address = if (binding.textCurrentAddress.text.trim().isEmpty()) null else binding.textCurrentAddress.text.trim().toString(),
            company = if (binding.textCurrentCompany.text.trim().isEmpty()) null else binding.textCurrentCompany.text.trim().toString(),
            profession = currentProfession
        )

        val updateAccount = UserDB(
            userInfo = account!!.userInfo,
            name = binding.edtName.text.toString(),
            birthYear = if (binding.textBirthYear.text.trim().isEmpty()) null else binding.textBirthYear.text.trim().toString().toInt(),
            gender = if (binding.textGender.text.trim().isEmpty()) null else binding.textGender.text.trim().toString(),
            height = if (binding.textHeight.text.trim().isEmpty()) null else binding.textHeight.text.trim().toString().toInt(),
            weight = if (binding.textWeight.text.trim().isEmpty()) null else binding.textWeight.text.trim().toString().toInt(),
            experience = if (binding.textExperience.text.trim().isEmpty()) null else binding.textExperience.text.trim().toString(),
            nameOfHighSchool = if (binding.textNameOfHighSchool.text.trim().isEmpty()) null else binding.textNameOfHighSchool.text.trim().toString(),
            numberHousehold = if (binding.textNumberHousehold.text.trim().isEmpty()) null else binding.textNumberHousehold.text.trim().toString(),
            idCCCD = if (binding.textCccd.text.trim().isEmpty()) null else binding.textCccd.text.trim().toString(),
            hobby = if (binding.textHobby.text.trim().isEmpty()) null else binding.textHobby.text.trim().toString(),
            personality = if (binding.textPersonality.text.trim().isEmpty()) null else binding.textPersonality.text.trim().toString(),
            hometown = if (binding.textHometown.text.trim().isEmpty()) null else binding.textHometown.text.trim().toString(),
            levelEducational = if (binding.textLevelEducational.text.trim().isEmpty()) null else binding.textLevelEducational.text.trim().toString().toInt(),
            wish = if (binding.textWish.text.trim().isEmpty()) null else binding.textWish.text.trim().toString(),
            professions = professions,
            specialConditions = if (binding.textSpecialConditions.text.trim().isEmpty()) null else binding.textSpecialConditions.text.trim().toString(),
            workPlace = if (binding.textWorkPlace.text.trim().isEmpty()) null else binding.textWorkPlace.text.trim().toString(),
            salary = if (binding.textSalary.text.trim().isEmpty()) null else binding.textSalary.text.trim().toString(),
            currentJob = currentJob
        )
        db.collection(account!!.userInfo!!._id!!).document("account")
            .set(updateAccount)
            .addOnSuccessListener {
                Log.d("ntduc_debug", "DocumentSnapshot successfully written!")
                if (isChangeAvatar) {
                    val storageRef = storage.reference
                    val avatarRef =
                        storageRef.child("${account!!.userInfo!!._id!!}/account/avatar/avatar.jpg")
                    if (isDeleteAvatar) {
                        val deleteTask = avatarRef.delete()
                        deleteTask.addOnSuccessListener {
                            loadingDialog?.dismiss()
                            shortToast("C???p nh???t t??i kho???n th??nh c??ng")

                            val intent = Intent(this, MainActivity::class.java)
                            intent.putExtra(MainActivity.KEY_USER_DB, updateAccount)
                            setResult(RESULT_UPDATE, intent)
                            finish()
                        }.addOnFailureListener {
                            loadingDialog?.dismiss()
                            shortToast("C???p nh???t ???nh ?????i di???n th???t b???i")
                        }
                    } else {
                        val uploadTask = avatarRef.putFile(uriCamera!!)
                        uploadTask.addOnSuccessListener {
                            loadingDialog?.dismiss()
                            shortToast("C???p nh???t t??i kho???n th??nh c??ng")

                            val intent = Intent(this, MainActivity::class.java)
                            intent.putExtra(MainActivity.KEY_USER_DB, updateAccount)
                            setResult(RESULT_UPDATE, intent)
                            finish()
                        }.addOnFailureListener {
                            loadingDialog?.dismiss()
                            shortToast("C???p nh???t ???nh ?????i di???n th???t b???i")
                        }
                    }
                } else {
                    loadingDialog?.dismiss()
                    shortToast("C???p nh???t t??i kho???n th??nh c??ng")

                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra(MainActivity.KEY_USER_DB, updateAccount)
                    setResult(RESULT_UPDATE, intent)
                    finish()
                }
            }
            .addOnFailureListener { e ->
                Log.d("ntduc_debug", "Error writing document: $e")
                loadingDialog?.dismiss()

                shortToast("C?? l???i x???y ra. Vui l??ng th??? l???i")
            }
    }

    private fun updateUI() {
        val docRef = db.collection("top_cv_global").document("profession_global")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    lifecycleScope.launch(Dispatchers.IO){
                        val professionsDB = document.toObject<ProfessionsDB>()

                        if (professionsDB != null) {
                            professions = arrayListOf()
                            professionsDB.professions?.forEach {
                                if (account!!.currentJob != null
                                    && account!!.currentJob!!.profession != null
                                    && account!!.currentJob!!.profession!!.name == it.name){
                                    currentProfession = it
                                }

                                val profession = Profession(it)
                                if (account!!.professions != null && account!!.professions!!.isNotEmpty()) {
                                    if (account!!.professions!!.contains(it)){
                                        profession.isSelected = true
                                    }
                                }
                                professions.add(profession)
                            }

                            val result = professions.filter {
                                it.isSelected
                            }

                            withContext(Dispatchers.Main){
                                binding.layoutLoading.root.visibility = View.GONE
                                adapter.updateData(result)

                                dataCurrentProfession = professions
                                val menuProfessionAdapter = MenuProfessionAdapter(this@AccountInformationActivity, dataCurrentProfession)
                                binding.textCurrentProfession.setAdapter(menuProfessionAdapter)
                                if (currentProfession!= null) binding.textCurrentProfession.setText(currentProfession!!.name)
                                binding.textCurrentProfession.setOnItemClickListener { _, _, position, _ ->
                                    currentProfession = professionsDB.professions!![position]
                                }
                            }

                        } else {
                            withContext(Dispatchers.Main){
                                shortToast("C?? l???i x???y ra, vui l??ng th??? l???i")
                                onBackPressed()
                            }
                        }
                    }
                } else {
                    shortToast("C?? l???i x???y ra, vui l??ng th??? l???i")
                    onBackPressed()
                }
            }
            .addOnFailureListener { e ->
                shortToast("C?? l???i x???y ra, vui l??ng th??? l???i")
                onBackPressed()
            }


        binding.edtName.setText(account!!.name)

        Glide.with(this)
            .load("https://firebasestorage.googleapis.com/v0/b/topcv-androidnc.appspot.com/o/${account!!.userInfo!!._id}%2Faccount%2Favatar%2Favatar.jpg?alt=media")
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .placeholder(R.color.black)
            .error(R.drawable.ic_ava_48dp)
            .into(binding.layoutAva.imgAva)

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
        if (account!!.experience != null) binding.textExperience.setText(account!!.experience!!.toString())
        if (account!!.currentJob != null){
            if (account!!.currentJob!!.address != null) binding.textCurrentAddress.setText(account!!.currentJob!!.address)
            if (account!!.currentJob!!.company != null) binding.textCurrentCompany.setText(account!!.currentJob!!.company)
        }
        if (account!!.salary != null) binding.textSalary.setText(account!!.salary!!.toString())
        if (account!!.workPlace != null) binding.textWorkPlace.setText(account!!.workPlace!!.toString())
        if (account!!.wish != null) binding.textWish.setText(account!!.wish!!.toString())
    }

    private fun openChooseImageDialog() {
        val dialog = ChooseImageBottomDialog()
        dialog.setOnTakePhotoListener {
            if (PermissionUtil.checkPermissionCamera(this)) {
                openCamera()
            } else {
                requestCameraPermission()
            }
            dialog.dismiss()
        }

        dialog.setOnChooseFromAlbumsListener {
            if (PermissionUtil.checkPermissionReadAllFile(this)) {
                chooseFile()
            } else {
                requestPermissionReadAllFile()
            }
            dialog.dismiss()
        }

        dialog.setOnDeleteListener {
            isChangeAvatar = true
            isDeleteAvatar = true
            binding.layoutAva.imgAva.setImageResource(R.drawable.ic_ava_48dp)
        }

        dialog.show(supportFragmentManager, "TakePhotoBottomDialog")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val result = CropImage.getActivityResult(data)
                Glide.with(this)
                    .load(result?.uri)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(R.color.black)
                    .error(R.drawable.ic_ava_48dp)
                    .into(binding.layoutAva.imgAva)
                isChangeAvatar = true
                isDeleteAvatar = false
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                shortToast("T???i ???nh th???t b???i")
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
        result.add("N???")
        result.add("Nam")
        result.add("Kh??ng x??c ?????nh")

        return result
    }

    private fun getDataExperience(): ArrayList<String> {
        val result = arrayListOf<String>()
        result.add("Ch??a c?? kinh nghi???m")
        result.add("D?????i 1 n??m")
        result.add("1 n??m")
        result.add("2 n??m")
        result.add("3 n??m")
        result.add("4 n??m")
        result.add("5 n??m")
        result.add("Tr??n 5 n??m")

        return result
    }

    private fun getDataSalary(): ArrayList<String> {
        val result = arrayListOf<String>()
        result.add("D?????i 3 tri???u")
        result.add("3 - 5 tri???u")
        result.add("5 - 7 tri???u")
        result.add("7 - 10 tri???u")
        result.add("10 - 12 tri???u")
        result.add("12 - 15 tri???u")
        result.add("15 - 20 tri???u")
        result.add("20 - 25 tri???u")
        result.add("25 - 30 tri???u")
        result.add("Tr??n 30 tri???u")
        result.add("Th???a thu???n")

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
                    } else {
                        openCamera()
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
    private lateinit var adapter: ItemProfessionAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private var dataBirthYear: ArrayList<Int> = arrayListOf()
    private var dataGender: ArrayList<String> = arrayListOf()
    private var dataExperience: ArrayList<String> = arrayListOf()
    private var dataCurrentProfession: ArrayList<Profession> = arrayListOf()
    private var currentProfession: ProfessionDB? = null
    private var dataSalary: ArrayList<String> = arrayListOf()

    private var mPrefs: Prefs? = null
    private var account: UserDB? = null
    private var professions: ArrayList<Profession> = arrayListOf()
    private var loadingDialog: LoadingDialog? = null
    private var uriCamera: Uri? = null
    private var isChangeAvatar: Boolean = false
    private var isDeleteAvatar: Boolean = false

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