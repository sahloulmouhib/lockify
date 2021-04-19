package eniso.ia.lockify

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.profile_fragment.*
import kotlin.system.exitProcess

class HomeFragment: Fragment(R.layout.home_fragment) {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return super.onCreateView(inflater, container, savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var notificationImage= true
        ivNotifications.setOnClickListener {
            if(notificationImage)
            {
                ivNotifications.setImageResource(R.drawable.ic_baseline_notifications_off_24);
                Toast.makeText(activity,"Notifications are off",Toast.LENGTH_LONG).show()
                notificationImage = false
            }
            else
            {
                ivNotifications.setImageResource(R.drawable.ic_notifications);
                Toast.makeText(activity,"Notifications are on",Toast.LENGTH_LONG).show()
                notificationImage = true
            }

        }


        super.onViewCreated(view, savedInstanceState)
    }
}