package com.ntduc.topcv.ui.ui.search.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.ntduc.contextutils.inflater
import com.ntduc.toastutils.shortToast
import com.ntduc.topcv.databinding.ActivitySearchJobBinding
import com.ntduc.topcv.ui.data.model.JobGlobal
import com.ntduc.topcv.ui.data.model.ProfessionDB
import com.ntduc.topcv.ui.data.model.ProfessionsDB
import com.ntduc.topcv.ui.ui.search.adapter.JobSearchAdapter
import com.ntduc.topcv.ui.ui.search.dialog.FilterDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchJobActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchJobBinding.inflate(inflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        initView()
        initData()
        initEvent()
    }

    private fun initView() {
        adapter = JobSearchAdapter(this)
        binding.rcvList.adapter = adapter
        binding.rcvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun initData() {
        db = Firebase.firestore
        db.collection("top_cv_global")
            .get()
            .addOnSuccessListener { result ->
                listJob = arrayListOf()
                lifecycleScope.launch(Dispatchers.IO) {
                    for (document in result) {
                        val job = document.toObject<JobGlobal>()
                        if (job.id.isNotEmpty()) {
                            listJob.add(job)
                        }
                    }

                    val docRef = db.collection("top_cv_global").document("profession_global")
                    docRef.get()
                        .addOnSuccessListener { document ->
                            if (document != null) {
                                lifecycleScope.launch(Dispatchers.IO) {
                                    val professionsDB = document.toObject<ProfessionsDB>()

                                    if (professionsDB != null) {
                                        professions = professionsDB.professions
                                        val defaultProfession = ProfessionDB(
                                            _id = "0",
                                            name = "Tất cả"
                                        )
                                        professions.add(0, defaultProfession)
                                        dataExperience = getDataExperience()
                                        dataLocation = getDataLocation()
                                        dataSalary = getDataSalary()

                                        withContext(Dispatchers.Main) {
                                            withContext(Dispatchers.Main) {
                                                binding.layoutLoading.root.visibility = View.GONE
                                                binding.txtNoItem.visibility = View.GONE
                                                binding.rcvList.visibility = View.VISIBLE
                                            }
                                        }
                                    } else {
                                        withContext(Dispatchers.Main) {
                                            shortToast("Có lỗi xảy ra, vui lòng thử lại")
                                        }
                                    }
                                }
                            } else {
                                shortToast("Có lỗi xảy ra, vui lòng thử lại")
                            }
                        }
                        .addOnFailureListener { e ->
                            shortToast("Có lỗi xảy ra, vui lòng thử lại")
                        }
                }
            }
            .addOnFailureListener {
                shortToast("Có lỗi xảy ra. Vui lòng thử lại sau")
            }
    }

    private fun initEvent() {
        binding.toolbar.btnClose.setOnClickListener {
            onBackPressed()
        }

        binding.toolbar.btnFilter.setOnClickListener {
            val dialog = FilterDialog()
            dialog.setDataLocation(dataLocation, positionLocation)
            dialog.setDataProfession(professions, positionProfession)
            dialog.setDataSalary(dataSalary, positionSalary)
            dialog.setDataExperience(dataExperience, positionExperience)
            dialog.setOnApplyListener { location, profession, salary, experience ->
                positionLocation = location
                positionProfession = profession
                positionSalary = salary
                positionExperience = experience

                if (binding.toolbar.edtSearch.text.isEmpty()) {
                    adapter.updateData(arrayListOf())
                } else {
                    filter(binding.toolbar.edtSearch.text.toString())
                }
            }
            dialog.show(supportFragmentManager, "FilterDialog")
        }

        binding.toolbar.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(arg0: Editable) {}
            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}

            override fun onTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                if (arg0.isEmpty()) {
                    adapter.updateData(arrayListOf())
                } else {
                    filter(arg0.toString())
                }
            }
        })
    }

    private fun filter(text: String) {
        binding.layoutLoading.root.visibility = View.VISIBLE
        binding.txtNoItem.visibility = View.GONE
        binding.rcvList.visibility = View.GONE

        lifecycleScope.launch(Dispatchers.IO) {
            var result = listJob.filter {
                it.infoJobGlobal.position.contains(text, true)
                        || it.infoCompanyGlobal.name.contains(text, true)
            }
            if (positionLocation != 0){
                result = result.filter {
                    it.infoJobGlobal.address.contains(dataLocation[positionLocation], true)
                }
            }
            if (positionProfession != 0){
                result = result.filter {
                    it.infoJobGlobal.position.contains(professions[positionProfession].name!!, true)
                }
            }
            if (positionSalary != 0){
                result = result.filter {
                    it.infoJobGlobal.salary.contains(dataSalary[positionSalary], true)
                }
            }
            if (positionExperience != 0){
                result = result.filter {
                    it.infoJobGlobal.experience.contains(dataExperience[positionExperience], true)
                }
            }
            withContext(Dispatchers.Main) {
                if (result.isEmpty()) {
                    binding.layoutLoading.root.visibility = View.GONE
                    binding.txtNoItem.visibility = View.VISIBLE
                    binding.rcvList.visibility = View.GONE
                } else {
                    binding.layoutLoading.root.visibility = View.GONE
                    binding.txtNoItem.visibility = View.GONE
                    binding.rcvList.visibility = View.VISIBLE

                    adapter.updateData(result)
                }
            }
        }
    }

    private fun getDataLocation(): ArrayList<String> {
        val result = arrayListOf<String>()
        result.add("Tất cả")
        result.add("An Giang")
        result.add("Bà Rịa – Vũng Tàu")
        result.add("Bắc Giang")
        result.add("Bắc Kạn")
        result.add("Bạc Liêu")
        result.add("Bắc Ninh")
        result.add("Bến Tre")
        result.add("Bình Định")
        result.add("Bình Dương")
        result.add("Bình Phước")
        result.add("Bình Thuận")
        result.add("Cà Mau")
        result.add("Cần Thơ")
        result.add("Cao Bằng")
        result.add("Đà Nẵng")
        result.add("Đắk Lắk")
        result.add("Đắk Nông")
        result.add("Điện Biên")
        result.add("Đồng Nai")
        result.add("Đồng Tháp")
        result.add("Gia Lai")
        result.add("Hà Giang")
        result.add("Hà Nam")
        result.add("Hà Nội")
        result.add("Hà Tĩnh")
        result.add("Hải Dương")
        result.add("Hải Phòng")
        result.add("Hậu Giang")
        result.add("Hòa Bình")
        result.add("Hưng Yên")
        result.add("Khánh Hòa")
        result.add("Kiên Giang")
        result.add("Kon Tum")
        result.add("Lai Châu")
        result.add("Lâm Đồng")
        result.add("Lạng Sơn")
        result.add("Lào Cai")
        result.add("Long An")
        result.add("Nam Định")
        result.add("Nghệ An")
        result.add("Ninh Bình")
        result.add("Ninh Thuận")
        result.add("Phú Thọ")
        result.add("Quảng Bình")
        result.add("Quảng Nam")
        result.add("Quảng Ngãi")
        result.add("Quảng Ninh")
        result.add("Quảng Trị")
        result.add("Sóc Trăng")
        result.add("Sơn La")
        result.add("Tây Ninh")
        result.add("Thái Bình")
        result.add("Thái Nguyên")
        result.add("Thanh Hóa")
        result.add("Thừa Thiên Huế")
        result.add("Tiền Giang")
        result.add("TP Hồ Chí Minh")
        result.add("Trà Vinh")
        result.add("Tuyên Quang")
        result.add("Vĩnh Long")
        result.add("Vĩnh Phúc")
        result.add("Yên Bái")

        return result
    }

    private fun getDataExperience(): ArrayList<String> {
        val result = arrayListOf<String>()
        result.add("Tất cả")
        result.add("Chưa có kinh nghiệm")
        result.add("Dưới 1 năm")
        result.add("1 năm")
        result.add("2 năm")
        result.add("3 năm")
        result.add("4 năm")
        result.add("5 năm")
        result.add("Trên 5 năm")

        return result
    }

    private fun getDataSalary(): ArrayList<String> {
        val result = arrayListOf<String>()
        result.add("Tất cả")
        result.add("Dưới 3 triệu")
        result.add("3 - 5 triệu")
        result.add("5 - 7 triệu")
        result.add("7 - 10 triệu")
        result.add("10 - 12 triệu")
        result.add("12 - 15 triệu")
        result.add("15 - 20 triệu")
        result.add("20 - 25 triệu")
        result.add("25 - 30 triệu")
        result.add("Trên 30 triệu")
        result.add("Thỏa thuận")

        return result
    }

    private lateinit var binding: ActivitySearchJobBinding
    private lateinit var adapter: JobSearchAdapter
    private lateinit var db: FirebaseFirestore

    private var listJob: ArrayList<JobGlobal> = arrayListOf()

    private var professions: ArrayList<ProfessionDB> = arrayListOf()
    private var dataSalary: ArrayList<String> = arrayListOf()
    private var dataExperience: ArrayList<String> = arrayListOf()
    private var dataLocation: ArrayList<String> = arrayListOf()

    private var positionLocation = 0
    private var positionProfession = 0
    private var positionSalary = 0
    private var positionExperience = 0
}