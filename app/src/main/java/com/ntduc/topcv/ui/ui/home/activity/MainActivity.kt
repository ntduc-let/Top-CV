package com.ntduc.topcv.ui.ui.home.activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ntduc.contextutils.inflater
import com.ntduc.toastutils.shortToast
import com.ntduc.topcv.R
import com.ntduc.topcv.databinding.ActivityMainBinding
import com.ntduc.topcv.ui.data.model.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(inflater)
        setContentView(binding.root)

        init()

//        addDATA()
//        addJOB()
    }

    private fun init() {
        initView()
        initData()
    }

    private fun initData() {
        val userDB = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(KEY_USER_DB, UserDB::class.java)
        } else {
            intent.getParcelableExtra(KEY_USER_DB) as UserDB?
        }

        if (userDB != null) {
            viewModel.userDB.value = userDB
        }
    }

    private fun initView() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration =
            AppBarConfiguration.Builder(
                R.id.jobFragment,
                R.id.CVFragment,
                R.id.profileFragment
            ).build()
        setupWithNavController(binding.bnvMain, navController)

        viewModel = ViewModelProvider(this)[MainActivityVM::class.java]
    }

    override fun onSupportNavigateUp(): Boolean {
        return (NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp())
    }

    var time = 0L
    override fun onBackPressed() {
        val backStackEntryCount = navHostFragment.childFragmentManager.backStackEntryCount
        if (backStackEntryCount > 0) {
            if (!navController.popBackStack()) {
                finish()
            }
            return
        } else {
            if (System.currentTimeMillis() - time < 2000) {
                finish()
            } else {
                shortToast(R.string.press_again_to_exit)
                time = System.currentTimeMillis()
            }
        }
    }

    companion object {
        const val KEY_USER_DB = "KEY_USER_DB"
        const val KEY_USER_CV = "KEY_USER_CV"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityVM
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private fun addDATA() {
        val db = Firebase.firestore
        val professionsDB = ProfessionsDB()
        professionsDB.professions!!.add(ProfessionDB("1", "Kinh doanh / Bán hàng"))
        professionsDB.professions!!.add(ProfessionDB("2", "Biên / Phiên dịch"))
        professionsDB.professions!!.add(ProfessionDB("3", "Báo chí / Truyền hình"))
        professionsDB.professions!!.add(ProfessionDB("4", "Bưu chính - Viễn thông"))
        professionsDB.professions!!.add(ProfessionDB("5", "Bảo hiểm"))
        professionsDB.professions!!.add(ProfessionDB("6", "Bất động sản"))
        professionsDB.professions!!.add(ProfessionDB("7", "Chứng khoán / Vàng / Ngoại tệ"))
        professionsDB.professions!!.add(ProfessionDB("8", "Công nghệ cao"))
        professionsDB.professions!!.add(ProfessionDB("9", "Cơ khí / Chế tạo / Tự động hóa"))
        professionsDB.professions!!.add(ProfessionDB("10", "Du lịch"))
        professionsDB.professions!!.add(ProfessionDB("11", "Dầu khí / Hóa chất"))
        professionsDB.professions!!.add(ProfessionDB("12", "Dệt may / Da giày"))
        professionsDB.professions!!.add(ProfessionDB("13", "Dịch vụ khách hàng"))
        professionsDB.professions!!.add(ProfessionDB("14", "Điện tử viễn thông"))
        professionsDB.professions!!.add(ProfessionDB("15", "Điện / Điện tử / Điện lạnh"))
        professionsDB.professions!!.add(ProfessionDB("16", "Giáo dục / Đào tạo"))
        professionsDB.professions!!.add(ProfessionDB("17", "Hóa học / Sinh học"))
        professionsDB.professions!!.add(ProfessionDB("18", "Hoạch định / Dự án"))
        professionsDB.professions!!.add(ProfessionDB("19", "Hàng gia dụng"))
        professionsDB.professions!!.add(ProfessionDB("20", "Hàng hải"))
        professionsDB.professions!!.add(ProfessionDB("21", "Hàng không"))
        professionsDB.professions!!.add(ProfessionDB("22", "Hành chính / Văn phòng"))
        professionsDB.professions!!.add(ProfessionDB("23", "In ấn / Xuất bản"))
        professionsDB.professions!!.add(ProfessionDB("24", "IT Phần cứng / Mạng"))
        professionsDB.professions!!.add(ProfessionDB("25", "IT Phần mềm"))
        professionsDB.professions!!.add(ProfessionDB("26", "Khách sạn / Nhà hàng"))
        professionsDB.professions!!.add(ProfessionDB("27", "Kế toán / Kiểm toán"))
        professionsDB.professions!!.add(ProfessionDB("28", "Marketing / Truyền thông / Quảng cáo"))
        professionsDB.professions!!.add(ProfessionDB("29", "Môi trường / Xử lý chất thải"))
        professionsDB.professions!!.add(ProfessionDB("30", "Mỹ phẩm / Trang sức"))
        professionsDB.professions!!.add(ProfessionDB("31", "Mỹ thuật / Nghệ thuật / Điện ảnh"))
        professionsDB.professions!!.add(ProfessionDB("32", "Ngân hàng / Tài chính"))
        professionsDB.professions!!.add(ProfessionDB("33", "Nhân sự"))
        professionsDB.professions!!.add(ProfessionDB("34", "Nông / Lâm / Ngư nghiệp"))
        professionsDB.professions!!.add(ProfessionDB("35", "Luật / Pháp lý"))
        professionsDB.professions!!.add(ProfessionDB("36", "Quản lý chất lượng (QA/QC)"))
        professionsDB.professions!!.add(ProfessionDB("37", "Quản lý điều hành"))
        professionsDB.professions!!.add(ProfessionDB("38", "Thiết kế đồ họa"))
        professionsDB.professions!!.add(ProfessionDB("39", "Thời trang"))
        professionsDB.professions!!.add(ProfessionDB("40", "Thực phẩm / Đồ uống"))
        professionsDB.professions!!.add(ProfessionDB("41", "Tư vấn"))
        professionsDB.professions!!.add(ProfessionDB("42", "Tổ chức sự kiện / Quà tặng"))
        professionsDB.professions!!.add(ProfessionDB("43", "Vận tải / Kho vận"))
        professionsDB.professions!!.add(ProfessionDB("44", "Logistics"))
        professionsDB.professions!!.add(ProfessionDB("45", "Xuất nhập khẩu"))
        professionsDB.professions!!.add(ProfessionDB("46", "Xây dựng"))
        professionsDB.professions!!.add(ProfessionDB("47", "Y tế / Dược"))
        professionsDB.professions!!.add(ProfessionDB("48", "Công nghệ Ô tô"))
        professionsDB.professions!!.add(ProfessionDB("49", "An toàn lao động"))
        professionsDB.professions!!.add(ProfessionDB("50", "Bán hàng kỹ thuật"))
        professionsDB.professions!!.add(ProfessionDB("51", "Bán lẻ / bán sỉ"))
        professionsDB.professions!!.add(ProfessionDB("52", "Bảo trì / Sửa chữa"))
        professionsDB.professions!!.add(ProfessionDB("53", "Dược phẩm / Công nghệ sinh học"))
        professionsDB.professions!!.add(ProfessionDB("54", "Địa chất / Khoáng sản"))
        professionsDB.professions!!.add(ProfessionDB("55", "Hàng cao cấp"))
        professionsDB.professions!!.add(ProfessionDB("56", "Hàng tiêu dùng"))
        professionsDB.professions!!.add(ProfessionDB("57", "Kiến trúc"))
        professionsDB.professions!!.add(ProfessionDB("58", "Phi chính phủ / Phi lợi nhuận"))
        professionsDB.professions!!.add(ProfessionDB("59", "Sản phầm công nghiệp"))
        professionsDB.professions!!.add(ProfessionDB("60", "Sản xuất"))
        professionsDB.professions!!.add(ProfessionDB("61", "Tài chính / Đầu tư"))
        professionsDB.professions!!.add(ProfessionDB("62", "Thiết kế nội thất"))
        professionsDB.professions!!.add(ProfessionDB("63", "Thư ký / Trợ lý"))
        professionsDB.professions!!.add(ProfessionDB("64", "Spa / Làm đẹp"))
        professionsDB.professions!!.add(ProfessionDB("65", "Công nghệ thông tin"))
        professionsDB.professions!!.add(ProfessionDB("66", "NGO / Phi chính phủ / Phi lợi nhuận"))
        professionsDB.professions!!.add(ProfessionDB("67", "Ngành nghề khác"))

        db.collection("top_cv_global").document("profession_global").set(professionsDB)
    }

    private fun addJOB() {
        val db = Firebase.firestore
        for (i in 1..100){
            val jobGlobal = JobGlobal()
            jobGlobal.id = i.toString()

            val infoJobGlobal = InfoJobGlobal()
            infoJobGlobal.salary = "Thỏa thuận"
            infoJobGlobal.number = "1 người"
            infoJobGlobal.gender = "Nam"
            infoJobGlobal.experience = "Chưa có kinh nghiệm"
            infoJobGlobal.position = "Kinh doanh / Bán hàng"
            infoJobGlobal.address = "Hà Nội, Tòa Osaka Complex, Ngõ 48 Ngọc Hồi, Hoàng Liệt, Hoàng Mai"
            infoJobGlobal.description = "Thường xuyên tiếp xúc, chăm sóc khách hàng, nắm rõ các thông tin của khách hàng.\n" +
                    "Tiếp nhận các phản ánh và yêu cầu của khách hàng về sản phẩm của công ty, công tác giao hàng....\n" +
                    "Báo cáo với Trưởng phòng về những khiếu nại và nhu cầu dịch vụ của khách hàng để tìm ra hướng giải quyết; duy trì và phát triển các mối quan hệ với khách hàng.\n" +
                    "Tư vấn các giải pháp thiết bị y tế toàn diện phù hợp với điều kiện từng khách hàng.\n" +
                    "Hiểu rõ công năng, tác dụng, giá, ưu nhược điểm của sản phẩm, sản phẩm tương tự, sản phẩm của đối thủ cạnh tranh.\n" +
                    "Nhận một phần thị trường của công ty, đảm bảo doanh số cam kết cũng như chịu trách nhiệm quản lý thị trường đó.\n" +
                    "Thực hiện các báo cáo kinh doanh theo ngày, tuần tháng và theo yêu cầu của cấp trên Tìm hiểu và nắm rõ các sản phẩm thiết bị Công ty cung cấp."
            infoJobGlobal.request = "Kỹ năng làm việc độc lập và theo nhóm\n" +
                    "Có ý thức trách nhiệm trong công việc\n" +
                    "Nhanh nhẹn trung thực, cẩn thận\n" +
                    "Sử dụng cơ bản MS office Có tinh thần cầu tiến và cầu thị"
            infoJobGlobal.benefit = "Định kỳ tăng lương 2 lần/năm\n" +
                    "Được đóng BHXH sau 2 tháng thử việc\n" +
                    "Hưởng các quyền lợi của nhân viên như du lịch, khám sức khỏe, sinh nhật, lễ tết....\n" +
                    "Đào tạo về sản phẩm trong nước và quốc tế"
            jobGlobal.infoJobGlobal = infoJobGlobal

            val infoCompanyGlobal = InfoCompanyGlobal()
            infoCompanyGlobal.src = ""
            infoCompanyGlobal.name = "Công ty Cổ phần vật tư và trang thiết bị y tế Cường An"
            infoCompanyGlobal.address = "Số 7, ngách 111/22, Cù Chính Lan, P. Khương Mai, Q. Thanh Xuân, TP. Hà Nội"
            infoCompanyGlobal.website = "google.com.vn"
            infoCompanyGlobal.description = "Công Ty CP Vật Tư Và Trang Thiết Bị Y Tế CƯỜNG AN\n" +
                    "Số 7, ngách 111/22, Cù Chính Lan, P. Khương Mai, Q. Thanh Xuân, TP. Hà Nội Tên người liên hệ: Phòng Nhân Sự\n" +
                    "Qui mô công ty: 30 - 50\n" +
                    "Công ty Cổ Phần Vật Tư Và Trang Thiết Bị Y Tế Cường An được thành lập ngày 26/06/2013 bởi những người có nhiều kinh nghiệm trong ngành Y Tế và Chăm sóc sức khỏe. Với mục tiêu đầu tư và cung cấp các giải pháp y tế, công nghệ hàng đầu với giá trị cộng đồng cao nhất, Cường An đã và đang khẳng định vị thế của mình trong ngành Y Tế Việt Nam.\n" +
                    "Đến năm 2020, Cường An phấn đấu trở thành 1 trong 50 Công ty đứng đầu khu vực ASEAN trong lĩnh vực cung cấp giải pháp y tế toàn diện, bao gồm hệ thống nhũ ảnh, hệ thống nội soi, hệ thống X-quang và các vật tư đi kèm. Trở thành tổ chức có tinh thần đoàn kết sâu đậm, tinh thần học hỏi mạnh mẽ, tinh thần cầu tiến tích cực và sự yêu thương đùm bọc lẫn nhau dựa trên những tôn chỉ hoạt động và văn hóa."
            jobGlobal.infoCompanyGlobal = infoCompanyGlobal

            db.collection("top_cv_global").document("job_global_$i").set(jobGlobal)
        }
    }
}