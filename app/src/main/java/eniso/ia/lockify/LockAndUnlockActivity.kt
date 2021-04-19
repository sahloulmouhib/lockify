package eniso.ia.lockify

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_lock_and_unlock.*
import kotlinx.android.synthetic.main.bottom_nav_menu.*

class LockAndUnlockActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock_and_unlock)



        btnLock.setOnClickListener{
            Toast.makeText(this, "Door is now locked!", Toast.LENGTH_SHORT).show()
        }
        btnUnlock.setOnClickListener{
            Toast.makeText(this, "Door is now unlocked!", Toast.LENGTH_SHORT).show()
        }
    }



}