package com.example.medicalcounselling.fragments
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.inflate
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicalcounselling.Authentication.AuthenticationPageActivity
import com.example.medicalcounselling.Dao.PostsDao
import com.example.medicalcounselling.PostsAdapter
import com.example.medicalcounselling.R
import com.example.medicalcounselling.databinding.FragmentHomeBinding
import com.example.medicalcounselling.models.Posts
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment(),PostsAdapter.IPostAdapter {
    lateinit var auth:FirebaseAuth
    lateinit var adapter: PostsAdapter
    lateinit var postDao: PostsDao

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding: FragmentHomeBinding = inflate(
            inflater, R.layout.fragment_home, container, false)
        binding.floatingActionButton.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_homeFragment_to_createPostsFragment)
        )
        auth = FirebaseAuth.getInstance()
        val recyclerView =binding.recyclerView
        setUpRecyclerView(recyclerView)
        setHasOptionsMenu(true)

        return binding.root
    }
    override fun onStart(){
        super.onStart()
        if(auth.currentUser==null){
            startActivity(Intent(context, AuthenticationPageActivity::class.java))
        }
        adapter.startListening()
    }
    override fun onStop(){
        super.onStop()
        adapter.stopListening()
    }
    private fun setUpRecyclerView(recyclerView: RecyclerView) {
        postDao = PostsDao()
        val postCollection =postDao.postCollection
        val query = postCollection.orderBy("createdAt", Query.Direction.DESCENDING)
        val recyclerOptions = FirestoreRecyclerOptions.Builder<Posts>().setQuery(query,Posts::class.java).build()

        adapter = PostsAdapter(recyclerOptions,this)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter =adapter

    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu,menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_logout-> {
                auth.signOut()
                startActivity(Intent(context, AuthenticationPageActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }



    override fun OnClickLikeButton(postId: String) {
        postDao.updateLikes(postId)
    }

}