package com.ntduc.topcv.ui.ui.create_cv.activity

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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.ntduc.androidimagecropper.CropImage
import com.ntduc.androidimagecropper.CropImageView
import com.ntduc.contextutils.inflater
import com.ntduc.datetimeutils.currentCalendar
import com.ntduc.datetimeutils.getDateTimeFromMillis
import com.ntduc.toastutils.shortToast
import com.ntduc.topcv.BuildConfig
import com.ntduc.topcv.R
import com.ntduc.topcv.databinding.ActivityCreateCvActivityBinding
import com.ntduc.topcv.ui.data.model.*
import com.ntduc.topcv.ui.ui.account.information.dialog.ChooseImageBottomDialog
import com.ntduc.topcv.ui.ui.account.information.dialog.RequestPermissionCameraDialog
import com.ntduc.topcv.ui.ui.account.information.dialog.RequestPermissionReadAllFileDialog
import com.ntduc.topcv.ui.ui.create_cv.adapter.*
import com.ntduc.topcv.ui.ui.create_cv.dialog.*
import com.ntduc.topcv.ui.ui.dialog.LoadingDialog
import com.ntduc.topcv.ui.ui.home.activity.MainActivity
import com.ntduc.topcv.ui.ui.home.fragment.CVFragment
import com.ntduc.topcv.ui.utils.PermissionUtil
import java.io.File
import kotlin.collections.ArrayList

