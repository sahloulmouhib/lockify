package eniso.ia.lockify

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_lock_and_unlock.*
import kotlinx.android.synthetic.main.profile_fragment.*
import kotlin.system.exitProcess

class ProfileFragment: Fragment(R.layout.profile_fragment) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return super.onCreateView(inflater, container, savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {




        val disconnectDialog = AlertDialog.Builder(activity)
            .setTitle("Disconnect")
            .setMessage("Are you sure you want to disconnect from Lockify ?")
            .setPositiveButton("Yes"){_, _ ->
                Toast.makeText(activity,"you disconeected",Toast.LENGTH_LONG).show()
                FirebaseAuth.getInstance().signOut();
                activity?.finish();
                exitProcess(0);
                //
                }
            .setNegativeButton(" hell No"){_,_ ->
            }.create()

        tvDisconnect.setOnClickListener {
                disconnectDialog.show()
            }


        tvChangePassword.setOnClickListener{
            Intent(activity, ChangePasswordActivity::class.java).also {
                startActivity(it)

            }
        }
        tvChooseLanguage.setOnClickListener{
            Intent(activity, LanguageActivity::class.java).also {
                startActivity(it)

            }
        }





        super.onViewCreated(view, savedInstanceState)
    }

}