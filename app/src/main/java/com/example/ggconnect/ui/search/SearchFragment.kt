import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ggconnect.data.firebase.FirestoreService
import com.example.ggconnect.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val firestoreService = FirestoreService()
    private val searchAdapter = SearchResultAdapter(
        onAddFriendClick = { userId ->
            Toast.makeText(requireContext(), "Add friend with ID: $userId", Toast.LENGTH_SHORT).show()
            // Add Firestore logic to send a friend request
        },
        onLikeGameClick = { gameId ->
            Toast.makeText(requireContext(), "Liked game with ID: $gameId", Toast.LENGTH_SHORT).show()
            // Add Firestore logic to like a game
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        setupRecyclerView()
        searchForItems("searchQuery")
        return binding.root
    }

    private fun setupRecyclerView() {
        binding.searchRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchAdapter
        }
    }

    private fun searchForItems(query: String) {
        firestoreService.searchUsersAndGames(query) { users, games ->
            val searchItems = mutableListOf<SearchItem>()
            searchItems.addAll(users)
            searchItems.addAll(games)
            searchAdapter.updateItems(searchItems)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
