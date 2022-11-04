package com.ntduc.topcv.ui.ui.create_cv.activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ntduc.contextutils.inflater
import com.ntduc.datetimeutils.currentCalendar
import com.ntduc.toastutils.shortToast
import com.ntduc.topcv.R
import com.ntduc.topcv.databinding.ActivityCreateCvActivityBinding
import com.ntduc.topcv.ui.data.model.*
import com.ntduc.topcv.ui.ui.create_cv.adapter.*
import com.ntduc.topcv.ui.ui.create_cv.dialog.*
import com.ntduc.topcv.ui.ui.home.activity.MainActivity

class CreateCVActivity : AppCompatActivity(), SkillBottomDialog.Callback,
    ExperienceBottomDialog.Callback, EducationBottomDialog.Callback, WorkBottomDialog.Callback,
    CertificateBottomDialog.Callback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateCvActivityBinding.inflate(inflater)
        setContentView(binding.root)

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
//            val intent = Intent(this, MainActivity::class.java)
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

    private fun initView() {
        binding.edtTitleCv.setText(cvDB.title)

        Glide.with(this)
            .load("https://firebasestorage.googleapis.com/v0/b/topcv-androidnc.appspot.com/o/${userDB!!.userInfo!!._id}%2Fcv%2F${cvDB.id}%2Favatar%2Favatar.jpg?alt=media")
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .placeholder(R.color.black)
            .error(R.drawable.ic_ava_48dp)
            .into(binding.layoutCv.imgAva)
        binding.layoutCv.txtName.text = cvDB.contactInfoCV.name
        binding.layoutCv.txtPosition.text = cvDB.contactInfoCV.position
        binding.layoutCv.txtTitleContactInfo.text = cvDB.contactInfoCV.title
        binding.layoutCv.txtBirthDay.text = "Ngày sinh: ${cvDB.contactInfoCV.birthDay}"
        binding.layoutCv.txtPhone.text = "Số điện thoại: ${cvDB.contactInfoCV.phone}"
        binding.layoutCv.txtEmail.text = "Email: ${cvDB.contactInfoCV.email}"
        binding.layoutCv.txtAddress.text = "Địa chỉ: ${cvDB.contactInfoCV.address}"

        binding.layoutCv.txtTitleCareerGoals.text = cvDB.careerGoalsCV.title
        binding.layoutCv.txtContentCareerGoals.text = cvDB.careerGoalsCV.content

        binding.layoutCv.txtTitleSkill.text = cvDB.skillCV.title
        itemSkillAdapter = ItemSkillAdapter(this, ItemSkillAdapter.MODE_SHOW, cvDB.skillCV.skills)
        binding.layoutCv.rcvSkill.adapter = itemSkillAdapter
        binding.layoutCv.rcvSkill.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.layoutCv.txtTitleExperience.text = cvDB.experienceCV.title
        itemExperienceAdapter = ItemExperienceAdapter(this, ItemExperienceAdapter.MODE_SHOW, cvDB.experienceCV.experiences)
        binding.layoutCv.rcvExperience.adapter = itemExperienceAdapter
        binding.layoutCv.rcvExperience.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.layoutCv.txtTitleEducation.text = cvDB.educationCV.title
        itemEducationAdapter = ItemEducationAdapter(this, ItemEducationAdapter.MODE_SHOW, cvDB.educationCV.educations)
        binding.layoutCv.rcvEducation.adapter = itemEducationAdapter
        binding.layoutCv.rcvEducation.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.layoutCv.txtTitleWork.text = cvDB.workCV.title
        itemWorkAdapter = ItemWorkAdapter(this, ItemWorkAdapter.MODE_SHOW, cvDB.workCV.works)
        binding.layoutCv.rcvWork.adapter = itemWorkAdapter
        binding.layoutCv.rcvWork.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.layoutCv.txtTitleCertificate.text = cvDB.certificateCV.title
        itemCertificateAdapter = ItemCertificateAdapter(this, ItemCertificateAdapter.MODE_SHOW, cvDB.certificateCV.certificates)
        binding.layoutCv.rcvCertificate.adapter = itemCertificateAdapter
        binding.layoutCv.rcvCertificate.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun initData() {
        cvDB = CVDB(id = currentCalendar.timeInMillis.toString())
        userDB = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(MainActivity.KEY_USER_DB, UserDB::class.java)
        } else {
            intent.getParcelableExtra(MainActivity.KEY_USER_DB) as UserDB?
        }

        if (userDB == null){
            shortToast("Đã xảy ra lỗi. Vui lòng thử lại")
            finish()
        }
    }

    private lateinit var binding: ActivityCreateCvActivityBinding
    private lateinit var cvDB: CVDB
    private lateinit var itemSkillAdapter: ItemSkillAdapter
    private lateinit var itemExperienceAdapter: ItemExperienceAdapter
    private lateinit var itemEducationAdapter: ItemEducationAdapter
    private lateinit var itemWorkAdapter: ItemWorkAdapter
    private lateinit var itemCertificateAdapter: ItemCertificateAdapter
    //    private lateinit var db: FirebaseFirestore

    private var userDB: UserDB? = null

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