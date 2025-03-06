package com.example.ggconnect.ui.search

import SearchAdapter
import SearchItem
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ggconnect.data.firebase.FirestoreService
import com.example.ggconnect.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val firestoreService = FirestoreService()
    private val searchAdapter = SearchAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        setupUI()
        return binding.root
    }

    private fun setupUI() {
        binding.searchRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchAdapter
        }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handleSearchInput(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun handleSearchInput(query: String) {
        if (query.isBlank()) {
            searchAdapter.updateItems(emptyList())
            return
        }

        firestoreService.searchUsersAndGames(query) { users, games ->
            val searchItems = mutableListOf<SearchItem>()

            searchItems.addAll(users.map { SearchItem.UserItem(it) })
            searchItems.addAll(games.map { SearchItem.GameItem(it) })

            searchAdapter.updateItems(searchItems)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
