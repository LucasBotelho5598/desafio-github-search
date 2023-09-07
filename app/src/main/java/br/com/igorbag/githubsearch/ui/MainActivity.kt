package br.com.igorbag.githubsearch.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.data.GitHubService
import br.com.igorbag.githubsearch.domain.Repository
import br.com.igorbag.githubsearch.ui.adapter.RepositoryAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var nomeUsuario: EditText
    private lateinit var btnConfirmar: Button
    private lateinit var listaRepositories: RecyclerView
    private lateinit var githubApi: GitHubService
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupView()
        setupListeners()
        setupRetrofit()
        sharedPreferences = getSharedPreferences("YourPreferencesName", Context.MODE_PRIVATE)
        showUserName()
    }

    private fun setupView() {
        nomeUsuario = findViewById(R.id.et_nome_usuario)
        btnConfirmar = findViewById(R.id.btn_confirmar)
        listaRepositories = findViewById(R.id.rv_lista_repositories)
    }

    private fun setupListeners() {
        btnConfirmar.setOnClickListener {
            val username = nomeUsuario.text.toString()
            if (username.isBlank()) {
                Toast.makeText(this, "Digite um nome de usuário válido", Toast.LENGTH_SHORT).show()
            } else {
                saveUserLocal(username)
                getAllReposByUserName(username)
            }
        }
    }

    private fun saveUserLocal(nomeUsuario: String) {
        sharedPreferences.edit().putString(getString(R.string.nome_usuario), nomeUsuario).apply()
    }

    private fun showUserName() {
        val nomeUsuarioSalvo = sharedPreferences.getString(getString(R.string.nome_usuario), "")
        nomeUsuario.setText(nomeUsuarioSalvo)
    }

    private fun setupRetrofit() {
        val baseUrl = "https://api.github.com/"

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        githubApi = retrofit.create(GitHubService::class.java)
    }

    private fun getAllReposByUserName(username: String) {
        githubApi.getAllRepositoriesByUser(username)
            .enqueue(object : Callback<List<Repository>> {
                override fun onResponse(
                    call: Call<List<Repository>>,
                    response: Response<List<Repository>>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            setupAdapter(it)
                        }
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            getString(R.string.error),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<Repository>>, t: Throwable) {
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.error),
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun setupAdapter(list: List<Repository>) {
        val repositoryAdapter = RepositoryAdapter(list)
        listaRepositories.adapter = repositoryAdapter

        repositoryAdapter.gitItemLister = { repository ->
            openBrowser(repository.htmlUrl)
        }

        repositoryAdapter.btnShareLister = { repository ->
            shareRepositoryLink(repository.htmlUrl)
        }
    }

    private fun shareRepositoryLink(urlRepository: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, urlRepository)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun openBrowser(urlRepository: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(urlRepository))
        startActivity(browserIntent)
    }
}
