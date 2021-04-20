package eniso.ia.lockify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = FirebaseAuth.getInstance()


        ivGoBack.setOnClickListener {
            finish()
        }
        btnRegister.setOnClickListener{
            registerUser()


      }
    }

    private fun registerUser() {
        val email = etEmailRegister.editText?.text.toString()
        Log.d("lol",email )
        val password = etPasswordRegister.editText?.text.toString()
        Log.d("lol", password)
        if (email.isNotEmpty() && password.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {

                    auth.createUserWithEmailAndPassword(email,password).await()
                    withContext(Dispatchers.Main)
                    {

                        checkLoggedInState()

                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@RegisterActivity, e.message, Toast.LENGTH_LONG).show()
                    }


                }
            }
        }
    }
    private fun checkLoggedInState ()
    {
        if(auth.currentUser == null)
        {
            tvLoggedIn.text="You not registered"
            Log.d("lol", tvLoggedIn.text.toString())
        }
        else
        {
            tvLoggedIn.text="You are  registered"
            Intent(this, BottomNavMenuActivity::class.java).also {
                startActivity(it)
            }
        }
    }
}
