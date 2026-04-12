import { Component, OnInit, inject } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../auth.service';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login implements OnInit {
  private authService = inject(AuthService);
  private fb = inject(FormBuilder);

  loginForm!: FormGroup;
  submitted = false;
  isLoading = false;
  errorMessage: string | null = null;

  constructor(private formBuilder: FormBuilder) {}

  ngOnInit(): void {
    this.loginForm = this.formBuilder.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required]],
    });
  }

  get f() {
    return this.loginForm.controls;
  }

  onSubmit(): void {
  this.submitted = true;
  this.errorMessage = null;

  if (this.loginForm.invalid) return;

  this.isLoading = true;

  this.authService.login(this.loginForm.value).subscribe({
    next: (response) => {
      console.log('Login response:', response);
      this.isLoading = false;
    },
    error: (err) => {
      console.error('Login error:', err);
      this.errorMessage = err.error?.messages?.[0] ?? 'Login failed';
      this.isLoading = false;
    }
  });
}

  resetForm(): void {
    this.submitted = false;
    this.errorMessage = null;
    this.loginForm.reset();
  }
}