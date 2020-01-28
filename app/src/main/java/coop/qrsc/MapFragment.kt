package coop.qrsc

import android.util.Log
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

import coop.qrsc.LoginViewModel.AuthenticationState.*


class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val viewModel: LoginViewModel by navGraphViewModels(R.id.login_graph)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        val view: View = inflater.inflate(R.layout.map_fragment, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_dest) as SupportMapFragment
        mapFragment.getMapAsync(this)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()


        viewModel.authenticationState.observe(
            viewLifecycleOwner,
            Observer { authenticationState ->
                // TODO catch null pointer exception here as the !! operator will generate an NPE ??
                when (authenticationState) {
                    AUTHENTICATED -> navController.navigate(R.id.map_dest)
                    UNAUTHENTICATED -> navController.navigate(R.id.logIn_dest)
                    // TODO check redirect to specfic destination to handle auth pb
                    INVALID_AUTHENTICATION -> navController.navigate(
                        R.id.home_dest
                    )
                }
            })
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }


}

