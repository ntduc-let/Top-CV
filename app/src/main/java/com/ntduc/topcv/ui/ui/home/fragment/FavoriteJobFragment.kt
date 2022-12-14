package com.ntduc.topcv.ui.ui.home.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.ntduc.toastutils.shortToast
import com.ntduc.topcv.databinding.FragmentFavoriteJobBinding
import com.ntduc.topcv.ui.data.model.JobGlobal
import com.ntduc.topcv.ui.data.model.ListJobGlobal
import com.ntduc.topcv.ui.ui.dialog.LoadingDialog
import com.ntduc.topcv.ui.ui.home.activity.MainActivity
import com.ntduc.topcv.ui.ui.home.activity.MainActivityVM
import com.ntduc.topcv.ui.ui.info_job.activity.InfoJobActivity
import com.ntduc.topcv.ui.ui.search.adapter.JobSearchAdapter
import com.ntduc.topcv.ui.utils.Prefs

class FavoriteJobFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteJobBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        initView()
        initEvent()
    }

    private fun initEvent() {
        adapter.setOnClickItemListener {
            val intent = Intent(requireContext(), InfoJobActivity::class.java)
            intent.putExtra(MainActivity.KEY_JOB, it)
            clickLauncher.launch(intent)
        }
    }

    private fun initView() {
        db = Firebase.firestore
        mPrefs = Prefs(requireContext())

        viewModel = ViewModelProvider(requireActivity())[MainActivityVM::class.java]
        viewModel.favoriteJob.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.layoutLoading.root.visibility = View.GONE
                binding.rcvList.visibility = View.VISIBLE

                adapter.updateData(it)
            }
        }

        adapter = JobSearchAdapter(requireContext())
        binding.rcvList.adapter = adapter
        binding.rcvList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    private lateinit var binding: FragmentFavoriteJobBinding
    private lateinit var viewModel: MainActivityVM
    private lateinit var adapter: JobSearchAdapter

    private lateinit var db: FirebaseFirestore

    private var listFav: ArrayList<JobGlobal> = arrayListOf()
    private var listApply: ArrayList<JobGlobal> = arrayListOf()
    private var mPrefs: Prefs? = null
    private var loadingDialog: LoadingDialog? = null

    private val clickLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            loadingDialog = LoadingDialog()
            loadingDialog!!.show(requireActivity().supportFragmentManager, "LoadingDialog")

            val favoriteRef =
                db.collection(MainActivity.userDB!!.userInfo!!._id!!)
                    .document("job_favorite")
            favoriteRef.get()
                .addOnSuccessListener { apply ->
                    if (apply != null) {
                        val applys = apply.toObject<ListJobGlobal>()
                        if (applys != null) {
                            viewModel.favoriteJob.value = applys.jobGlobals
                        }
                    }
                    loadingDialog?.dismiss()
                }
                .addOnFailureListener {
                    requireActivity().shortToast("C?? l???i x???y ra, vui l??ng th??? l???i")
                }

            val applyRef =
                db.collection(MainActivity.userDB!!.userInfo!!._id!!)
                    .document("job_apply")
            applyRef.get()
                .addOnSuccessListener { apply ->
                    if (apply != null) {
                        val applys = apply.toObject<ListJobGlobal>()
                        if (applys != null) {
                            viewModel.applyJob.value = applys.jobGlobals
                        }
                    }
                }
                .addOnFailureListener {
                    requireActivity().shortToast("C?? l???i x???y ra, vui l??ng th??? l???i")
                }
        }
}