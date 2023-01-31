package com.crayosa.surveil.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import com.crayosa.surveil.R
import com.crayosa.surveil.databinding.FragmentClassRoomInfoBinding

class ClassRoomInfoFragment : Fragment() {
    private val args : ClassRoomInfoFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.
            inflate<FragmentClassRoomInfoBinding>(
                inflater,R.layout.fragment_class_room_info, container, false
            )
        binding.copyCode.setOnClickListener {
            val clipboard = requireActivity()
                .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText("simple text", args.classroom.id)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(requireContext(), "Code Copied to clipboard", Toast.LENGTH_SHORT).show()
        }
        binding.shareCode.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, args.classroom.id)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)

        }
        return binding.root
    }
}