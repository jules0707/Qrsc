package coop.qrsc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation

class HomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.I_am_customer_button)?.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.next_action_customer_button, null)
        )
        view.findViewById<Button>(R.id.I_am_driver_button)?.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.next_action_driver_button, null)
        )
    }
}
