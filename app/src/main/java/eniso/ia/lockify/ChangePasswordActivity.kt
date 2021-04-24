package eniso.ia.lockify

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isNotEmpty
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_change_password.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ChangePasswordActivity : AppCompatActivity() {
    lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        auth = FirebaseAuth.getInstance()
        ivGoBack.setOnClickListener{

                finish()

        }

        btnChangePassword.setOnClickListener{
            if (etNewPassword.isNotEmpty() && etOldPassword.isNotEmpty() &&  etOldPassword.editText?.text.toString()==etNewPassword.editText?.text.toString()) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                      auth.currentUser.updatePassword(etNewPassword.editText?.text.toString()).await()

                        withContext(Dispatchers.Main){
                            Toast.makeText(this@ChangePasswordActivity,"passwrod has been reset successfully ",Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main){
                            Toast.makeText(this@ChangePasswordActivity, e.message, Toast.LENGTH_LONG).show()
                        }


                    }
                }
            }
        }


    }
}