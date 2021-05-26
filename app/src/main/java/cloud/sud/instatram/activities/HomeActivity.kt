package cloud.sud.instatram.activities



import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import cloud.sud.instatram.fragments.HomeFragment
import cloud.sud.instatram.R
import cloud.sud.instatram.databinding.ActivityHomeBinding
import cloud.sud.instatram.activities.ImagesActivity.Companion.HOME_OR_MAP_FRAGMENT
import cloud.sud.instatram.fragments.MapsFragment

class HomeActivity : BaseActivity() {

    private lateinit var mBinding:ActivityHomeBinding
    private  var NmbFragment:Int=0
    override fun onCreate(savedInstanceState: Bundle?) {

        checkTheme()
        loadLocate()
        super.onCreate(savedInstanceState)
        mBinding= ActivityHomeBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        val homeFragment= HomeFragment()
        val mapFragment= MapsFragment()

        NmbFragment=intent.getIntExtra(HOME_OR_MAP_FRAGMENT,0)
        if (NmbFragment==1){
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.flfragment, mapFragment)
                commit()
            }
        }else{
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.flfragment, homeFragment)
                commit()
            }
        }

        mBinding.btnHome.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.flfragment, homeFragment)
                commit()
            }
        }

        mBinding.btnMap.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.flfragment, mapFragment)
                commit()
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.itemLanguage -> {
               languageDialog()
            }
            R.id.itemTheme -> {
                themeDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object{
        const val ID_TO_FRAGMENT="id to fragment"
    }
}
