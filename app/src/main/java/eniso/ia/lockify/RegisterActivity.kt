package eniso.ia.lockify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {


    val personCollectionRef= Firebase.firestore.collection("persons")
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = FirebaseAuth.getInstance()

        //Go back button
        ivGoBack.setOnClickListener {
            finish()
        }

        //Register user
        btnRegister.setOnClickListener{
            registerUser()

      }
    }


    //Register User
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
            val firstName= edFirstName.text.toString()
            val lastName=edLastName.text.toString()
            val person= Person(firstName, lastName)
            savePerson(person)

        }
    }

    //Save person
    private fun savePerson(person: Person)= CoroutineScope(Dispatchers.IO).launch{
        try{
            val currentUser = auth.currentUser.uid

            //personCollectionRef.add(person)
            personCollectionRef.document(currentUser).set(person).await()

            withContext(Dispatchers.Main){
                Toast.makeText(
                        applicationContext,
                        "Successfully saved data",
                        Toast.LENGTH_SHORT
                ).show()
                Intent(applicationContext, MainActivity::class.java).also {
                    startActivity(it)
                }
            }
        } catch (e: Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }




}