package coop.qrsc

import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.databinding.DataBindingUtil.setContentView

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.emailpassword_fragment.*


class EmailPasswordFragment : Fragment(), View.OnClickListener {

    private val viewModel by viewModels<LoginViewModel>(::MainActivity) // TODO CHECK HERE IF CORRECT???
    private lateinit var auth: FirebaseAuth


    // TODO WHY THIS VAR HERE?? duplicate with authentication state >>> and why set to false???
    var isAuthenticated: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.emailpassword_fragment, container, false) // attach ? false
    }

    // TODO CHECK LIFECYCLE OF FRAGMENTS !!!
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Buttons
        emailSignInButton.setOnClickListener(this)
        emailSignUpButton.setOnClickListener(this)
        signOutButton.setOnClickListener(this)
        verifyEmailButton.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun signUp(email: String, password: String) {
        Log.d(TAG, "createAccount:$email")
        if (!validateForm()) {
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this.requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser

                    // TODO same here????
                    isAuthenticated = true
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        requireContext(), "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
            }
    }


    // TODO make login() generic and decouple from specific fragment implementations and avoid back and forth communications
//
//    fun logIn(email: String, password: String) {
//        Log.d(TAG, "signIn:$email")
//        if (!validateForm()) {
//            return
//        }
//        viewModel.authenticate(email, password)
//    }


    fun signIn(email: String, password: String) {
        Log.d(TAG, "signIn:$email")
        if (!validateForm()) {
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this.requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    isAuthenticated = true

                    // We set the auth state value to allow broadcast
                    viewModel.authenticationState.value =
                        LoginViewModel.AuthenticationState.AUTHENTICATED
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    // we set the auth state value to allow broadcast

                    // TODO > we set the auth state value to allow broadcast
                    viewModel.authenticationState.value =
                        LoginViewModel.AuthenticationState.UNAUTHENTICATED


                    Toast.makeText(
                        requireContext(), "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
                if (!task.isSuccessful) {
                    // TODO > we set the auth state value to allow broadcast
                    viewModel.authenticationState.value =
                        LoginViewModel.AuthenticationState.INVALID_AUTHENTICATION

                    status.setText(R.string.auth_failed)
                }
            }
    }

    private fun signOut() {
        auth.signOut()
        isAuthenticated = false
        updateUI(null)

    }


    private fun sendEmailVerification() {
        verifyEmailButton.isEnabled = false

        val user = auth.currentUser
        user?.sendEmailVerification()
            ?.addOnCompleteListener(requireActivity()) { task ->
                // [START_EXCLUDE]
                // Re-enable button
                verifyEmailButton.isEnabled = true

                if (task.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        "Verification email sent to ${user.email} ",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Log.e(TAG, "sendEmailVerification", task.exception)
                    Toast.makeText(
                        requireContext(),
                        "Failed to send verification email.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }


    private fun validateForm(): Boolean {
        var valid = true

        val email = fieldEmail.text.toString()
        if (TextUtils.isEmpty(email)) {
            fieldEmail.error = "Required."
            valid = false
        } else {
            fieldEmail.error = null
        }

        val password = fieldPassword.text.toString()
        if (TextUtils.isEmpty(password)) {
            fieldPassword.error = "Required."
            valid = false
        } else {
            fieldPassword.error = null
        }

        return valid
    }


    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            status.text = getString(
                R.string.emailpassword_status_fmt,
                user.email, user.isEmailVerified
            )
            detail.text = getString(R.string.firebase_status_fmt, user.uid)

            emailPasswordButtons.visibility = View.GONE
            emailPasswordFields.visibility = View.GONE
            signedInButtons.visibility = View.VISIBLE

            verifyEmailButton.isEnabled = !user.isEmailVerified

        } else {
            status.setText(R.string.signed_out)
            detail.text = null

            emailPasswordButtons.visibility = View.VISIBLE
            emailPasswordFields.visibility = View.VISIBLE
            signedInButtons.visibility = View.GONE
        }
    }


    override fun onClick(v: View) {
        val i = v.id
        when (i) {
            R.id.emailSignUpButton -> signUp(
                fieldEmail.text.toString(),
                fieldPassword.text.toString()
            )
//            R.id.emailSignInButton -> logIn(
//                fieldEmail.text.toString(),
//                fieldPassword.text.toString()
//            )
            R.id.emailSignInButton -> signIn(
                fieldEmail.text.toString(),
                fieldPassword.text.toString()
            )
            R.id.signOutButton -> signOut()
            R.id.verifyEmailButton -> sendEmailVerification()
        }
    }

    companion object {
        private const val TAG = "EmailPassword"
    }

}
