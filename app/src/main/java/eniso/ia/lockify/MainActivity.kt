package eniso.ia.lockify

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class MainActivity : AppCompatActivity() {


    lateinit var auth :FirebaseAuth
    val personCollectionRef= Firebase.firestore.collection("persons")




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

    private fun  loginUser() {
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
      private fun checkLoggedInState ()
    {
        if(auth.currentUser == null)
        {

            tvSignedIn.text="You are not logged in"

        }
        else
        {
            tvSignedIn.text="You are logged in"
            retrievePersons()


        }
    }


   private fun retrievePersons() = CoroutineScope(Dispatchers.IO).launch {

        try {
            val currentuser = FirebaseAuth.getInstance().currentUser.uid
            //querySnapshot is the query result after every task we call await()
            val querySnapshot = personCollectionRef.get().await()
            val firstName = StringBuilder()
            val lastName=StringBuilder()
            for(document in querySnapshot.documents) {
                if(document.id==currentuser) {
                    val person = document.toObject<Person>()
                    if(person!=null)
                    {
                        firstName.append("${person.firstName}")
                        lastName.append("${person.lastName}")
                    }
                    withContext(Dispatchers.Main) {
                        val firstNamePref=firstName.toString().capitalize()
                        val lastNamePref=lastName.toString().capitalize()
                        val sessionManager=SessionManager(applicationContext);
                        sessionManager.createLoginSession(firstNamePref,lastNamePref)
                        Intent(applicationContext, BottomNavMenuActivity::class.java).also {
                            startActivity(it)

                        }

                    }
                }

            }

        } catch(e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
            }
        }

    }
}