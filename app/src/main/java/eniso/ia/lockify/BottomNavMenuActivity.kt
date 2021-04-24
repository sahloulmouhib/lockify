package eniso.ia.lockify

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.bottom_nav_menu.*

class BottomNavMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.bottom_nav_menu)

        val homeFragment = HomeFragment()
        val profileFragment = ProfileFragment()
        val lockAndUnlockFragment = LockAndUnlockFragment()
        setCurrentFragment(homeFragment)

       bnv.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.miHome -> setCurrentFragment(homeFragment)
                R.id.miLockAndUnlock -> setCurrentFragment(lockAndUnlockFragment)
                R.id.miProfile -> setCurrentFragment(profileFragment)
            }
            true
        }

    }
    private fun setCurrentFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            commit()
        }
}

