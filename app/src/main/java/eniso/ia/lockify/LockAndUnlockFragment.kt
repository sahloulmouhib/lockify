package eniso.ia.lockify

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_lock_and_unlock.*

class LockAndUnlockFragment: Fragment(R.layout.activity_lock_and_unlock) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return super.onCreateView(inflater, container, savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        btnLock.setOnClickListener{
            Toast.makeText(getActivity(), "Door is now locked!", Toast.LENGTH_SHORT).show()
        }
        btnUnlock.setOnClickListener{
            Toast.makeText(getActivity(), "Door is now unlocked!", Toast.LENGTH_SHORT).show()
        }
        super.onViewCreated(view, savedInstanceState)
    }


}