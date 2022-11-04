package com.ntduc.topcv.ui.ui.home.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.ntduc.toastutils.shortToast
import com.ntduc.topcv.databinding.FragmentCvBinding
import com.ntduc.topcv.ui.data.model.CVDB
import com.ntduc.topcv.ui.data.model.CVsDB
import com.ntduc.topcv.ui.data.model.UserDB
import com.ntduc.topcv.ui.ui.create_cv.activity.CreateCVActivity
import com.ntduc.topcv.ui.ui.home.activity.MainActivity
import com.ntduc.topcv.ui.ui.home.activity.MainActivityVM
import com.ntduc.topcv.ui.ui.home.adapter.CVAdapter
import com.ntduc.topcv.ui.utils.Prefs

class CVFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCvBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        initView()
        initData()
        initEvent()
    }

    private fun initEvent() {
        binding.layoutNoCv.btnCreate.setOnClickListener {
            if (mPrefs!!.isLogin && userDB != null) {
                val intent = Intent(requireContext(), CreateCVActivity::class.java)
                intent.putExtra(MainActivity.KEY_USER_DB, userDB)
                saveCVLauncher.launch(intent)
            } else {
                requireContext().shortToast("Vui lòng đăng nhập để thực hiện được tính năng này")
            }
        }

        binding.layoutListCv.btnAdd.setOnClickListener {
            if (mPrefs!!.isLogin && userDB != null) {
                val intent = Intent(requireContext(), CreateCVActivity::class.java)
                intent.putExtra(MainActivity.KEY_USER_DB, userDB)
                saveCVLauncher.launch(intent)
            } else {
                requireContext().shortToast("Vui lòng đăng nhập để thực hiện được tính năng này")
            }
        }
    }

    private fun initData() {
        mPrefs = Prefs(requireContext())
        db = Firebase.firestore

        viewModel = ViewModelProvider(requireActivity())[MainActivityVM::class.java]
        viewModel.userDB.observe(viewLifecycleOwner) {
            if (it != null) {
                userDB = it
                getDataCV(it)
            } else {
                binding.layoutLoading.root.visibility = View.GONE
                binding.layoutNoCv.root.visibility = View.VISIBLE
                binding.layoutListCv.root.visibility = View.GONE
            }
        }
    }

    private fun getDataCV(userDB: UserDB) {
        val docRef = db.collection(userDB.userInfo!!._id!!).document("cv")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val cVsDB = document.toObject<CVsDB>()
                    if (cVsDB != null) {
                        listCV = cVsDB.listCV
                        binding.layoutLoading.root.visibility = View.GONE

                        if (listCV.isEmpty()) {
                            binding.layoutNoCv.root.visibility = View.VISIBLE
                            binding.layoutListCv.root.visibility = View.GONE

                            adapter.updateData(arrayListOf())
                        } else {
                            binding.layoutNoCv.root.visibility = View.GONE
                            binding.layoutListCv.root.visibility = View.VISIBLE

                            adapter.updateData(listCV)
                        }
                    } else {
                        binding.layoutNoCv.root.visibility = View.VISIBLE
                        binding.layoutListCv.root.visibility = View.GONE

                        adapter.updateData(arrayListOf())
                    }
                } else {
                    requireContext().shortToast("Có lỗi xảy ra, vui lòng thử lại")
                }
            }
            .addOnFailureListener {
                requireContext().shortToast("Có lỗi xảy ra, vui lòng thử lại")
            }
    }

    private fun initView() {
        adapter = CVAdapter(requireContext())
        binding.layoutListCv.rcvList.adapter = adapter
        binding.layoutListCv.rcvList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    companion object{
        var listCV: ArrayList<CVDB> = arrayListOf()
    }

    private lateinit var binding: FragmentCvBinding
    private lateinit var adapter: CVAdapter
    private lateinit var viewModel: MainActivityVM
    private lateinit var db: FirebaseFirestore

    private var mPrefs: Prefs? = null
    private var userDB: UserDB? = null

    private val saveCVLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                if (listCV.isEmpty()) {
                    binding.layoutNoCv.root.visibility = View.VISIBLE
                    binding.layoutListCv.root.visibility = View.GONE

                    adapter.updateData(arrayListOf())
                } else {
                    binding.layoutNoCv.root.visibility = View.GONE
                    binding.layoutListCv.root.visibility = View.VISIBLE

                    adapter.updateData(listCV)
                }
            }
        }
}