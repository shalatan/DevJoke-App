package com.shalatan.devjoke.ui.submitJoke

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shalatan.devjoke.data.Joke
import com.shalatan.devjoke.databinding.FragmentSubmitJokeBinding

class SubmitJokeFragment : Fragment() {

    private lateinit var binding: FragmentSubmitJokeBinding
    private var jokeNumber = 1001
    private var isConditionAccepted = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val animation =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation

        binding = FragmentSubmitJokeBinding.inflate(inflater)
        val db = Firebase.firestore
        binding.postJokeButton.setOnClickListener {
            jokeNumber++
            val jokeText = binding.postJokeEditText.text.toString()
            val joke = Joke(jokeNumber, jokeText)
            db.collection("jokes").document(jokeNumber.toString()).set(joke).addOnSuccessListener {
                Log.e("JOKE UPLOADING : ", "SUCCESSFUL")
                Toast.makeText(requireContext(), "Thank You for submitting", Toast.LENGTH_SHORT)
                    .show()
            }.addOnFailureListener {
                Log.e("JOKE UPLOADING : ", "FAILED")
                Toast.makeText(
                    requireContext(),
                    "Ooops !! There was some error",
                    Toast.LENGTH_SHORT
                ).show()
            }
            binding.postJokeEditText.text = null
            binding.postJokeEditText.hideKeyboard()
        }

        binding.postJokeEditText.doOnTextChanged { newJoke, start, before, count ->
            binding.exampleCardTextView.text = newJoke
        }

        val cardView = binding.exampleCardView

        binding.acceptButton.setOnClickListener {
            if (!isConditionAccepted) {
                isConditionAccepted = true
                val oa1 = ObjectAnimator.ofFloat(cardView, "scaleX", 1f, 0f)
                val oa2 = ObjectAnimator.ofFloat(cardView, "scaleX", 0f, 1f)
                oa1.interpolator = DecelerateInterpolator()
                oa2.interpolator = AccelerateDecelerateInterpolator()
                oa1.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        cardView.elevation = 8f
                        binding.acceptButton.isEnabled = false
                        binding.postJokeButton.isEnabled = true
                        binding.postJokeEditText.isEnabled = true
                        binding.exampleCardTextView.text = null
                        oa2.start()
                    }
                })
                oa1.start()
            }
        }
        return binding.root
    }

    /**
     * hide soft-keyboard
     */
    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

}