class EditCVActivity : AppCompatActivity(), SkillBottomDialog.Callback,
    ExperienceBottomDialog.Callback, EducationBottomDialog.Callback, WorkBottomDialog.Callback,
    CertificateBottomDialog.Callback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateCvActivityBinding.inflate(inflater)
        setContentView(binding.root)
        binding.txtTitle.text = "Sửa CV"

        init()
    }

    private fun init() {
        initData()
        initView()
        initEvent()
    }

    private fun initEvent() {
        binding.layoutBottom.btnCancel.setOnClickListener {
            onBackPressed()
        }

        binding.layoutBottom.btnSave.setOnClickListener {
            if (binding.txtTitle.text.trim().isEmpty()) {
                shortToast("Tiêu đề CV không được để trống")
            } else {
                updateCV()
            }
        }

        binding.layoutCv.cvAva.setOnClickListener {
            openChooseImageDialog()
        }

        binding.layoutCv.layoutContactInfo.setOnClickListener {
            val dialog = AddContactInfoDialog()
            dialog.setContactInfoCV(cvDB!!.contactInfoCV)
            dialog.setOnSaveListener {
                binding.layoutCv.txtName.text = it.name
                binding.layoutCv.txtPosition.text = it.position
                binding.layoutCv.txtTitleContactInfo.text = it.title
                binding.layoutCv.txtBirthDay.text = "Ngày sinh: ${it.birthDay}"
                binding.layoutCv.txtPhone.text = "Số điện thoại: ${it.phone}"
                binding.layoutCv.txtEmail.text = "Email: ${it.email}"
                binding.layoutCv.txtAddress.text = "Địa chỉ: ${it.address}"
            }
            dialog.show(supportFragmentManager, "AddContactInfoDialog")
        }

        binding.layoutCv.layoutSkill.setOnClickListener {
            val dialog = SkillBottomDialog()
            dialog.setCallback(this)
            dialog.setListSkill(itemSkillAdapter.list)
            dialog.show(supportFragmentManager, "SkillBottomDialog")
        }

        binding.layoutCv.layoutExperience.setOnClickListener {
            val dialog = ExperienceBottomDialog()
            dialog.setCallback(this)
            dialog.setListExperience(itemExperienceAdapter.list)
            dialog.show(supportFragmentManager, "SkillBottomDialog")
        }

        binding.layoutCv.layoutEducation.setOnClickListener {
            val dialog = EducationBottomDialog()
            dialog.setCallback(this)
            dialog.setListEducation(itemEducationAdapter.list)
            dialog.show(supportFragmentManager, "EducationBottomDialog")
        }

        binding.layoutCv.layoutWork.setOnClickListener {
            val dialog = WorkBottomDialog()
            dialog.setCallback(this)
            dialog.setListWork(itemWorkAdapter.list)
            dialog.show(supportFragmentManager, "WorkBottomDialog")
        }

        binding.layoutCv.layoutCertificate.setOnClickListener {
            val dialog = CertificateBottomDialog()
            dialog.setCallback(this)
            dialog.setListCertificate(itemCertificateAdapter.list)
            dialog.show(supportFragmentManager, "CertificateBottomDialog")
        }
    }

    private fun updateCV() {
        loadingDialog = LoadingDialog()
        loadingDialog!!.show(supportFragmentManager, "LoadingDialog")

        cvDB!!.update_at = getDateTimeFromMillis(currentCalendar.timeInMillis, "hh:mm - dd/MM/yyyy")
        cvDB!!.title = binding.edtTitleCv.text.trim().toString()
        CVFragment.listCV.indices.forEach {
            if (CVFragment.listCV[it].id == cvDB!!.id) {
                CVFragment.listCV[it] = cvDB!!
                return@forEach
            }
        }
        val cVsDB = CVsDB(listCV = CVFragment.listCV)
        db.collection(userDB!!.userInfo!!._id!!).document("cv")
            .set(cVsDB)
            .addOnSuccessListener {
                Log.d("ntduc_debug", "DocumentSnapshot successfully written!")
                if (isChangeAvatar) {
                    val storageRef = storage.reference
                    val avatarRef =
                        storageRef.child("${userDB!!.userInfo!!._id!!}/cv/${cvDB!!.id}/avatar/avatar.jpg")
                    if (isDeleteAvatar) {
                        val deleteTask = avatarRef.delete()
                        deleteTask.addOnSuccessListener {
                            loadingDialog?.dismiss()
                            shortToast("Cập nhật CV thành công")

                            val intent = Intent(this, MainActivity::class.java)
                            setResult(RESULT_OK, intent)
                            finish()
                        }.addOnFailureListener {
                            loadingDialog?.dismiss()
                            shortToast("Cập nhật ảnh đại diện thất bại")
                        }
                    } else {
                        val uploadTask = avatarRef.putFile(uriCamera!!)
                        uploadTask.addOnSuccessListener {
                            loadingDialog?.dismiss()
                            shortToast("Cập nhật CV thành công")

                            val intent = Intent(this, MainActivity::class.java)
                            setResult(RESULT_OK, intent)
                            finish()
                        }.addOnFailureListener {
                            loadingDialog?.dismiss()
                            shortToast("Cập nhật ảnh đại diện thất bại")
                            onBackPressed()
                        }
                    }
                } else {
                    loadingDialog?.dismiss()
                    shortToast("Cập nhật CV thành công")

                    val intent = Intent(this, MainActivity::class.java)
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
            .addOnFailureListener { e ->
                loadingDialog?.dismiss()
                shortToast("Có lỗi xảy ra. Vui lòng thử lại")
            }
    }

    private fun initView() {
        binding.edtTitleCv.setText(cvDB!!.title)

        Glide.with(this)
            .load("https://firebasestorage.googleapis.com/v0/b/topcv-androidnc.appspot.com/o/${userDB!!.userInfo!!._id}%2Fcv%2F${cvDB!!.id}%2Favatar%2Favatar.jpg?alt=media")
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .placeholder(R.color.black)
            .error(R.drawable.ic_ava_48dp)
            .into(binding.layoutCv.imgAva)
        binding.layoutCv.txtName.text = cvDB!!.contactInfoCV.name
        binding.layoutCv.txtPosition.text = cvDB!!.contactInfoCV.position
        binding.layoutCv.txtTitleContactInfo.text = cvDB!!.contactInfoCV.title
        binding.layoutCv.txtBirthDay.text = "Ngày sinh: ${cvDB!!.contactInfoCV.birthDay}"
        binding.layoutCv.txtPhone.text = "Số điện thoại: ${cvDB!!.contactInfoCV.phone}"
        binding.layoutCv.txtEmail.text = "Email: ${cvDB!!.contactInfoCV.email}"
        binding.layoutCv.txtAddress.text = "Địa chỉ: ${cvDB!!.contactInfoCV.address}"

        binding.layoutCv.txtTitleCareerGoals.text = cvDB!!.careerGoalsCV.title
        binding.layoutCv.txtContentCareerGoals.text = cvDB!!.careerGoalsCV.content

        binding.layoutCv.txtTitleSkill.text = cvDB!!.skillCV.title
        itemSkillAdapter = ItemSkillAdapter(this, ItemSkillAdapter.MODE_SHOW, cvDB!!.skillCV.skills)
        binding.layoutCv.rcvSkill.adapter = itemSkillAdapter
        binding.layoutCv.rcvSkill.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.layoutCv.txtTitleExperience.text = cvDB!!.experienceCV.title
        itemExperienceAdapter = ItemExperienceAdapter(
            this,
            ItemExperienceAdapter.MODE_SHOW,
            cvDB!!.experienceCV.experiences
        )
        binding.layoutCv.rcvExperience.adapter = itemExperienceAdapter
        binding.layoutCv.rcvExperience.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.layoutCv.txtTitleEducation.text = cvDB!!.educationCV.title
        itemEducationAdapter =
            ItemEducationAdapter(
                this,
                ItemEducationAdapter.MODE_SHOW,
                cvDB!!.educationCV.educations
            )
        binding.layoutCv.rcvEducation.adapter = itemEducationAdapter
        binding.layoutCv.rcvEducation.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.layoutCv.txtTitleWork.text = cvDB!!.workCV.title
        itemWorkAdapter = ItemWorkAdapter(this, ItemWorkAdapter.MODE_SHOW, cvDB!!.workCV.works)
        binding.layoutCv.rcvWork.adapter = itemWorkAdapter
        binding.layoutCv.rcvWork.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.layoutCv.txtTitleCertificate.text = cvDB!!.certificateCV.title
        itemCertificateAdapter = ItemCertificateAdapter(
            this,
            ItemCertificateAdapter.MODE_SHOW,
            cvDB!!.certificateCV.certificates
        )
        binding.layoutCv.rcvCertificate.adapter = itemCertificateAdapter
        binding.layoutCv.rcvCertificate.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun initData() {
        db = Firebase.firestore
        storage = Firebase.storage
        cvDB = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(MainActivity.KEY_USER_CV, CVDB::class.java)
        } else {
            intent.getParcelableExtra(MainActivity.KEY_USER_CV) as CVDB?
        }

        userDB = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(MainActivity.KEY_USER_DB, UserDB::class.java)
        } else {
            intent.getParcelableExtra(MainActivity.KEY_USER_DB) as UserDB?
        }

        if (userDB == null || cvDB == null) {
            shortToast("Đã xảy ra lỗi. Vui lòng thử lại")
            finish()
        }
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
            binding.layoutCv.imgAva.setImageResource(R.drawable.ic_ava_48dp)
        }

        dialog.show(supportFragmentManager, "TakePhotoBottomDialog")
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

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            REQ_CAMERA_CODE
        )
    }

    private fun chooseFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        val uri = Uri.parse(Environment.getDownloadCacheDirectory().path)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.setDataAndType(uri, "image/*")
        selectFileLauncher.launch(intent)
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
            requestPermissions(
                permissions,
                REQUEST_PERMISSION_READ_WRITE
            )
        }
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

    private fun openDialogPermissionCamera() {
        val dialog = RequestPermissionCameraDialog()
        dialog.setOnAllowListener {
            goSetting()
        }
        dialog.show(supportFragmentManager, "RequestPermissionCameraDialog")
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
                    .into(binding.layoutCv.imgAva)
                isChangeAvatar = true
                isDeleteAvatar = false
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                shortToast("Tải ảnh thất bại")
            }
        }
    }

    companion object {
        private const val REQUEST_PERMISSION_READ_WRITE = 300
        private const val REQ_CAMERA_CODE = 400
    }

    private lateinit var binding: ActivityCreateCvActivityBinding
    private lateinit var itemSkillAdapter: ItemSkillAdapter
    private lateinit var itemExperienceAdapter: ItemExperienceAdapter
    private lateinit var itemEducationAdapter: ItemEducationAdapter
    private lateinit var itemWorkAdapter: ItemWorkAdapter
    private lateinit var itemCertificateAdapter: ItemCertificateAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private var cvDB: CVDB? = null
    private var userDB: UserDB? = null
    private var uriCamera: Uri? = null
    private var isChangeAvatar: Boolean = false
    private var isDeleteAvatar: Boolean = false
    private var loadingDialog: LoadingDialog? = null

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

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (PermissionUtil.checkPermissionReadAllFile(this)) {
                chooseFile()
            }
        }

    override fun onUpdateSkill(list: ArrayList<Skill>) {
        itemSkillAdapter.updateData(list)
    }

    override fun onUpdateExperience(list: ArrayList<Experience>) {
        itemExperienceAdapter.updateData(list)
    }

    override fun onUpdateEducation(list: ArrayList<Education>) {
        itemEducationAdapter.updateData(list)
    }

    override fun onUpdateWork(list: ArrayList<Work>) {
        itemWorkAdapter.updateData(list)
    }

    override fun onUpdateCertificate(list: ArrayList<Certificate>) {
        itemCertificateAdapter.updateData(list)
    }
}