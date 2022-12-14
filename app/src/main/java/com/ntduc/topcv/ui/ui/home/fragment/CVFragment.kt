package com.ntduc.topcv.ui.ui.home.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.ntduc.toastutils.shortToast
import com.ntduc.topcv.databinding.FragmentCvBinding
import com.ntduc.topcv.ui.data.model.CVDB
import com.ntduc.topcv.ui.data.model.CVsDB
import com.ntduc.topcv.ui.data.model.UserDB
import com.ntduc.topcv.ui.ui.create_cv.activity.CreateCVActivity
import com.ntduc.topcv.ui.ui.create_cv.activity.EditCVActivity
import com.ntduc.topcv.ui.ui.dialog.LoadingDialog
import com.ntduc.topcv.ui.ui.home.activity.MainActivity
import com.ntduc.topcv.ui.ui.home.activity.MainActivityVM
import com.ntduc.topcv.ui.ui.home.adapter.CVAdapter
import com.ntduc.topcv.ui.ui.home.dialog.DeleteCVDialog
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
                addCVLauncher.launch(intent)
            } else {
                requireContext().shortToast("Vui l??ng ????ng nh???p ????? th???c hi???n ???????c t??nh n??ng n??y")
            }
        }

        binding.layoutListCv.btnAdd.setOnClickListener {
            if (mPrefs!!.isLogin && userDB != null) {
                val intent = Intent(requireContext(), CreateCVActivity::class.java)
                intent.putExtra(MainActivity.KEY_USER_DB, userDB)
                addCVLauncher.launch(intent)
            } else {
                requireContext().shortToast("Vui l??ng ????ng nh???p ????? th???c hi???n ???????c t??nh n??ng n??y")
            }
        }

        adapter.setOnDeleteItemListener {
            val dialog = DeleteCVDialog()
            dialog.setOnDeleteListener {
                deleteCV(it)
            }
            dialog.show(requireActivity().supportFragmentManager, "DeleteCVDialog")
        }

        adapter.setOnClickItemListener {
            val intent = Intent(requireContext(), EditCVActivity::class.java)
            intent.putExtra(MainActivity.KEY_USER_DB, userDB)
            intent.putExtra(MainActivity.KEY_USER_CV, it)
            editCVLauncher.launch(intent)
        }
    }

    private fun initData() {
        mPrefs = Prefs(requireContext())
        db = Firebase.firestore
        storage = Firebase.storage

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

    private fun deleteCV(cvdb: CVDB) {
        loadingDialog = LoadingDialog()
        loadingDialog!!.show(requireActivity().supportFragmentManager, "LoadingDialog")

        listCV.remove(cvdb)
        val cVsDB = CVsDB(listCV = listCV)
        db.collection(userDB!!.userInfo!!._id!!).document("cv")
            .set(cVsDB)
            .addOnSuccessListener {
                Log.d("ntduc_debug", "DocumentSnapshot successfully written!")
                val storageRef = storage.reference
                val avatarRef =
                    storageRef.child("${userDB!!.userInfo!!._id!!}/cv/${cvdb.id}/avatar/avatar.jpg")
                val deleteTask = avatarRef.delete()
                deleteTask.addOnSuccessListener {
                    loadingDialog?.dismiss()
                    requireContext().shortToast("X??a CV th??nh c??ng")

                    if (listCV.isEmpty()) {
                        binding.layoutNoCv.root.visibility = View.VISIBLE
                        binding.layoutListCv.root.visibility = View.GONE

                        adapter.updateData(arrayListOf())
                    } else {
                        binding.layoutNoCv.root.visibility = View.GONE
                        binding.layoutListCv.root.visibility = View.VISIBLE

                        adapter.updateData(listCV)
                    }
                }.addOnFailureListener {
                    loadingDialog?.dismiss()
                    requireContext().shortToast("X??a CV th??nh c??ng")

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
            .addOnFailureListener {
                loadingDialog?.dismiss()
                listCV.add(cvdb)
                requireContext().shortToast("C?? l???i x???y ra. Vui l??ng th??? l???i")
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
                        binding.layoutLoading.root.visibility = View.GONE
                        binding.layoutNoCv.root.visibility = View.VISIBLE
                        binding.layoutListCv.root.visibility = View.GONE

                        adapter.updateData(arrayListOf())
                    }
                } else {
                    requireContext().shortToast("C?? l???i x???y ra, vui l??ng th??? l???i")
                }
            }
            .addOnFailureListener {
                requireContext().shortToast("C?? l???i x???y ra, vui l??ng th??? l???i")
            }
    }

    private fun initView() {
        adapter = CVAdapter(requireContext())
        binding.layoutListCv.rcvList.adapter = adapter
        binding.layoutListCv.rcvList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    companion object {
        var listCV: ArrayList<CVDB> = arrayListOf()
    }

    private lateinit var binding: FragmentCvBinding
    private lateinit var adapter: CVAdapter
    private lateinit var viewModel: MainActivityVM
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private var mPrefs: Prefs? = null
    private var userDB: UserDB? = null
    private var loadingDialog: LoadingDialog? = null

    private val addCVLauncher =
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

    private val editCVLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                adapter.updateData(listCV)
            }
        }
}