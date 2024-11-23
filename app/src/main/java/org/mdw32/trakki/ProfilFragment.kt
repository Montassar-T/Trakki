package org.mdw32.trakki

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

class ProfilFragment : Fragment() {


    private lateinit var settings: LinearLayout
    private lateinit var help: LinearLayout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        settings = view.findViewById(R.id.settings)
        help = view.findViewById(R.id.help)


        settings.setOnClickListener {
            val intent = Intent(requireActivity(), SettingsActivity::class.java)
            startActivity(intent)
        }

        help.setOnClickListener {
            val intent = Intent(requireActivity(), HelpActivity::class.java)
            startActivity(intent)
        }
    }

}