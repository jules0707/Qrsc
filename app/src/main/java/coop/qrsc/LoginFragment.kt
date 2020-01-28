package coop.qrsc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth

import coop.qrsc.LoginViewModel.AuthenticationState.*

class LoginFragment : Fragment() {

    //private val viewModel: LoginViewModel by activityViewModels()


    private lateinit var fieldEmail: EditText
    private lateinit var fieldPassword: EditText
    private lateinit var signInButton: Button
    private lateinit var signUpButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        val viewModel by viewModels<LoginViewModel>(::MainActivity)

        fieldEmail = view.findViewById(R.id.fieldEmail)
        fieldPassword = view.findViewById(R.id.fieldPassword)
        signInButton = view.findViewById(R.id.signInButton)
        signUpButton = view.findViewById(R.id.signUpButton)


        signInButton.setOnClickListener {
            viewModel.authenticate(
                fieldEmail.text.toString(),
                fieldPassword.text.toString()
            )
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.refuseAuthentication()
            navController.popBackStack(R.id.home_dest, false)
        }

        viewModel.authenticationState.observe(viewLifecycleOwner, Observer { authenticationState ->
            when (authenticationState) {
                AUTHENTICATED -> navController.navigate(R.id.map_dest)
                UNAUTHENTICATED -> navController.navigate(R.id.logIn_dest)
                INVALID_AUTHENTICATION -> {
                    showErrorMessage()
                    navController.popBackStack()
                }
            }
        })
    }

    private fun showErrorMessage() {
        Toast.makeText(
            requireContext(), "Authentication failed.",
            Toast.LENGTH_SHORT
        ).show()
    }
}