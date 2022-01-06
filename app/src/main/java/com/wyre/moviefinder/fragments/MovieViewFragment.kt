package com.wyre.moviefinder.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.view.SimpleDraweeView
import com.wyre.moviefinder.R
import com.wyre.moviefinder.models.Movie
import com.wyre.moviefinder.models.MovieResponse
import com.wyre.moviefinder.models.ResponseState
import com.wyre.moviefinder.networking.MovieEndpoints
import com.wyre.moviefinder.networking.MovieNetworkService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat


/**
 * A simple [Fragment] subclass.
 * Use the [MovieViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MovieViewFragment : Fragment() {

    private val movieViewModel: MovieViewViewModel by viewModels()
    private lateinit var movieRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_movie_view, container, false)
        movieRecyclerView = view.findViewById(R.id.movie_item_list)
        movieRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Observe changes to the movielist
        movieViewModel.movieListLiveData.observe(viewLifecycleOwner, {
            movieRecyclerView.adapter = MovieAdapter(it)
        })
        //Observe changes to the loading state
        movieViewModel.responseStateLiveData.observe(viewLifecycleOwner, {
            it.let {
                if (it == ResponseState.FAILURE) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.fetch_failure_toast),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
        //Fetch the movies
        movieViewModel.fetchMovieData()
    }

    companion object {
        @JvmStatic
        fun newInstance() = MovieViewFragment()
    }


}

class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val movieName: TextView = itemView.findViewById(R.id.tv_name)
    val releaseDate: TextView = itemView.findViewById(R.id.tv_date)
    val description: TextView = itemView.findViewById(R.id.tv_description)
    val artwork: SimpleDraweeView = itemView.findViewById(R.id.image_artwork)

    fun bindView(movie: Movie) {
        movieName.text = movie.trackName
        releaseDate.text = SimpleDateFormat("MM-dd-yyyy").format(movie.releaseDate)
        description.text = movie.shortDescription
        artwork.setImageURI(movie.artworkUrl100)
    }

}

class MovieAdapter(val movieList: List<Movie>) :
    RecyclerView.Adapter<MovieViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.movie_item, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) =
        holder.bindView(movieList[position])

    override fun getItemCount() = movieList.size


}


class MovieViewViewModel : ViewModel() {
    private val movieNetworkService = MovieNetworkService()

    val movieListLiveData: MutableLiveData<List<Movie>> by lazy {
        MutableLiveData<List<Movie>>()
    }

    val responseStateLiveData: MutableLiveData<ResponseState> by lazy {
        //Start off in the default waiting for data state
        MutableLiveData<ResponseState>(ResponseState.WAITING)
    }

    fun fetchMovieData() {
        val request = movieNetworkService.buildService(MovieEndpoints::class.java)
        val call = request.getMovies()
        call.enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        movieListLiveData.value = it.results

                    }
                } else {
                    responseStateLiveData.value = ResponseState.FAILURE
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                responseStateLiveData.value = ResponseState.FAILURE
                t.printStackTrace()
            }

        })
    }


}