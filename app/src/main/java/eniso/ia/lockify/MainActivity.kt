package eniso.ia.lockify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class MainActivity : AppCompatActivity() {

    lateinit var auth :FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        auth.signOut()
        tvCreateAccount.setOnClickListener {
            Intent(this, RegisterActivity::class.java).also {
                startActivity(it)
            }
        }
        btnLogin.setOnClickListener{
            loginUser()


        }

    }

    override fun onStart() {
        super.onStart()
        checkLoggedInState()
    }

    fun loginUser() {
       val email=etEmailLoggedIn.editText?.text.toString()
        Log.d("lol",email )
        val password = etPasswordLoggedIn.editText?.text.toString()
        Log.d("lol", password)
        if (email.isNotEmpty() && password.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {

                    auth.signInWithEmailAndPassword(email,password).await()
                    withContext(Dispatchers.Main)
                    {
                        checkLoggedInState()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
                    }


                }
            }
        }
    }
      fun checkLoggedInState ()
    {
        if(auth.currentUser == null)
        {
            tvSignedIn.text="You are not logged in"

        }
        else
        {
            tvSignedIn.text="You are logged in"
            Intent(this, BottomNavMenuActivity::class.java).also {
                    startActivity(it)

            }
        }
    }
}