package com.ntduc.topcv.ui.ui.search.activity

import android.content.Intent
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
import com.ntduc.topcv.ui.ui.home.activity.MainActivity
import com.ntduc.topcv.ui.ui.info_job.activity.InfoJobActivity
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
                                            name = "T???t c???"
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
                                            shortToast("C?? l???i x???y ra, vui l??ng th??? l???i")
                                        }
                                    }
                                }
                            } else {
                                shortToast("C?? l???i x???y ra, vui l??ng th??? l???i")
                            }
                        }
                        .addOnFailureListener { e ->
                            shortToast("C?? l???i x???y ra, vui l??ng th??? l???i")
                        }
                }
            }
            .addOnFailureListener {
                shortToast("C?? l???i x???y ra. Vui l??ng th??? l???i sau")
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

        adapter.setOnClickItemListener {
            val intent = Intent(this, InfoJobActivity::class.java)
            intent.putExtra(MainActivity.KEY_JOB, it)
            startActivity(intent)
        }
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
        result.add("T???t c???")
        result.add("An Giang")
        result.add("B?? R???a ??? V??ng T??u")
        result.add("B???c Giang")
        result.add("B???c K???n")
        result.add("B???c Li??u")
        result.add("B???c Ninh")
        result.add("B???n Tre")
        result.add("B??nh ?????nh")
        result.add("B??nh D????ng")
        result.add("B??nh Ph?????c")
        result.add("B??nh Thu???n")
        result.add("C?? Mau")
        result.add("C???n Th??")
        result.add("Cao B???ng")
        result.add("???? N???ng")
        result.add("?????k L???k")
        result.add("?????k N??ng")
        result.add("??i???n Bi??n")
        result.add("?????ng Nai")
        result.add("?????ng Th??p")
        result.add("Gia Lai")
        result.add("H?? Giang")
        result.add("H?? Nam")
        result.add("H?? N???i")
        result.add("H?? T??nh")
        result.add("H???i D????ng")
        result.add("H???i Ph??ng")
        result.add("H???u Giang")
        result.add("H??a B??nh")
        result.add("H??ng Y??n")
        result.add("Kh??nh H??a")
        result.add("Ki??n Giang")
        result.add("Kon Tum")
        result.add("Lai Ch??u")
        result.add("L??m ?????ng")
        result.add("L???ng S??n")
        result.add("L??o Cai")
        result.add("Long An")
        result.add("Nam ?????nh")
        result.add("Ngh??? An")
        result.add("Ninh B??nh")
        result.add("Ninh Thu???n")
        result.add("Ph?? Th???")
        result.add("Qu???ng B??nh")
        result.add("Qu???ng Nam")
        result.add("Qu???ng Ng??i")
        result.add("Qu???ng Ninh")
        result.add("Qu???ng Tr???")
        result.add("S??c Tr??ng")
        result.add("S??n La")
        result.add("T??y Ninh")
        result.add("Th??i B??nh")
        result.add("Th??i Nguy??n")
        result.add("Thanh H??a")
        result.add("Th???a Thi??n Hu???")
        result.add("Ti???n Giang")
        result.add("TP H??? Ch?? Minh")
        result.add("Tr?? Vinh")
        result.add("Tuy??n Quang")
        result.add("V??nh Long")
        result.add("V??nh Ph??c")
        result.add("Y??n B??i")

        return result
    }

    private fun getDataExperience(): ArrayList<String> {
        val result = arrayListOf<String>()
        result.add("T???t c???")
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
        result.add("T???t c???")
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