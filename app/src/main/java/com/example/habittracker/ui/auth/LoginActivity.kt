package com.example.habittracker.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.habittracker.MainActivity
import com.example.habittracker.data.local.DatabaseProvider
import com.example.habittracker.data.repository.UserRepository
import com.example.habittracker.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Manual DI
        val dao = DatabaseProvider.getDatabase(this).userDao()
        val repository = UserRepository(dao)
        viewModel = AuthViewModel(repository)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmailLogin.text.toString().trim()
            val pass = binding.etPasswordLogin.text.toString().trim()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập Email và Mật khẩu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.login(email, pass)
        }

        binding.tvGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun observeViewModel() {
        viewModel.loginResult.observe(this) { result ->
            result.onSuccess { user ->
                Toast.makeText(this, "Xin chào, ${user.name}!", Toast.LENGTH_SHORT).show()
                // Lưu trạng thái đăng nhập vào SharedPreferences (nếu cần) tại đây

                // Chuyển sang màn hình chính
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // Đóng LoginActivity để user không back lại được
            }.onFailure { error ->
                Toast.makeText(this, "Đăng nhập thất bại: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}