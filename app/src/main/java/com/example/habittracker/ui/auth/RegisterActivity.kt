package com.example.habittracker.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.habittracker.data.local.DatabaseProvider
import com.example.habittracker.data.repository.UserRepository
import com.example.habittracker.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Manual Dependency Injection (Đơn giản hoá)
        val dao = DatabaseProvider.getDatabase(this).userDao()
        val repository = UserRepository(dao)
        viewModel = AuthViewModel(repository)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            val name = binding.etNameRegister.text.toString().trim()
            val email = binding.etEmailRegister.text.toString().trim()
            val pass = binding.etPasswordRegister.text.toString().trim()
            val confirmPass = binding.etConfirmPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pass != confirmPass) {
                Toast.makeText(this, "Mật khẩu nhập lại không khớp", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Gọi ViewModel
            viewModel.register(name, email, pass)
        }

        binding.tvGoToLogin.setOnClickListener {
            finish() // Quay lại màn hình Login
        }
    }

    private fun observeViewModel() {
        viewModel.registerResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                // Chuyển về Login
                finish()
            }.onFailure { error ->
                Toast.makeText(this, "Lỗi: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}